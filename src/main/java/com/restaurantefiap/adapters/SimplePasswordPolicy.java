package com.restaurantefiap.adapters;

import org.springframework.stereotype.Component;

import com.restaurantefiap.security.PasswordPolicy;

@Component
public class SimplePasswordPolicy implements PasswordPolicy {
    @Override public void validateOrThrow(String raw) {
        if (raw.length() < 8) throw new IllegalArgumentException("Mínimo 8 caracteres.");
        if (!raw.matches(".*\\d.*")) throw new IllegalArgumentException("Precisa de dígito.");
        if (!raw.matches(".*[A-Z].*")) throw new IllegalArgumentException("Precisa de maiúscula.");
    }
}