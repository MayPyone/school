package com.school.school.service;

import com.school.school.entity.User;
import com.school.school.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthTokenService {
    private static final Duration TOKEN_TTL = Duration.ofHours(2);
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, TokenRecord> tokens = new ConcurrentHashMap<>();

    public AuthTokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createToken(User user) {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        tokens.put(token, new TokenRecord(user.getId(), Instant.now().plus(TOKEN_TTL)));
        return token;
    }

    public Optional<User> authenticate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        TokenRecord record = tokens.get(token);
        if (record == null) {
            return Optional.empty();
        }

        if (record.expiresAt().isBefore(Instant.now())) {
            tokens.remove(token);
            return Optional.empty();
        }

        return userRepository.findById(record.userId());
    }

    public void revoke(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }

    public Optional<String> extractBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }

    private record TokenRecord(UUID userId, Instant expiresAt) {
    }
}
