package order_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    private final RestTemplate restTemplate = new RestTemplate();

    // Check Order Service
    @GetMapping("/orders")
    public String getOrders() {
        return "Order Service is working";
    }

    // Order Service calls User Service
    @GetMapping("/orders/user/{id}")
    public ResponseEntity<String> getUserFromOrderService(@PathVariable Long id) {

        String url = "http://localhost:8081/users/" + id;

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }
}