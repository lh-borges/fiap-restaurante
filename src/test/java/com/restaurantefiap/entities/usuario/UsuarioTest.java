package com.restaurantefiap.entities.usuario;

import com.restaurantefiap.enums.Role;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;
import com.restaurantefiap.util.UsuarioTestBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a entidade {@link Usuario}.
 * <p>Valida a lógica de negócio contida na entidade: atualização de perfil,
 * alteração de senha com validação e verificação de status ativo.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
@ExtendWith(MockitoExtension.class)
class UsuarioTest {

    @Mock
    private PasswordPolicy passwordPolicy;

    @Mock
    private PasswordHasher passwordHasher;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = UsuarioTestBuilder.criarUsuarioValido();
    }

    // ========================================================================
    // ATUALIZAR PERFIL
    // ========================================================================

    @Nested
    @DisplayName("Atualização de perfil (atualizarPerfil)")
    class AtualizarPerfilTests {

        /**
         * Verifica que nome é atualizado quando informado.
         */
        @Test
        @DisplayName("Deve atualizar nome quando informado")
        void atualizarPerfil_quandoNomeInformado_deveAtualizarNome() {
            // Arrange
            Usuario origem = new Usuario();
            origem.setNome("Novo Nome");

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals("Novo Nome", usuario.getNome());
        }

        /**
         * Verifica que telefone é atualizado quando informado.
         */
        @Test
        @DisplayName("Deve atualizar telefone quando informado")
        void atualizarPerfil_quandoTelefoneInformado_deveAtualizarTelefone() {
            // Arrange
            Usuario origem = new Usuario();
            origem.setTelefone("11999999999");

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals("11999999999", usuario.getTelefone());
        }

        /**
         * Verifica que email é atualizado e normalizado quando informado.
         */
        @Test
        @DisplayName("Deve atualizar e normalizar email quando informado")
        void atualizarPerfil_quandoEmailInformado_deveAtualizarENormalizar() {
            // Arrange
            Usuario origem = new Usuario();
            origem.setEmail("  NOVO@EMAIL.COM  ");

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals("novo@email.com", usuario.getEmail());
        }

        /**
         * Verifica que nome não é alterado quando null.
         */
        @Test
        @DisplayName("Não deve atualizar nome quando null")
        void atualizarPerfil_quandoNomeNull_naoDeveAlterar() {
            // Arrange
            String nomeOriginal = usuario.getNome();
            Usuario origem = new Usuario();
            origem.setNome(null);

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals(nomeOriginal, usuario.getNome());
        }

        /**
         * Verifica que nome não é alterado quando vazio.
         */
        @Test
        @DisplayName("Não deve atualizar nome quando vazio")
        void atualizarPerfil_quandoNomeVazio_naoDeveAlterar() {
            // Arrange
            String nomeOriginal = usuario.getNome();
            Usuario origem = new Usuario();
            origem.setNome("   ");

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals(nomeOriginal, usuario.getNome());
        }

        /**
         * Verifica que múltiplos campos são atualizados simultaneamente.
         */
        @Test
        @DisplayName("Deve atualizar múltiplos campos simultaneamente")
        void atualizarPerfil_quandoMultiplosCamposInformados_deveAtualizarTodos() {
            // Arrange
            Usuario origem = new Usuario();
            origem.setNome("Novo Nome");
            origem.setTelefone("11888888888");
            origem.setEmail("novo@email.com");

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertAll(
                    () -> assertEquals("Novo Nome", usuario.getNome()),
                    () -> assertEquals("11888888888", usuario.getTelefone()),
                    () -> assertEquals("novo@email.com", usuario.getEmail())
            );
        }

        /**
         * Verifica que role não é alterada (segurança).
         */
        @Test
        @DisplayName("Não deve atualizar role por segurança")
        void atualizarPerfil_quandoRoleInformada_naoDeveAlterar() {
            // Arrange
            usuario.setRole(Role.CLIENTE);
            Usuario origem = new Usuario();
            origem.setRole(Role.MASTER);

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals(Role.CLIENTE, usuario.getRole());
        }

        /**
         * Verifica que login não é alterado (imutável).
         */
        @Test
        @DisplayName("Não deve atualizar login por ser imutável")
        void atualizarPerfil_quandoLoginInformado_naoDeveAlterar() {
            // Arrange
            String loginOriginal = usuario.getLogin();
            Usuario origem = new Usuario();
            origem.setLogin("novo.login");

            // Act
            usuario.atualizarPerfil(origem);

            // Assert
            assertEquals(loginOriginal, usuario.getLogin());
        }
    }

    // ========================================================================
    // ALTERAR SENHA
    // ========================================================================

    @Nested
    @DisplayName("Alteração de senha (alterarSenha)")
    class AlterarSenhaTests {

        /**
         * Verifica que senha é validada pela policy e hasheada.
         */
        @Test
        @DisplayName("Deve validar e hashear senha quando válida")
        void alterarSenha_quandoSenhaValida_deveValidarEHashear() {
            // Arrange
            String senhaPlana = "NovaSenha123";
            String senhaHasheada = "hash_da_senha";
            when(passwordHasher.hash(senhaPlana)).thenReturn(senhaHasheada);

            // Act
            usuario.alterarSenha(senhaPlana, passwordPolicy, passwordHasher);

            // Assert
            verify(passwordPolicy).validateOrThrow(senhaPlana);
            verify(passwordHasher).hash(senhaPlana);
            assertEquals(senhaHasheada, usuario.getPassword());
        }

        /**
         * Verifica que exceção é lançada quando senha é null.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha null")
        void alterarSenha_quandoSenhaNull_deveLancarExcecao() {
            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> usuario.alterarSenha(null, passwordPolicy, passwordHasher)
            );
            assertEquals("Senha é obrigatória.", ex.getMessage());
            verifyNoInteractions(passwordPolicy, passwordHasher);
        }

        /**
         * Verifica que exceção é lançada quando senha é vazia.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha vazia")
        void alterarSenha_quandoSenhaVazia_deveLancarExcecao() {
            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> usuario.alterarSenha("", passwordPolicy, passwordHasher)
            );
            assertEquals("Senha é obrigatória.", ex.getMessage());
            verifyNoInteractions(passwordPolicy, passwordHasher);
        }

        /**
         * Verifica que exceção é lançada quando senha contém apenas espaços.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha apenas com espaços")
        void alterarSenha_quandoSenhaApenasEspacos_deveLancarExcecao() {
            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> usuario.alterarSenha("     ", passwordPolicy, passwordHasher)
            );
            assertEquals("Senha é obrigatória.", ex.getMessage());
        }

        /**
         * Verifica que exceção da policy é propagada.
         */
        @Test
        @DisplayName("Deve propagar exceção quando senha não atende policy")
        void alterarSenha_quandoSenhaNaoAtendePolicy_devePropagarExcecao() {
            // Arrange
            String senhaFraca = "fraca";
            doThrow(new IllegalArgumentException("Senha muito fraca"))
                    .when(passwordPolicy).validateOrThrow(senhaFraca);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> usuario.alterarSenha(senhaFraca, passwordPolicy, passwordHasher)
            );
            assertEquals("Senha muito fraca", ex.getMessage());
            verify(passwordHasher, never()).hash(anyString());
        }
    }

    // ========================================================================
    // ESTA ATIVO (Soft Delete)
    // ========================================================================

    @Nested
    @DisplayName("Verificação de status ativo (estaAtivo)")
    class EstaAtivoTests {

        /**
         * Verifica que usuário sem deletadoEm está ativo.
         */
        @Test
        @DisplayName("Deve retornar true quando deletadoEm é null")
        void estaAtivo_quandoDeletadoEmNull_deveRetornarTrue() {
            // Arrange
            usuario.setDeletadoEm(null);

            // Act
            boolean ativo = usuario.estaAtivo();

            // Assert
            assertTrue(ativo);
        }

        /**
         * Verifica que usuário com deletadoEm não está ativo.
         */
        @Test
        @DisplayName("Deve retornar false quando deletadoEm possui valor")
        void estaAtivo_quandoDeletadoEmPreenchido_deveRetornarFalse() {
            // Arrange
            usuario.setDeletadoEm(LocalDateTime.now());

            // Act
            boolean ativo = usuario.estaAtivo();

            // Assert
            assertFalse(ativo);
        }
    }
}