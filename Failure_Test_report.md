Day 5 — Resilience Capstone and Failure Testing

1. Objective

The objective of this task is to test the reliability and recovery behavior of the microservices architecture under normal conditions, invalid requests, excessive traffic, and service failures.

2. Test Environment

- API Gateway: Port 8080
- User Service: Port 8081
- Product Service: Port 8082
- Order Service: Port 8083
- Tools: Postman, Spring Boot Actuator, Redis

3. Test Results

Test Scenario| Expected Result| Actual Result| Status
Normal order creation| 200 OK| 200 OK| Passed
Invalid quantity| 400 Bad Request| 400 Bad Request| Passed
Duplicate request| 409 Conflict| 409 Conflict| Passed
Rate limit exceeded| 429 Too Many Requests| 429 Too Many Requests| Passed
User Service unavailable| 503 Service Unavailable| 503 Service Unavailable| Passed
User Service recovery| 200 OK| 200 OK| Passed
Correlation ID propagation| Same ID across related logs| Verified| Passed
HTTP request metrics| Metrics available through Actuator| Verified| Passed
Circuit-breaker call metrics| Metrics available through Actuator| Verified| Passed

4. Resilience Mechanisms

Timeouts

Connection and read timeouts prevent requests from waiting indefinitely for downstream services.

Retries with Exponential Backoff

The Order Service retries eligible failed calls with increasing delays to handle temporary failures.

Circuit Breaker

The circuit breaker helps prevent repeated calls to an unavailable service and activates fallback handling.

Idempotency

Repeated requests using an already-used Idempotency-Key are rejected with HTTP 409 Conflict.

Rate Limiting

The API Gateway limits request traffic and returns HTTP 429 Too Many Requests when the configured limit is exceeded.

Distributed Tracing

Correlation IDs help trace requests across services. Structured logs include request details, HTTP status codes, and response duration.

5. Recovery Behavior

When the User Service was stopped, requests requiring user details returned HTTP 503 Service Unavailable. After restarting the User Service and allowing recovery, requests succeeded with HTTP 200 OK.

6. Architectural Trade-offs

- Retries improve resilience but can increase response time during failures.
- Circuit breakers reduce repeated calls to unhealthy services but may temporarily reject requests during recovery.
- Rate limiting protects services from excessive traffic but can reject legitimate requests when the configured limit is exceeded.
- Idempotency prevents repeated order creation for reused keys but requires a consistent key-management strategy.
- Correlation IDs improve troubleshooting but must be propagated consistently across services.

7. Conclusion

The tested resilience mechanisms handled normal requests, invalid inputs, duplicate requests, excessive traffic, and downstream service failures as expected. Recovery behavior and monitoring metrics were also verified.