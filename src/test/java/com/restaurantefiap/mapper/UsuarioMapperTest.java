package com.restaurantefiap.mapper;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;
import com.restaurantefiap.util.UsuarioTestBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link UsuarioMapper}.
 * <p>Valida conversões entre entidade Usuario e seus DTOs,
 * garantindo que todos os campos são mapeados corretamente.</p>
 *
 * @author Danilo Fernando
 * @since 04/01/2026
 */
class UsuarioMapperTest {

    // ========================================================================
    // ENTIDADE -> DTO (paraDto)
    // ========================================================================

    @Nested
    @DisplayName("Conversão Usuario -> UsuarioResponseDTO")
    class ParaDtoTests {

        /**
         * Verifica que todos os campos da entidade são mapeados para o DTO.
         */
        @Test
        @DisplayName("Deve converter todos os campos do usuário para DTO")
        void paraDto_quandoUsuarioValido_deveConverterTodosCampos() {
            // Arrange
            Usuario usuario = criarUsuarioCompleto();

            // Act
            UsuarioResponseDTO dto = UsuarioMapper.paraDto(usuario);

            // Assert
            assertAll("Campos básicos",
                    () -> assertEquals(usuario.getId(), dto.id()),
                    () -> assertEquals(usuario.getLogin(), dto.login()),
                    () -> assertEquals(usuario.getEmail(), dto.email()),
                    () -> assertEquals(usuario.getNome(), dto.nome()),
                    () -> assertEquals(usuario.getTelefone(), dto.telefone()),
                    () -> assertEquals(usuario.getRole(), dto.role()),
                    () -> assertEquals(usuario.getCriadoEm(), dto.criadoEm()),
                    () -> assertEquals(usuario.getAtualizadoEm(), dto.atualizadoEm())
            );
        }

        /**
         * Verifica que endereço é mapeado corretamente quando presente.
         */
        @Test
        @DisplayName("Deve mapear endereço quando usuário possui endereço")
        void paraDto_quandoUsuarioComEndereco_deveMapearEndereco() {
            // Arrange
            Usuario usuario = criarUsuarioCompleto();
            Endereco endereco = new Endereco(UsuarioTestBuilder.criarEnderecoValido());
            usuario.setEndereco(endereco);

            // Act
            UsuarioResponseDTO dto = UsuarioMapper.paraDto(usuario);

            // Assert
            assertNotNull(dto.endereco());
            assertAll("Campos do endereço",
                    () -> assertEquals(endereco.getLogradouro(), dto.endereco().logradouro()),
                    () -> assertEquals(endereco.getBairro(), dto.endereco().bairro()),
                    () -> assertEquals(endereco.getCep(), dto.endereco().cep()),
                    () -> assertEquals(endereco.getCidade(), dto.endereco().cidade()),
                    () -> assertEquals(endereco.getUf(), dto.endereco().uf()),
                    () -> assertEquals(endereco.getNumero(), dto.endereco().numero()),
                    () -> assertEquals(endereco.getComplemento(), dto.endereco().complemento())
            );
        }

        /**
         * Verifica que DTO é criado sem endereço quando usuário não possui.
         */
        @Test
        @DisplayName("Deve retornar endereço null quando usuário não possui endereço")
        void paraDto_quandoUsuarioSemEndereco_deveRetornarEnderecoNull() {
            // Arrange
            Usuario usuario = criarUsuarioCompleto();
            usuario.setEndereco(null);

            // Act
            UsuarioResponseDTO dto = UsuarioMapper.paraDto(usuario);

            // Assert
            assertNotNull(dto);
            assertNull(dto.endereco());
        }

        /**
         * Verifica comportamento quando usuário é null.
         * Espera-se NullPointerException pois o mapper não trata este caso.
         */
        @Test
        @DisplayName("Deve lançar NullPointerException quando usuário null")
        void paraDto_quandoUsuarioNull_deveLancarNullPointerException() {
            // Act & Assert
            assertThrows(
                    NullPointerException.class,
                    () -> UsuarioMapper.paraDto(null)
            );
        }
    }

    // ========================================================================
    // DTO -> ENTIDADE (paraEntidade)
    // ========================================================================

    @Nested
    @DisplayName("Conversão UsuarioRequestDTO -> Usuario")
    class ParaEntidadeTests {

        /**
         * Verifica que todos os campos do DTO são mapeados para a entidade.
         */
        @Test
        @DisplayName("Deve converter todos os campos do DTO para entidade")
        void paraEntidade_quandoDtoValido_deveConverterTodosCampos() {
            // Arrange
            UsuarioRequestDTO dto = UsuarioTestBuilder.criarUsuarioRequestDTOValido();

            // Act
            Usuario usuario = UsuarioMapper.paraEntidade(dto);

            // Assert
            assertAll("Campos básicos",
                    () -> assertNull(usuario.getId(), "ID deve ser null (gerado pelo banco)"),
                    () -> assertEquals(dto.login(), usuario.getLogin()),
                    () -> assertEquals(dto.email(), usuario.getEmail()),
                    () -> assertEquals(dto.nome(), usuario.getNome()),
                    () -> assertEquals(dto.telefone(), usuario.getTelefone()),
                    () -> assertEquals(dto.role(), usuario.getRole()),
                    () -> assertNull(usuario.getPassword(), "Senha não deve ser mapeada aqui")
            );
        }

        /**
         * Verifica que endereço é criado quando presente no DTO.
         */
        @Test
        @DisplayName("Deve criar endereço quando DTO possui endereço")
        void paraEntidade_quandoDtoComEndereco_deveCriarEndereco() {
            // Arrange
            UsuarioRequestDTO dto = UsuarioTestBuilder.criarUsuarioRequestDTOValido();

            // Act
            Usuario usuario = UsuarioMapper.paraEntidade(dto);

            // Assert
            assertNotNull(usuario.getEndereco());
            assertEquals(dto.endereco().logradouro(), usuario.getEndereco().getLogradouro());
            assertEquals(dto.endereco().bairro(), usuario.getEndereco().getBairro());
        }

        /**
         * Verifica que entidade é criada sem endereço quando DTO não possui.
         */
        @Test
        @DisplayName("Deve criar entidade sem endereço quando DTO não possui endereço")
        void paraEntidade_quandoDtoSemEndereco_deveCriarSemEndereco() {
            // Arrange
            UsuarioRequestDTO dtoSemEndereco = new UsuarioRequestDTO(
                    "usuario.teste",
                    "usuario@teste.com",
                    "João Silva",
                    "11987654321",
                    Role.CLIENTE,
                    "ValidPass123",
                    null
            );

            // Act
            Usuario usuario = UsuarioMapper.paraEntidade(dtoSemEndereco);

            // Assert
            assertNotNull(usuario);
            assertNull(usuario.getEndereco());
        }

        /**
         * Verifica comportamento quando DTO é null.
         * Espera-se NullPointerException pois o mapper não trata este caso.
         */
        @Test
        @DisplayName("Deve lançar NullPointerException quando DTO null")
        void paraEntidade_quandoDtoNull_deveLancarNullPointerException() {
            // Act & Assert
            assertThrows(
                    NullPointerException.class,
                    () -> UsuarioMapper.paraEntidade(null)
            );
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Cria um Usuario completo com todos os campos preenchidos para testes.
     */
    private Usuario criarUsuarioCompleto() {
        Usuario usuario = UsuarioTestBuilder.criarUsuarioValido();
        usuario.setCriadoEm(LocalDateTime.of(2026, 1, 1, 10, 0));
        usuario.setAtualizadoEm(LocalDateTime.of(2026, 1, 4, 15, 30));
        return usuario;
    }
}