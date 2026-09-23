package order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // How long to wait while trying to connect to User Service
        factory.setConnectTimeout(3000); // 3 seconds

        // Once connected, how long to wait for User Service to respond
        factory.setReadTimeout(5000); // 5 seconds

        return new RestTemplate(factory);
    }
}