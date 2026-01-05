package com.restaurantefiap.adapters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link SimplePasswordPolicy}.
 * <p>Valida os critérios de complexidade de senha: tamanho mínimo,
 * presença de dígito e presença de letra maiúscula.</p>
 *
 * @author Danilo Fernando
 * @since 04/01/2026
 */
class SimplePasswordPolicyTest {

    private SimplePasswordPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new SimplePasswordPolicy();
    }

    @Nested
    @DisplayName("Cenários de sucesso - Senhas válidas")
    class CenariosValidos {

        /**
         * Verifica que senha com todos os critérios atendidos passa na validação.
         * Critérios: 8+ caracteres, ao menos 1 dígito, ao menos 1 maiúscula.
         */
        @Test
        @DisplayName("Deve aceitar senha com 8 caracteres, dígito e maiúscula")
        void validateOrThrow_quandoSenhaValida_naoDeveLancarExcecao() {
            // Arrange
            String senhaValida = "ValidPass123";

            // Act & Assert
            assertDoesNotThrow(() -> policy.validateOrThrow(senhaValida));
        }

        /**
         * Verifica que senhas maiores que o mínimo são aceitas.
         */
        @Test
        @DisplayName("Deve aceitar senha com mais de 8 caracteres")
        void validateOrThrow_quandoSenhaMaiorQue8Caracteres_naoDeveLancarExcecao() {
            // Arrange
            String senhaLonga = "ValidPassword123ComMuitosCaracteres";

            // Act & Assert
            assertDoesNotThrow(() -> policy.validateOrThrow(senhaLonga));
        }

        /**
         * Verifica que dígito pode estar em qualquer posição da senha.
         */
        @Test
        @DisplayName("Deve aceitar senha com dígito no meio")
        void validateOrThrow_quandoDigitoNoMeioDaSenha_naoDeveLancarExcecao() {
            // Arrange
            String senhaComDigitoNoMeio = "Valid1Pass";

            // Act & Assert
            assertDoesNotThrow(() -> policy.validateOrThrow(senhaComDigitoNoMeio));
        }

        /**
         * Verifica que maiúscula pode estar em qualquer posição da senha.
         */
        @Test
        @DisplayName("Deve aceitar senha com maiúscula no meio")
        void validateOrThrow_quandoMaiusculaNoMeioDaSenha_naoDeveLancarExcecao() {
            // Arrange
            String senhaComMaiusculaNoMeio = "validPass123";

            // Act & Assert
            assertDoesNotThrow(() -> policy.validateOrThrow(senhaComMaiusculaNoMeio));
        }
    }

    @Nested
    @DisplayName("Cenários de falha - Senhas inválidas")
    class CenariosInvalidos {

        /**
         * Verifica que senha com menos de 8 caracteres é rejeitada.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha menor que 8 caracteres")
        void validateOrThrow_quandoSenhaMenorQue8Caracteres_deveLancarExcecao() {
            // Arrange
            String senhaCurta = "Pass1";

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> policy.validateOrThrow(senhaCurta)
            );
            assertEquals("A senha deve ter no mínimo 8 caracteres.", ex.getMessage());
        }

        /**
         * Verifica que senha sem dígito numérico é rejeitada.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha sem dígito")
        void validateOrThrow_quandoSenhaSemDigito_deveLancarExcecao() {
            // Arrange
            String senhaSemDigito = "ValidPassword";

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> policy.validateOrThrow(senhaSemDigito)
            );
            assertEquals("A senha deve conter pelo menos um dígito numérico.", ex.getMessage());
        }

        /**
         * Verifica que senha sem letra maiúscula é rejeitada.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha sem maiúscula")
        void validateOrThrow_quandoSenhaSemMaiuscula_deveLancarExcecao() {
            // Arrange
            String senhaSemMaiuscula = "validpass123";

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> policy.validateOrThrow(senhaSemMaiuscula)
            );
            assertEquals("A senha deve conter pelo menos uma letra maiúscula.", ex.getMessage());
        }

        /**
         * Verifica que senha vazia é rejeitada na validação de tamanho.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha vazia")
        void validateOrThrow_quandoSenhaVazia_deveLancarExcecao() {
            // Arrange
            String senhaVazia = "";

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> policy.validateOrThrow(senhaVazia)
            );
            assertEquals("A senha deve ter no mínimo 8 caracteres.", ex.getMessage());
        }

        /**
         * Verifica que senha null é rejeitada.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha null")
        void validateOrThrow_quandoSenhaNull_deveLancarExcecao() {
            // Arrange
            String senhaNula = null;

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> policy.validateOrThrow(senhaNula)
            );
            assertEquals("A senha deve ter no mínimo 8 caracteres.", ex.getMessage());
        }

        /**
         * Verifica que senha apenas com espaços em branco é rejeitada.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha apenas com espaços")
        void validateOrThrow_quandoSenhaApenasEspacos_deveLancarExcecao() {
            // Arrange
            String senhaEspacos = "        ";

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> policy.validateOrThrow(senhaEspacos)
            );
            // Passa no tamanho (8 chars), mas falha no dígito
            assertEquals("A senha deve conter pelo menos um dígito numérico.", ex.getMessage());
        }
    }
}