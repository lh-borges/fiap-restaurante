// domain/security/PasswordPolicy.java
package com.restaurantefiap.security;

public interface PasswordPolicy {
    void validateOrThrow(String rawPassword);
}
