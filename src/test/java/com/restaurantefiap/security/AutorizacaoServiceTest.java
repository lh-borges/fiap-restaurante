package com.restaurantefiap.security;

import com.restaurantefiap.entities.usuario.UserPrincipal;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;
import com.restaurantefiap.util.UsuarioTestBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link AutorizacaoService}.
 * <p>Valida regras de autorização: ownership, roles administrativas
 * e combinações de permissões usadas em @PreAuthorize.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
class AutorizacaoServiceTest {

    private AutorizacaoService autorizacaoService;

    @BeforeEach
    void setUp() {
        autorizacaoService = new AutorizacaoService();
    }

    @AfterEach
    void tearDown() {
        // Limpa o contexto de segurança após cada teste
        SecurityContextHolder.clearContext();
    }

    // ========================================================================
    // IS PROPRIO USUARIO
    // ========================================================================

    @Nested
    @DisplayName("Verificação de Ownership (isProprioUsuario)")
    class IsProprioUsuarioTests {

        /**
         * Verifica que retorna true quando usuário acessa seu próprio recurso.
         */
        @Test
        @DisplayName("Deve retornar true quando usuário acessa próprio recurso")
        void isProprioUsuario_quandoIdCorresponde_deveRetornarTrue() {
            // Arrange
            Long idUsuario = 1L;
            autenticarUsuario(idUsuario, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isProprioUsuario(idUsuario);

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que retorna false quando usuário tenta acessar recurso de outro.
         */
        @Test
        @DisplayName("Deve retornar false quando usuário acessa recurso de outro")
        void isProprioUsuario_quandoIdNaoCorresponde_deveRetornarFalse() {
            // Arrange
            Long idUsuarioLogado = 1L;
            Long idOutroUsuario = 2L;
            autenticarUsuario(idUsuarioLogado, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isProprioUsuario(idOutroUsuario);

            // Assert
            assertFalse(resultado);
        }

        /**
         * Verifica que retorna false quando não há usuário autenticado.
         */
        @Test
        @DisplayName("Deve retornar false quando não há usuário autenticado")
        void isProprioUsuario_quandoNaoAutenticado_deveRetornarFalse() {
            // Arrange - contexto vazio (nenhum usuário autenticado)
            Long idRecurso = 1L;

            // Act
            boolean resultado = autorizacaoService.isProprioUsuario(idRecurso);

            // Assert
            assertFalse(resultado);
        }

        /**
         * Verifica que retorna false quando idRecurso é null.
         */
        @Test
        @DisplayName("Deve retornar false quando idRecurso é null")
        void isProprioUsuario_quandoIdRecursoNull_deveRetornarFalse() {
            // Arrange
            autenticarUsuario(1L, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isProprioUsuario(null);

            // Assert
            assertFalse(resultado);
        }
    }

    // ========================================================================
    // IS ADMIN
    // ========================================================================

    @Nested
    @DisplayName("Verificação de Role Admin (isAdmin)")
    class IsAdminTests {

        /**
         * Verifica que MASTER é considerado admin.
         */
        @Test
        @DisplayName("Deve retornar true quando usuário é MASTER")
        void isAdmin_quandoRoleMaster_deveRetornarTrue() {
            // Arrange
            autenticarUsuario(1L, Role.MASTER);

            // Act
            boolean resultado = autorizacaoService.isAdmin();

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que DONO_RESTAURANTE é considerado admin.
         */
        @Test
        @DisplayName("Deve retornar true quando usuário é DONO_RESTAURANTE")
        void isAdmin_quandoRoleDonoRestaurante_deveRetornarTrue() {
            // Arrange
            autenticarUsuario(1L, Role.DONO_RESTAURANTE);

            // Act
            boolean resultado = autorizacaoService.isAdmin();

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que CLIENTE não é considerado admin.
         */
        @Test
        @DisplayName("Deve retornar false quando usuário é CLIENTE")
        void isAdmin_quandoRoleCliente_deveRetornarFalse() {
            // Arrange
            autenticarUsuario(1L, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isAdmin();

            // Assert
            assertFalse(resultado);
        }

        /**
         * Verifica que retorna false quando não autenticado.
         */
        @Test
        @DisplayName("Deve retornar false quando não há usuário autenticado")
        void isAdmin_quandoNaoAutenticado_deveRetornarFalse() {
            // Arrange - contexto vazio

            // Act
            boolean resultado = autorizacaoService.isAdmin();

            // Assert
            assertFalse(resultado);
        }
    }

    // ========================================================================
    // IS MASTER
    // ========================================================================

    @Nested
    @DisplayName("Verificação de Role Master (isMaster)")
    class IsMasterTests {

        /**
         * Verifica que MASTER retorna true.
         */
        @Test
        @DisplayName("Deve retornar true quando usuário é MASTER")
        void isMaster_quandoRoleMaster_deveRetornarTrue() {
            // Arrange
            autenticarUsuario(1L, Role.MASTER);

            // Act
            boolean resultado = autorizacaoService.isMaster();

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que DONO_RESTAURANTE não é MASTER.
         */
        @Test
        @DisplayName("Deve retornar false quando usuário é DONO_RESTAURANTE")
        void isMaster_quandoRoleDonoRestaurante_deveRetornarFalse() {
            // Arrange
            autenticarUsuario(1L, Role.DONO_RESTAURANTE);

            // Act
            boolean resultado = autorizacaoService.isMaster();

            // Assert
            assertFalse(resultado);
        }

        /**
         * Verifica que CLIENTE não é MASTER.
         */
        @Test
        @DisplayName("Deve retornar false quando usuário é CLIENTE")
        void isMaster_quandoRoleCliente_deveRetornarFalse() {
            // Arrange
            autenticarUsuario(1L, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isMaster();

            // Assert
            assertFalse(resultado);
        }

        /**
         * Verifica que retorna false quando não autenticado.
         */
        @Test
        @DisplayName("Deve retornar false quando não há usuário autenticado")
        void isMaster_quandoNaoAutenticado_deveRetornarFalse() {
            // Arrange - contexto vazio

            // Act
            boolean resultado = autorizacaoService.isMaster();

            // Assert
            assertFalse(resultado);
        }
    }

    // ========================================================================
    // IS ADMIN OU PROPRIO
    // ========================================================================

    @Nested
    @DisplayName("Verificação Combinada (isAdminOuProprio)")
    class IsAdminOuProprioTests {

        /**
         * Verifica que admin pode acessar recurso de qualquer usuário.
         */
        @Test
        @DisplayName("Deve retornar true quando é admin acessando recurso de outro")
        void isAdminOuProprio_quandoAdminAcessandoRecursoDeOutro_deveRetornarTrue() {
            // Arrange
            Long idAdmin = 1L;
            Long idOutroUsuario = 99L;
            autenticarUsuario(idAdmin, Role.MASTER);

            // Act
            boolean resultado = autorizacaoService.isAdminOuProprio(idOutroUsuario);

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que usuário comum pode acessar próprio recurso.
         */
        @Test
        @DisplayName("Deve retornar true quando cliente acessa próprio recurso")
        void isAdminOuProprio_quandoClienteAcessandoProprioRecurso_deveRetornarTrue() {
            // Arrange
            Long idUsuario = 1L;
            autenticarUsuario(idUsuario, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isAdminOuProprio(idUsuario);

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que DONO_RESTAURANTE (admin) pode acessar recurso de outro.
         */
        @Test
        @DisplayName("Deve retornar true quando DONO_RESTAURANTE acessa recurso de outro")
        void isAdminOuProprio_quandoDonoRestauranteAcessandoRecursoDeOutro_deveRetornarTrue() {
            // Arrange
            Long idDono = 1L;
            Long idCliente = 50L;
            autenticarUsuario(idDono, Role.DONO_RESTAURANTE);

            // Act
            boolean resultado = autorizacaoService.isAdminOuProprio(idCliente);

            // Assert
            assertTrue(resultado);
        }

        /**
         * Verifica que cliente não pode acessar recurso de outro usuário.
         */
        @Test
        @DisplayName("Deve retornar false quando cliente acessa recurso de outro")
        void isAdminOuProprio_quandoClienteAcessandoRecursoDeOutro_deveRetornarFalse() {
            // Arrange
            Long idCliente = 1L;
            Long idOutroUsuario = 2L;
            autenticarUsuario(idCliente, Role.CLIENTE);

            // Act
            boolean resultado = autorizacaoService.isAdminOuProprio(idOutroUsuario);

            // Assert
            assertFalse(resultado);
        }

        /**
         * Verifica que retorna false quando não autenticado.
         */
        @Test
        @DisplayName("Deve retornar false quando não há usuário autenticado")
        void isAdminOuProprio_quandoNaoAutenticado_deveRetornarFalse() {
            // Arrange - contexto vazio
            Long idRecurso = 1L;

            // Act
            boolean resultado = autorizacaoService.isAdminOuProprio(idRecurso);

            // Assert
            assertFalse(resultado);
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Configura o SecurityContext com um usuário autenticado.
     *
     * @param idUsuario ID do usuário a ser autenticado
     * @param role Role do usuário
     */
    private void autenticarUsuario(Long idUsuario, Role role) {
        Usuario usuario = UsuarioTestBuilder.criarUsuarioValido();
        usuario.setId(idUsuario);
        usuario.setRole(role);

        UserPrincipal userPrincipal = new UserPrincipal(usuario);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}