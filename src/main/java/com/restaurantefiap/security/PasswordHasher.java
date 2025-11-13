// domain/security/PasswordHasher.java
package com.restaurantefiap.security;

public interface PasswordHasher {
    String hash(String raw);
}
