package order_service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import order_service.dto.UserResponse;

@Component
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    // Matches User Service's actual endpoint: GET /users/{id}
    private static final String USER_SERVICE_URL = "http://localhost:8081/users/";

    // Kept for the simple exists-check used elsewhere if needed
    public boolean userExists(Long userId) {
        try {
            restTemplate.getForObject(USER_SERVICE_URL + userId, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    // Fetches full user details for the combined response (Step 7)
    public UserResponse getUserDetails(Long userId) {
        try {
            return restTemplate.getForObject(USER_SERVICE_URL + userId, UserResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("User not found with id: " + userId);
        } catch (ResourceAccessException e) {
            // This fires when User Service is not reachable at all (Step 9 — test failure)
            throw new RuntimeException("User Service is unavailable. Please try again later.");
        }
    }
}