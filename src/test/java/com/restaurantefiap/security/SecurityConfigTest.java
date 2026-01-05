package com.restaurantefiap.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link SecurityConfig}.
 * <p>Valida configuração dos beans de segurança.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(null, null); // Dependências não usadas nos testes unitários
    }

    @Nested
    @DisplayName("Password Encoder")
    class PasswordEncoderTests {

        /**
         * Verifica que o bean passwordEncoder retorna BCryptPasswordEncoder.
         */
        @Test
        @DisplayName("Deve retornar instância de BCryptPasswordEncoder")
        void passwordEncoder_quandoChamado_deveRetornarBCryptPasswordEncoder() {
            // Act
            PasswordEncoder encoder = securityConfig.passwordEncoder();

            // Assert
            assertNotNull(encoder);
            assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        }

        /**
         * Verifica que o encoder consegue codificar senhas.
         */
        @Test
        @DisplayName("Deve codificar senha corretamente")
        void passwordEncoder_quandoCodificaSenha_deveGerarHash() {
            // Arrange
            PasswordEncoder encoder = securityConfig.passwordEncoder();
            String senhaPlana = "MinhaS3nh@!";

            // Act
            String hash = encoder.encode(senhaPlana);

            // Assert
            assertNotNull(hash);
            assertNotEquals(senhaPlana, hash);
            assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "Deve ser hash BCrypt");
        }

        /**
         * Verifica que o encoder valida senhas corretamente.
         */
        @Test
        @DisplayName("Deve validar senha codificada corretamente")
        void passwordEncoder_quandoValidaSenha_deveRetornarTrue() {
            // Arrange
            PasswordEncoder encoder = securityConfig.passwordEncoder();
            String senhaPlana = "MinhaS3nh@!";
            String hash = encoder.encode(senhaPlana);

            // Act
            boolean matches = encoder.matches(senhaPlana, hash);

            // Assert
            assertTrue(matches);
        }

        /**
         * Verifica que o encoder rejeita senha incorreta.
         */
        @Test
        @DisplayName("Deve rejeitar senha incorreta")
        void passwordEncoder_quandoSenhaIncorreta_deveRetornarFalse() {
            // Arrange
            PasswordEncoder encoder = securityConfig.passwordEncoder();
            String senhaCorreta = "MinhaS3nh@!";
            String senhaErrada = "SenhaErrada123";
            String hash = encoder.encode(senhaCorreta);

            // Act
            boolean matches = encoder.matches(senhaErrada, hash);

            // Assert
            assertFalse(matches);
        }
    }
}