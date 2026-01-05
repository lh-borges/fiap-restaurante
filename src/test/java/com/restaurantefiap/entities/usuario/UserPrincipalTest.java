package com.restaurantefiap.entities.usuario;

import com.restaurantefiap.enums.Role;
import com.restaurantefiap.util.UsuarioTestBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link UserPrincipal}.
 * <p>Valida o adapter entre a entidade Usuario e a interface UserDetails
 * do Spring Security, garantindo delegação correta e formatação de authorities.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
class UserPrincipalTest {

    private Usuario usuario;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        usuario = UsuarioTestBuilder.criarUsuarioValido();
        userPrincipal = new UserPrincipal(usuario);
    }

    // ========================================================================
    // DELEGAÇÃO PARA USUARIO
    // ========================================================================

    @Nested
    @DisplayName("Delegação para entidade Usuario")
    class DelegacaoTests {

        /**
         * Verifica que getUsername retorna o login do Usuario.
         */
        @Test
        @DisplayName("Deve retornar login do usuário como username")
        void getUsername_quandoChamado_deveRetornarLoginDoUsuario() {
            // Act
            String username = userPrincipal.getUsername();

            // Assert
            assertEquals(usuario.getLogin(), username);
        }

        /**
         * Verifica que getPassword retorna a senha do Usuario.
         */
        @Test
        @DisplayName("Deve retornar password do usuário")
        void getPassword_quandoChamado_deveRetornarPasswordDoUsuario() {
            // Act
            String password = userPrincipal.getPassword();

            // Assert
            assertEquals(usuario.getPassword(), password);
        }

        /**
         * Verifica que getUsuario retorna a entidade encapsulada.
         */
        @Test
        @DisplayName("Deve retornar entidade Usuario encapsulada")
        void getUsuario_quandoChamado_deveRetornarEntidadeEncapsulada() {
            // Act
            Usuario usuarioRetornado = userPrincipal.getUsuario();

            // Assert
            assertSame(usuario, usuarioRetornado);
        }
    }

    // ========================================================================
    // AUTHORITIES
    // ========================================================================

    @Nested
    @DisplayName("Authorities do usuário")
    class AuthoritiesTests {

        /**
         * Verifica que authority é formatada com prefixo ROLE_.
         */
        @Test
        @DisplayName("Deve retornar authority com prefixo ROLE_")
        void getAuthorities_quandoChamado_deveRetornarComPrefixoRole() {
            // Arrange
            usuario.setRole(Role.CLIENTE);
            UserPrincipal principal = new UserPrincipal(usuario);

            // Act
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            // Assert
            assertNotNull(authorities);
            assertEquals(1, authorities.size());
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
        }

        /**
         * Verifica authority para role MASTER.
         */
        @Test
        @DisplayName("Deve retornar ROLE_MASTER para usuário MASTER")
        void getAuthorities_quandoRoleMaster_deveRetornarRoleMaster() {
            // Arrange
            usuario.setRole(Role.MASTER);
            UserPrincipal principal = new UserPrincipal(usuario);

            // Act
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            // Assert
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MASTER")));
        }

        /**
         * Verifica authority para role DONO_RESTAURANTE.
         */
        @Test
        @DisplayName("Deve retornar ROLE_DONO_RESTAURANTE para usuário DONO_RESTAURANTE")
        void getAuthorities_quandoRoleDonoRestaurante_deveRetornarRoleDonoRestaurante() {
            // Arrange
            usuario.setRole(Role.DONO_RESTAURANTE);
            UserPrincipal principal = new UserPrincipal(usuario);

            // Act
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            // Assert
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DONO_RESTAURANTE")));
        }
    }

    // ========================================================================
    // STATUS DA CONTA
    // ========================================================================

    @Nested
    @DisplayName("Status da conta")
    class StatusContaTests {

        /**
         * Verifica que conta nunca expira.
         */
        @Test
        @DisplayName("Deve retornar true para isAccountNonExpired")
        void isAccountNonExpired_quandoChamado_deveRetornarTrue() {
            // Act & Assert
            assertTrue(userPrincipal.isAccountNonExpired());
        }

        /**
         * Verifica que conta nunca está bloqueada.
         */
        @Test
        @DisplayName("Deve retornar true para isAccountNonLocked")
        void isAccountNonLocked_quandoChamado_deveRetornarTrue() {
            // Act & Assert
            assertTrue(userPrincipal.isAccountNonLocked());
        }

        /**
         * Verifica que credenciais nunca expiram.
         */
        @Test
        @DisplayName("Deve retornar true para isCredentialsNonExpired")
        void isCredentialsNonExpired_quandoChamado_deveRetornarTrue() {
            // Act & Assert
            assertTrue(userPrincipal.isCredentialsNonExpired());
        }

        /**
         * Verifica que conta sempre está habilitada.
         */
        @Test
        @DisplayName("Deve retornar true para isEnabled")
        void isEnabled_quandoChamado_deveRetornarTrue() {
            // Act & Assert
            assertTrue(userPrincipal.isEnabled());
        }
    }

    // ========================================================================
    // IMUTABILIDADE
    // ========================================================================

    @Nested
    @DisplayName("Imutabilidade da classe")
    class ImutabilidadeTests {

        /**
         * Verifica que classe é final (não pode ser estendida).
         */
        @Test
        @DisplayName("Deve ser classe final")
        void classe_deveSerFinal() {
            // Assert
            assertTrue(java.lang.reflect.Modifier.isFinal(UserPrincipal.class.getModifiers()));
        }

        /**
         * Verifica que campo usuario é final.
         */
        @Test
        @DisplayName("Deve ter campo usuario final")
        void campoUsuario_deveSerFinal() throws NoSuchFieldException {
            // Arrange
            var field = UserPrincipal.class.getDeclaredField("usuario");

            // Assert
            assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
        }
    }
}