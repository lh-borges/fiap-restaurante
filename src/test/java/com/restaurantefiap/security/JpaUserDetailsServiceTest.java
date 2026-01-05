package com.restaurantefiap.security;

import com.restaurantefiap.entities.usuario.UserPrincipal;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.repository.UsuarioRepository;
import com.restaurantefiap.util.UsuarioTestBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link JpaUserDetailsService}.
 * <p>Valida o carregamento de usuários para autenticação do Spring Security,
 * incluindo normalização de login e tratamento de usuário não encontrado.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private JpaUserDetailsService jpaUserDetailsService;

    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        usuarioExistente = UsuarioTestBuilder.criarUsuarioValido();
    }

    // ========================================================================
    // CENÁRIOS DE SUCESSO
    // ========================================================================

    @Nested
    @DisplayName("Carregamento de usuário com sucesso")
    class CarregamentoSucessoTests {

        /**
         * Verifica que usuário existente é carregado e retornado como UserPrincipal.
         */
        @Test
        @DisplayName("Deve retornar UserPrincipal quando login existe")
        void loadUserByUsername_quandoLoginExiste_deveRetornarUserPrincipal() {
            // Arrange
            String login = "usuario.teste";
            when(usuarioRepository.findByLoginIgnoreCase(login)).thenReturn(Optional.of(usuarioExistente));

            // Act
            UserDetails resultado = jpaUserDetailsService.loadUserByUsername(login);

            // Assert
            assertNotNull(resultado);
            assertInstanceOf(UserPrincipal.class, resultado);
            assertEquals(usuarioExistente.getLogin(), resultado.getUsername());
        }

        /**
         * Verifica que login é normalizado antes da busca (trim + lowercase).
         */
        @Test
        @DisplayName("Deve normalizar login antes de buscar")
        void loadUserByUsername_quandoLoginComEspacosEMaiusculas_deveNormalizarEBuscar() {
            // Arrange
            String loginComEspacos = "  USUARIO.TESTE  ";
            String loginNormalizado = "usuario.teste";
            when(usuarioRepository.findByLoginIgnoreCase(loginNormalizado)).thenReturn(Optional.of(usuarioExistente));

            // Act
            UserDetails resultado = jpaUserDetailsService.loadUserByUsername(loginComEspacos);

            // Assert
            assertNotNull(resultado);
            verify(usuarioRepository).findByLoginIgnoreCase(loginNormalizado);
        }

        /**
         * Verifica que UserPrincipal retornado contém a entidade Usuario correta.
         */
        @Test
        @DisplayName("Deve retornar UserPrincipal com Usuario encapsulado")
        void loadUserByUsername_quandoLoginExiste_deveEncapsularUsuario() {
            // Arrange
            String login = "usuario.teste";
            when(usuarioRepository.findByLoginIgnoreCase(login)).thenReturn(Optional.of(usuarioExistente));

            // Act
            UserDetails resultado = jpaUserDetailsService.loadUserByUsername(login);

            // Assert
            UserPrincipal userPrincipal = (UserPrincipal) resultado;
            assertEquals(usuarioExistente.getId(), userPrincipal.getUsuario().getId());
            assertEquals(usuarioExistente.getEmail(), userPrincipal.getUsuario().getEmail());
            assertEquals(usuarioExistente.getRole(), userPrincipal.getUsuario().getRole());
        }
    }

    // ========================================================================
    // CENÁRIOS DE FALHA
    // ========================================================================

    @Nested
    @DisplayName("Carregamento de usuário com falha")
    class CarregamentoFalhaTests {

        /**
         * Verifica que login inexistente lança UsernameNotFoundException.
         */
        @Test
        @DisplayName("Deve lançar exceção quando login não existe")
        void loadUserByUsername_quandoLoginNaoExiste_deveLancarUsernameNotFoundException() {
            // Arrange
            String loginInexistente = "usuario.inexistente";
            when(usuarioRepository.findByLoginIgnoreCase(loginInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            UsernameNotFoundException ex = assertThrows(
                    UsernameNotFoundException.class,
                    () -> jpaUserDetailsService.loadUserByUsername(loginInexistente)
            );
            assertTrue(ex.getMessage().contains(loginInexistente));
        }

        /**
         * Verifica que login null é normalizado para string vazia e busca é executada.
         */
        @Test
        @DisplayName("Deve normalizar login null para vazio e lançar exceção")
        void loadUserByUsername_quandoLoginNull_deveNormalizarParaVazioELancarExcecao() {
            // Arrange
            when(usuarioRepository.findByLoginIgnoreCase("")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    UsernameNotFoundException.class,
                    () -> jpaUserDetailsService.loadUserByUsername(null)
            );
            verify(usuarioRepository).findByLoginIgnoreCase("");
        }

        /**
         * Verifica que login vazio (apenas espaços) é normalizado e busca é executada.
         */
        @Test
        @DisplayName("Deve normalizar login com apenas espaços para vazio")
        void loadUserByUsername_quandoLoginApenasEspacos_deveNormalizarParaVazio() {
            // Arrange
            String loginEspacos = "     ";
            when(usuarioRepository.findByLoginIgnoreCase("")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    UsernameNotFoundException.class,
                    () -> jpaUserDetailsService.loadUserByUsername(loginEspacos)
            );
            verify(usuarioRepository).findByLoginIgnoreCase("");
        }
    }

    // ========================================================================
    // VERIFICAÇÃO DE AUTHORITIES
    // ========================================================================

    @Nested
    @DisplayName("Authorities do usuário")
    class AuthoritiesTests {

        /**
         * Verifica que authorities são carregadas corretamente do UserPrincipal.
         */
        @Test
        @DisplayName("Deve retornar UserDetails com authorities corretas")
        void loadUserByUsername_quandoLoginExiste_deveRetornarComAuthorities() {
            // Arrange
            String login = "usuario.teste";
            when(usuarioRepository.findByLoginIgnoreCase(login)).thenReturn(Optional.of(usuarioExistente));

            // Act
            UserDetails resultado = jpaUserDetailsService.loadUserByUsername(login);

            // Assert
            assertNotNull(resultado.getAuthorities());
            assertFalse(resultado.getAuthorities().isEmpty());
            assertTrue(
                    resultado.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().startsWith("ROLE_"))
            );
        }

        /**
         * Verifica que password é retornado corretamente para validação.
         */
        @Test
        @DisplayName("Deve retornar UserDetails com password para validação")
        void loadUserByUsername_quandoLoginExiste_deveRetornarComPassword() {
            // Arrange
            String login = "usuario.teste";
            when(usuarioRepository.findByLoginIgnoreCase(login)).thenReturn(Optional.of(usuarioExistente));

            // Act
            UserDetails resultado = jpaUserDetailsService.loadUserByUsername(login);

            // Assert
            assertNotNull(resultado.getPassword());
            assertEquals(usuarioExistente.getPassword(), resultado.getPassword());
        }
    }
}