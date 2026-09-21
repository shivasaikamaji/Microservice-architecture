package user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;
import io.jsonwebtoken.Claims;
import user_service.dto.LoginRequest;
import user_service.dto.RegisterRequest;
import user_service.dto.RefreshRequest;
import user_service.entity.User;
import user_service.repository.UserRepository;
import user_service.config.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Step 5 & 6 — Register + duplicate check
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        savedUser.setPassword(null); // never send the hash back

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Step 7, 8 & 9 — Login + verify credentials + generate JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {

        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .<ResponseEntity<?>>map(user -> {
                    String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
                    String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
                    long accessTokenExpirationSeconds = 900000L / 1000;

                    Map<String, Object> response = new HashMap<>();
                    response.put("accessToken", accessToken);
                    response.put("refreshToken", refreshToken);
                    response.put("tokenType", "Bearer");
                    response.put("expiresIn", accessTokenExpirationSeconds);

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password"));
    }

    // Step 9 — Refresh access token
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token");
        }

        Claims claims = jwtUtil.extractAllClaims(refreshToken);
        String email = claims.getSubject();

        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(user -> {
                    String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());

                    Map<String, Object> response = new HashMap<>();
                    response.put("accessToken", newAccessToken);
                    response.put("tokenType", "Bearer");
                    response.put("expiresIn", 900000L / 1000);

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User no longer exists"));
    }
}