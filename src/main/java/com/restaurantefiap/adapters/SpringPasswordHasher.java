package com.restaurantefiap.adapters;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import com.restaurantefiap.security.PasswordHasher;

// infrastructure/security/SpringPasswordHasher.java
@Component
@RequiredArgsConstructor
public class SpringPasswordHasher implements PasswordHasher {
    private final PasswordEncoder encoder;     // BCrypt/Argon2 do Spring
    @Override public String hash(String raw) { return encoder.encode(raw); }
}


