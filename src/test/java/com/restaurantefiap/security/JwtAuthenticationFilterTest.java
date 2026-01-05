package com.restaurantefiap.security;

import com.restaurantefiap.entities.usuario.UserPrincipal;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;
import com.restaurantefiap.service.JwtService;
import com.restaurantefiap.util.UsuarioTestBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link JwtAuthenticationFilter}.
 * <p>Valida o comportamento do filtro de autenticação JWT:
 * extração de token, validação e configuração do SecurityContext.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        Usuario usuario = UsuarioTestBuilder.criarUsuarioValido();
        usuario.setRole(Role.CLIENTE);
        userPrincipal = new UserPrincipal(usuario);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ========================================================================
    // REQUISIÇÃO SEM TOKEN
    // ========================================================================

    @Nested
    @DisplayName("Requisições sem token")
    class RequisicoesSemTokenTests {

        /**
         * Verifica que requisição sem header Authorization continua normalmente.
         */
        @Test
        @DisplayName("Deve continuar cadeia quando header Authorization ausente")
        void doFilterInternal_quandoSemHeaderAuthorization_deveContinuarCadeia() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verifyNoInteractions(jwtService, userDetailsService);
        }

        /**
         * Verifica que requisição com header sem prefixo Bearer continua sem autenticar.
         */
        @Test
        @DisplayName("Deve continuar cadeia quando header não começa com Bearer")
        void doFilterInternal_quandoHeaderSemPrefixoBearer_deveContinuarCadeia() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verifyNoInteractions(jwtService, userDetailsService);
        }

        /**
         * Verifica que header Authorization vazio é tratado corretamente.
         */
        @Test
        @DisplayName("Deve continuar cadeia quando header Authorization vazio")
        void doFilterInternal_quandoHeaderAuthorizationVazio_deveContinuarCadeia() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("");

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    // ========================================================================
    // REQUISIÇÃO COM TOKEN VÁLIDO
    // ========================================================================

    @Nested
    @DisplayName("Requisições com token válido")
    class RequisicoesComTokenValidoTests {

        /**
         * Verifica que token válido autentica o usuário no SecurityContext.
         */
        @Test
        @DisplayName("Deve autenticar usuário quando token válido")
        void doFilterInternal_quandoTokenValido_deveAutenticarUsuario() throws ServletException, IOException {
            // Arrange
            String token = "token.jwt.valido";
            String username = "usuario.teste";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userPrincipal);
            when(jwtService.isTokenValid(token, userPrincipal)).thenReturn(true);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals(userPrincipal, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }

        /**
         * Verifica que authorities do usuário são configuradas corretamente.
         */
        @Test
        @DisplayName("Deve configurar authorities do usuário quando token válido")
        void doFilterInternal_quandoTokenValido_deveConfigurarAuthorities() throws ServletException, IOException {
            // Arrange
            String token = "token.jwt.valido";
            String username = "usuario.teste";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userPrincipal);
            when(jwtService.isTokenValid(token, userPrincipal)).thenReturn(true);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(authentication.getAuthorities());
            assertFalse(authentication.getAuthorities().isEmpty());
        }
    }

    // ========================================================================
    // REQUISIÇÃO COM TOKEN INVÁLIDO
    // ========================================================================

    @Nested
    @DisplayName("Requisições com token inválido")
    class RequisicoesComTokenInvalidoTests {

        /**
         * Verifica que token inválido não autentica o usuário.
         */
        @Test
        @DisplayName("Deve continuar sem autenticar quando token inválido")
        void doFilterInternal_quandoTokenInvalido_naoDeveAutenticar() throws ServletException, IOException {
            // Arrange
            String token = "token.invalido";
            String username = "usuario.teste";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userPrincipal);
            when(jwtService.isTokenValid(token, userPrincipal)).thenReturn(false);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        /**
         * Verifica que username null no token não autentica.
         */
        @Test
        @DisplayName("Deve continuar sem autenticar quando username extraído é null")
        void doFilterInternal_quandoUsernameNull_naoDeveAutenticar() throws ServletException, IOException {
            // Arrange
            String token = "token.sem.username";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(null);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verifyNoInteractions(userDetailsService);
        }
    }

    // ========================================================================
    // CONTEXTO JÁ AUTENTICADO
    // ========================================================================

    @Nested
    @DisplayName("Requisições com contexto já autenticado")
    class ContextoJaAutenticadoTests {

        /**
         * Verifica que não sobrescreve autenticação existente no contexto.
         */
        @Test
        @DisplayName("Não deve reautenticar quando contexto já possui autenticação")
        void doFilterInternal_quandoContextoJaAutenticado_naoDeveReautenticar() throws ServletException, IOException {
            // Arrange
            String token = "token.jwt.valido";
            String username = "usuario.teste";

            // Simula contexto já autenticado
            var authExistente = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authExistente);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(username);

            // Act
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(userDetailsService); // Não deve carregar novamente
            assertEquals(authExistente, SecurityContextHolder.getContext().getAuthentication());
        }
    }
}