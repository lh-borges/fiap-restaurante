package com.restaurantefiap.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para {@link JwtService}.
 * <p>Valida geração, extração de claims e validação de tokens JWT,
 * garantindo a segurança do processo de autenticação.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    /**
     * Chave secreta de 256 bits codificada em Base64 para testes.
     * IMPORTANTE: Esta chave é apenas para testes, nunca usar em produção.
     */
    private static final String SECRET_BASE64 = "dGVzdGUtc2VjcmV0LWtleS0yNTYtYml0cy1wYXJhLWp3dA==";

    /**
     * Tempo de expiração padrão para testes: 1 hora (3600000ms).
     */
    private static final long EXPIRATION_MS = 3600000L;

    @Mock
    private UserDetails userDetails;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretBase64", SECRET_BASE64);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    // ========================================================================
    // GERAÇÃO DE TOKEN
    // ========================================================================

    @Nested
    @DisplayName("Geração de Token")
    class GeracaoTokenTests {

        /**
         * Verifica que token é gerado corretamente para usuário válido.
         * Token deve ser não nulo, não vazio e conter três partes (header.payload.signature).
         */
        @Test
        @DisplayName("Deve gerar token válido quando UserDetails válido")
        void generateToken_quandoUserDetailsValido_deveGerarTokenValido() {
            // Arrange
            when(userDetails.getUsername()).thenReturn("usuario.teste");

            // Act
            String token = jwtService.generateToken(userDetails);

            // Assert
            assertNotNull(token);
            assertFalse(token.isBlank());
            assertEquals(3, token.split("\\.").length, "Token JWT deve ter 3 partes");
        }

        /**
         * Verifica que tokens gerados em momentos diferentes são distintos.
         * NOTA: Teste com delay.
         */
        @Test
        @DisplayName("Deve gerar tokens diferentes quando gerados em momentos distintos")
        void generateToken_quandoGeradosEmMomentosDistintos_deveGerarTokensDiferentes() throws InterruptedException {
            // Arrange
            when(userDetails.getUsername()).thenReturn("usuario.teste");

            // Act
            String token1 = jwtService.generateToken(userDetails);
            Thread.sleep(1100); // aguarda 1.1 segundo para mudar o timestamp
            String token2 = jwtService.generateToken(userDetails);

            // Assert
            assertNotEquals(token1, token2, "Tokens gerados em momentos diferentes devem ser únicos");
        }
    }

    // ========================================================================
    // EXTRAÇÃO DE CLAIMS
    // ========================================================================

    @Nested
    @DisplayName("Extração de Claims")
    class ExtracaoClaimsTests {

        /**
         * Verifica que username é extraído corretamente do token.
         */
        @Test
        @DisplayName("Deve extrair username do token válido")
        void extractUsername_quandoTokenValido_deveRetornarUsername() {
            // Arrange
            String usernameEsperado = "usuario.teste";
            when(userDetails.getUsername()).thenReturn(usernameEsperado);
            String token = jwtService.generateToken(userDetails);

            // Act
            String usernameExtraido = jwtService.extractUsername(token);

            // Assert
            assertEquals(usernameEsperado, usernameExtraido);
        }

        /**
         * Verifica que data de expiração é extraída corretamente.
         */
        @Test
        @DisplayName("Deve extrair data de expiração do token")
        void extractClaim_quandoTokenValido_deveExtrairExpiracao() {
            // Arrange
            when(userDetails.getUsername()).thenReturn("usuario.teste");
            String token = jwtService.generateToken(userDetails);
            Date agora = new Date();

            // Act
            Date expiracao = jwtService.extractClaim(token, claims -> claims.getExpiration());

            // Assert
            assertNotNull(expiracao);
            assertTrue(expiracao.after(agora), "Data de expiração deve ser no futuro");
        }

        /**
         * Verifica que token malformado lança exceção ao extrair claims.
         */
        @Test
        @DisplayName("Deve lançar exceção quando token malformado")
        void extractUsername_quandoTokenMalformado_deveLancarExcecao() {
            // Arrange
            String tokenInvalido = "token.invalido.malformado";

            // Act & Assert
            assertThrows(
                    JwtException.class,
                    () -> jwtService.extractUsername(tokenInvalido)
            );
        }

        /**
         * Verifica que token com assinatura inválida é rejeitado.
         */
        @Test
        @DisplayName("Deve lançar exceção quando assinatura do token é inválida")
        void extractUsername_quandoAssinaturaInvalida_deveLancarExcecao() {
            // Arrange
            when(userDetails.getUsername()).thenReturn("usuario.teste");
            String tokenOriginal = jwtService.generateToken(userDetails);
            String tokenAdulterado = tokenOriginal.substring(0, tokenOriginal.length() - 5) + "XXXXX";

            // Act & Assert
            assertThrows(
                    JwtException.class,
                    () -> jwtService.extractUsername(tokenAdulterado)
            );
        }
    }

    // ========================================================================
    // VALIDAÇÃO DE TOKEN
    // ========================================================================

    @Nested
    @DisplayName("Validação de Token")
    class ValidacaoTokenTests {

        /**
         * Verifica que token válido e não expirado passa na validação.
         */
        @Test
        @DisplayName("Deve retornar true quando token válido e não expirado")
        void isTokenValid_quandoTokenValidoENaoExpirado_deveRetornarTrue() {
            // Arrange
            String username = "usuario.teste";
            when(userDetails.getUsername()).thenReturn(username);
            String token = jwtService.generateToken(userDetails);

            // Act
            boolean resultado = jwtService.isTokenValid(token, userDetails);

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que validação é case-insensitive para username.
         */
        @Test
        @DisplayName("Deve validar token ignorando case do username")
        void isTokenValid_quandoUsernameDiferenteCaseValido_deveRetornarTrue() {
            // Arrange
            when(userDetails.getUsername())
                    .thenReturn("usuario.teste")  // para gerar o token
                    .thenReturn("USUARIO.TESTE"); // para validar (uppercase)
            String token = jwtService.generateToken(userDetails);

            // Act
            boolean resultado = jwtService.isTokenValid(token, userDetails);

            // Assert
            assertTrue(resultado, "Validação deve ser case-insensitive");
        }

        /**
         * Verifica que token expirado lança exceção ao tentar validar.
         * A biblioteca JJWT rejeita tokens expirados durante o parse.
         */
        @Test
        @DisplayName("Deve lançar exceção quando token expirado")
        void isTokenValid_quandoTokenExpirado_deveLancarExcecao() {
            // Arrange
            String tokenExpirado = criarTokenExpirado("usuario.teste");

            // Act & Assert
            assertThrows(
                    ExpiredJwtException.class,
                    () -> jwtService.isTokenValid(tokenExpirado, userDetails),
                    "Token expirado deve lançar ExpiredJwtException"
            );
        }

        /**
         * Verifica que token de outro usuário falha na validação.
         */
        @Test
        @DisplayName("Deve retornar false quando username não corresponde")
        void isTokenValid_quandoUsernameNaoCorresponde_deveRetornarFalse() {
            // Arrange
            when(userDetails.getUsername())
                    .thenReturn("usuario.original")  // para gerar o token
                    .thenReturn("outro.usuario");    // para validar
            String token = jwtService.generateToken(userDetails);

            // Act
            boolean resultado = jwtService.isTokenValid(token, userDetails);

            // Assert
            assertFalse(resultado, "Token de outro usuário não deve ser válido");
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Cria um token JWT já expirado para testes de validação.
     *
     * @param username o subject do token
     * @return token JWT com data de expiração no passado
     */
    private String criarTokenExpirado(String username) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_BASE64);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date agora = new Date();
        Date expiracaoPassada = new Date(agora.getTime() - 10000); // 10 segundos atrás

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(agora.getTime() - 20000)) // emitido 20s atrás
                .setExpiration(expiracaoPassada)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}