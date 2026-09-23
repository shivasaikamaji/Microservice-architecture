package order_service.client;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import order_service.dto.UserResponse;

@Component
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    // Matches User Service's actual endpoint: GET /users/{id}
    private static final String USER_SERVICE_URL = "http://localhost:8081/users/";

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_DELAY_MS = 500; // first retry waits 0.5s, next 1s, next 2s...

    // Kept for the simple exists-check used when creating an order
    public boolean userExists(Long userId) {
        try {
            restTemplate.getForObject(USER_SERVICE_URL + userId, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    // Step 4: @CircuitBreaker wraps this whole method (including the retry loop).
    // "userService" must match the name used in application.properties (Step 5).
    // If the circuit is OPEN, this method body is skipped entirely and
    // getUserDetailsFallback() runs instead - that's the "fail fast" behaviour.
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserDetailsFallback")
    public UserResponse getUserDetails(Long userId) {

        System.out.println("[UserServiceClient] Request started for userId=" + userId);

        long delay = INITIAL_DELAY_MS;
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            if (attempt > 1) {
                System.out.println("[UserServiceClient] Retry attempt " + attempt
                        + " for userId=" + userId);
            }

            try {
                UserResponse user = restTemplate.getForObject(USER_SERVICE_URL + userId, UserResponse.class);
                System.out.println("[UserServiceClient] Request succeeded on attempt " + attempt);
                return user;

            } catch (HttpClientErrorException.NotFound e) {
                System.out.println("[UserServiceClient] Final failure - userId=" + userId + " not found (non-retryable)");
                throw new RuntimeException("User not found with id: " + userId);

            } catch (ResourceAccessException e) {
                Throwable cause = e.getCause();
                boolean isTimeout = cause instanceof SocketTimeoutException;
                boolean isConnectionRefused = cause instanceof ConnectException;

                String reason = isTimeout ? "timeout"
                        : isConnectionRefused ? "connection refused"
                        : "network error";

                if (isTimeout) {
                    lastError = new RuntimeException("User Service took too long to respond. Please try again later.");
                } else if (isConnectionRefused) {
                    lastError = new RuntimeException("User Service is unavailable. Please try again later.");
                } else {
                    lastError = new RuntimeException("Could not reach User Service. Please try again later.");
                }

                if (attempt == MAX_ATTEMPTS) {
                    System.out.println("[UserServiceClient] Final failure after " + attempt
                            + " attempts (" + reason + ")");
                    break;
                } else {
                    System.out.println("[UserServiceClient] Retry failure on attempt " + attempt
                            + " (" + reason + ")");
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                delay *= 2;
            }
        }

        throw lastError;
    }

    // Step 4/6: fallback method. Signature must match the original method,
    // plus one extra Throwable parameter at the end.
    // This runs in TWO cases:
    //   1. Circuit is OPEN -> getUserDetails() body never even runs, this fires immediately
    //   2. Circuit is CLOSED/HALF_OPEN but getUserDetails() still threw after all retries
    //
    // "User not found" is a genuine, permanent error (not a service-availability
    // problem), so it is rethrown as-is rather than treated as a fallback case.
    // Everything else (timeout, connection refused, circuit open) means we cannot
    // tell whether the user exists or not - return null so the caller can degrade
    // gracefully instead of failing the whole request (Step 6).
    public UserResponse getUserDetailsFallback(Long userId, Throwable t) {

        if (t.getMessage() != null && t.getMessage().contains("not found")) {
            throw new RuntimeException(t.getMessage());
        }

        System.out.println("[UserServiceClient] Circuit breaker fallback triggered for userId=" + userId
                + " (" + t.getClass().getSimpleName() + ")");
        return null;
    }
}