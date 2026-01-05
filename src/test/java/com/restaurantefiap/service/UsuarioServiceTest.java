package com.restaurantefiap.service;

import com.restaurantefiap.dto.request.AlterarSenhaRequestDTO;
import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;
import com.restaurantefiap.exception.DuplicateResourceException;
import com.restaurantefiap.exception.InvalidPasswordException;
import com.restaurantefiap.exception.ResourceNotFoundException;
import com.restaurantefiap.repository.UsuarioRepository;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;
import com.restaurantefiap.util.UsuarioTestBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Testes unitários para {@link UsuarioService}.
 * <p>Cobre operações de CRUD, validações de negócio e tratamento de exceções.</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordPolicy passwordPolicy;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UsuarioService service;

    // ========================================================================
    // CRIAR USUÁRIO - 9 testes
    // ========================================================================

    @Nested
    @DisplayName("Criar Usuário")
    class CriarUsuarioTests {

        private UsuarioRequestDTO requestDTO;

        @BeforeEach
        void setUp() {
            requestDTO = UsuarioTestBuilder.criarUsuarioRequestDTOValido();
        }

        /**
         * Verifica criação bem-sucedida com todos os dados válidos.
         * Deve normalizar login/email, hashear senha e persistir.
         */
        @Test
        @DisplayName("Deve criar usuário com sucesso quando dados válidos")
        void criar_quandoDadosValidos_deveCriarUsuarioComSucesso() {
            // Arrange
            when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
            when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn("senhaHasheada");
            when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            // Act
            UsuarioResponseDTO resultado = service.criar(requestDTO);

            // Assert
            assertNotNull(resultado);
            assertEquals("usuario.teste", resultado.login());
            assertEquals("usuario@teste.com", resultado.email());
            assertEquals(Role.CLIENTE, resultado.role());

            verify(passwordPolicy).validateOrThrow(requestDTO.password());
            verify(passwordHasher).hash(requestDTO.password());
            verify(repository).save(any(Usuario.class));
        }

        /**
         * Verifica que usuário com endereço é criado corretamente.
         */
        @Test
        @DisplayName("Deve criar usuário com endereço quando informado")
        void criar_quandoEnderecoInformado_deveCriarUsuarioComEndereco() {
            // Arrange
            when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
            when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn("senhaHasheada");
            when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            // Act
            UsuarioResponseDTO resultado = service.criar(requestDTO);

            // Assert
            assertNotNull(resultado);
            assertNotNull(resultado.endereco());
            assertEquals("Rua Teste", resultado.endereco().logradouro());
            assertEquals("Centro", resultado.endereco().bairro());
        }

        /**
         * Verifica que usuário sem endereço é criado sem erro.
         */
        @Test
        @DisplayName("Deve criar usuário sem endereço quando não informado")
        void criar_quandoEnderecoNulo_deveCriarUsuarioSemEndereco() {
            // Arrange
            UsuarioRequestDTO dtoSemEndereco = new UsuarioRequestDTO(
                    "usuario.teste",
                    "usuario@teste.com",
                    "João Silva",
                    "11987654321",
                    Role.CLIENTE,
                    "ValidPass123",
                    null // sem endereço
            );

            when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
            when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn("senhaHasheada");
            when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            // Act
            UsuarioResponseDTO resultado = service.criar(dtoSemEndereco);

            // Assert
            assertNotNull(resultado);
            assertNull(resultado.endereco());
        }

        /**
         * Verifica que login vazio lança exceção.
         */
        @Test
        @DisplayName("Deve lançar exceção quando login vazio")
        void criar_quandoLoginVazio_deveLancarExcecao() {
            // Arrange
            UsuarioRequestDTO dtoLoginVazio = new UsuarioRequestDTO(
                    "",
                    "usuario@teste.com",
                    "João Silva",
                    "11987654321",
                    Role.CLIENTE,
                    "ValidPass123",
                    null
            );

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criar(dtoLoginVazio)
            );
            assertEquals("Login não pode ser vazio.", ex.getMessage());
            verify(repository, never()).save(any());
        }

        /**
         * Verifica que login nulo lança exceção.
         */
        @Test
        @DisplayName("Deve lançar exceção quando login nulo")
        void criar_quandoLoginNulo_deveLancarExcecao() {
            // Arrange
            UsuarioRequestDTO dtoLoginNulo = new UsuarioRequestDTO(
                    null,
                    "usuario@teste.com",
                    "João Silva",
                    "11987654321",
                    Role.CLIENTE,
                    "ValidPass123",
                    null
            );

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criar(dtoLoginNulo)
            );
            assertEquals("Login não pode ser vazio.", ex.getMessage());
        }

        /**
         * Verifica que email vazio lança exceção.
         */
        @Test
        @DisplayName("Deve lançar exceção quando email vazio")
        void criar_quandoEmailVazio_deveLancarExcecao() {
            // Arrange
            UsuarioRequestDTO dtoEmailVazio = new UsuarioRequestDTO(
                    "usuario.teste",
                    "   ",
                    "João Silva",
                    "11987654321",
                    Role.CLIENTE,
                    "ValidPass123",
                    null
            );

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criar(dtoEmailVazio)
            );
            assertEquals("E-mail não pode ser vazio.", ex.getMessage());
        }

        /**
         * Verifica que login duplicado lança exceção.
         */
        @Test
        @DisplayName("Deve lançar exceção quando login já existe")
        void criar_quandoLoginDuplicado_deveLancarExcecao() {
            // Arrange
            when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(true);

            // Act & Assert
            DuplicateResourceException ex = assertThrows(
                    DuplicateResourceException.class,
                    () -> service.criar(requestDTO)
            );
            assertTrue(ex.getMessage().contains("Login"));
            verify(repository, never()).save(any());
        }

        /**
         * Verifica que email duplicado lança exceção.
         */
        @Test
        @DisplayName("Deve lançar exceção quando email já existe")
        void criar_quandoEmailDuplicado_deveLancarExcecao() {
            // Arrange
            when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
            when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

            // Act & Assert
            DuplicateResourceException ex = assertThrows(
                    DuplicateResourceException.class,
                    () -> service.criar(requestDTO)
            );
            assertTrue(ex.getMessage().contains("Email"));
            verify(repository, never()).save(any());
        }

        /**
         * Verifica que login e email são normalizados (trim + lowercase).
         */
        @Test
        @DisplayName("Deve normalizar login e email ao criar")
        void criar_quandoLoginComEspacosEMaiusculas_deveNormalizar() {
            // Arrange
            UsuarioRequestDTO dtoComEspacos = new UsuarioRequestDTO(
                    "  USER.TESTE  ",
                    "  EMAIL@TESTE.COM  ",
                    "João Silva",
                    "11987654321",
                    Role.CLIENTE,
                    "ValidPass123",
                    null
            );

            when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
            when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(passwordHasher.hash(anyString())).thenReturn("senhaHasheada");

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            when(repository.save(captor.capture())).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            // Act
            service.criar(dtoComEspacos);

            // Assert
            Usuario usuarioSalvo = captor.getValue();
            assertEquals("user.teste", usuarioSalvo.getLogin());
            assertEquals("email@teste.com", usuarioSalvo.getEmail());
        }
    }

    // ========================================================================
    // ALTERAR SENHA - 6 testes
    // ========================================================================

    @Nested
    @DisplayName("Alterar Senha")
    class AlterarSenhaTests {

        private Usuario usuarioExistente;

        @BeforeEach
        void setUp() {
            usuarioExistente = UsuarioTestBuilder.criarUsuarioValido();
        }

        /**
         * Verifica alteração de senha bem-sucedida.
         */
        @Test
        @DisplayName("Deve alterar senha com sucesso quando senha atual correta")
        void alterarSenha_quandoSenhaAtualCorreta_deveAlterarComSucesso() {
            // Arrange
            Long id = 1L;
            AlterarSenhaRequestDTO dto = new AlterarSenhaRequestDTO("senhaAtual123", "NovaSenha456");

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(passwordHasher.matches("senhaAtual123", usuarioExistente.getPassword())).thenReturn(true);
            when(passwordHasher.hash("NovaSenha456")).thenReturn("novaSenhaHasheada");
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            assertDoesNotThrow(() -> service.alterarSenha(id, dto));

            // Assert
            verify(passwordPolicy).validateOrThrow("NovaSenha456");
            verify(passwordHasher).hash("NovaSenha456");
            verify(repository).save(usuarioExistente);
        }

        /**
         * Verifica que a nova senha é validada pela policy.
         */
        @Test
        @DisplayName("Deve validar nova senha com policy")
        void alterarSenha_quandoChamado_deveValidarNovaSenhaComPolicy() {
            // Arrange
            Long id = 1L;
            AlterarSenhaRequestDTO dto = new AlterarSenhaRequestDTO("senhaAtual123", "NovaSenha456");

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);
            when(passwordHasher.hash(anyString())).thenReturn("hash");
            when(repository.save(any())).thenReturn(usuarioExistente);

            // Act
            service.alterarSenha(id, dto);

            // Assert
            verify(passwordPolicy).validateOrThrow("NovaSenha456");
        }

        /**
         * Verifica exceção quando usuário não existe.
         */
        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void alterarSenha_quandoUsuarioNaoExiste_deveLancarExcecao() {
            // Arrange
            Long idInexistente = 999L;
            AlterarSenhaRequestDTO dto = new AlterarSenhaRequestDTO("senhaAtual", "NovaSenha456");

            when(repository.findAtivoById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.alterarSenha(idInexistente, dto)
            );
            verify(passwordHasher, never()).hash(anyString());
        }

        /**
         * Verifica exceção quando senha atual está incorreta.
         */
        @Test
        @DisplayName("Deve lançar exceção quando senha atual incorreta")
        void alterarSenha_quandoSenhaAtualIncorreta_deveLancarExcecao() {
            // Arrange
            Long id = 1L;
            AlterarSenhaRequestDTO dto = new AlterarSenhaRequestDTO("senhaErrada", "NovaSenha456");

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(passwordHasher.matches("senhaErrada", usuarioExistente.getPassword())).thenReturn(false);

            // Act & Assert
            InvalidPasswordException ex = assertThrows(
                    InvalidPasswordException.class,
                    () -> service.alterarSenha(id, dto)
            );
            assertEquals("Senha atual incorreta", ex.getMessage());
            verify(repository, never()).save(any());
        }

        /**
         * Verifica que exceção da policy é propagada.
         */
        @Test
        @DisplayName("Deve lançar exceção quando nova senha não atende policy")
        void alterarSenha_quandoNovaSenhaNaoAtendePolicy_deveLancarExcecao() {
            // Arrange
            Long id = 1L;
            AlterarSenhaRequestDTO dto = new AlterarSenhaRequestDTO("senhaAtual123", "fraca");

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);
            doThrow(new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres."))
                    .when(passwordPolicy).validateOrThrow("fraca");

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.alterarSenha(id, dto)
            );
            assertTrue(ex.getMessage().contains("8 caracteres"));
            verify(repository, never()).save(any());
        }

        /**
         * Verifica exceção quando nova senha é vazia.
         * A validação ocorre na entidade Usuario antes de chamar a policy.
         */
        @Test
        @DisplayName("Deve lançar exceção quando nova senha vazia")
        void alterarSenha_quandoNovaSenhaVazia_deveLancarExcecao() {
            // Arrange
            Long id = 1L;
            AlterarSenhaRequestDTO dto = new AlterarSenhaRequestDTO("senhaAtual123", "");

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.alterarSenha(id, dto)
            );

            assertEquals("Senha é obrigatória.", ex.getMessage());
            verify(passwordPolicy, never()).validateOrThrow(anyString());
            verify(repository, never()).save(any());
        }
    }

    // ========================================================================
    // ATUALIZAR USUÁRIO - 7 testes
    // ========================================================================

    @Nested
    @DisplayName("Atualizar Usuário")
    class AtualizarUsuarioTests {

        private Usuario usuarioExistente;

        @BeforeEach
        void setUp() {
            usuarioExistente = UsuarioTestBuilder.criarUsuarioValido();
        }

        /**
         * Verifica atualização apenas do nome.
         */
        @Test
        @DisplayName("Deve atualizar apenas nome quando somente nome informado")
        void atualizar_quandoApenasNomeInformado_deveAtualizarNome() {
            // Arrange
            Long id = 1L;
            String nomeOriginal = usuarioExistente.getNome();
            String telefoneOriginal = usuarioExistente.getTelefone();
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO("Novo Nome", null, null);

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            UsuarioResponseDTO resultado = service.atualizar(id, dto);

            // Assert
            assertNotNull(resultado);
            assertEquals("Novo Nome", usuarioExistente.getNome());
            assertEquals(telefoneOriginal, usuarioExistente.getTelefone());
        }

        /**
         * Verifica atualização apenas do telefone.
         */
        @Test
        @DisplayName("Deve atualizar apenas telefone quando somente telefone informado")
        void atualizar_quandoApenasTelefoneInformado_deveAtualizarTelefone() {
            // Arrange
            Long id = 1L;
            String nomeOriginal = usuarioExistente.getNome();
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO(null, "11999999999", null);

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            service.atualizar(id, dto);

            // Assert
            assertEquals(nomeOriginal, usuarioExistente.getNome());
            assertEquals("11999999999", usuarioExistente.getTelefone());
        }

        /**
         * Verifica criação de endereço quando usuário não possuía.
         */
        @Test
        @DisplayName("Deve criar endereço quando usuário não possuía")
        void atualizar_quandoUsuarioSemEndereco_deveCriarEndereco() {
            // Arrange
            Long id = 1L;
            usuarioExistente.setEndereco(null); // sem endereço

            DadosEndereco novoEndereco = UsuarioTestBuilder.criarEnderecoValido();
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO(null, null, novoEndereco);

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            service.atualizar(id, dto);

            // Assert
            assertNotNull(usuarioExistente.getEndereco());
            assertEquals("Rua Teste", usuarioExistente.getEndereco().getLogradouro());
        }

        /**
         * Verifica atualização de endereço existente.
         */
        @Test
        @DisplayName("Deve atualizar endereço existente")
        void atualizar_quandoUsuarioComEndereco_deveAtualizarEndereco() {
            // Arrange
            Long id = 1L;
            Endereco enderecoExistente = new Endereco(UsuarioTestBuilder.criarEnderecoValido());
            usuarioExistente.setEndereco(enderecoExistente);

            DadosEndereco enderecoAtualizado = new DadosEndereco(
                    "Rua Nova",
                    "Bairro Novo",
                    "87654321",
                    "Rio de Janeiro",
                    "RJ",
                    "456",
                    "Apto 101"
            );
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO(null, null, enderecoAtualizado);

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            service.atualizar(id, dto);

            // Assert
            assertEquals("Rua Nova", usuarioExistente.getEndereco().getLogradouro());
            assertEquals("Bairro Novo", usuarioExistente.getEndereco().getBairro());
        }

        /**
         * Verifica exceção quando usuário não existe.
         */
        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe")
        void atualizar_quandoUsuarioNaoExiste_deveLancarExcecao() {
            // Arrange
            Long idInexistente = 999L;
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO("Novo Nome", null, null);

            when(repository.findAtivoById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.atualizar(idInexistente, dto)
            );
            verify(repository, never()).save(any());
        }

        /**
         * Verifica que campos nulos não alteram dados existentes.
         */
        @Test
        @DisplayName("Não deve atualizar campos quando valores nulos")
        void atualizar_quandoCamposNulos_naoDeveAlterar() {
            // Arrange
            Long id = 1L;
            String nomeOriginal = usuarioExistente.getNome();
            String telefoneOriginal = usuarioExistente.getTelefone();
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO(null, null, null);

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            service.atualizar(id, dto);

            // Assert
            assertEquals(nomeOriginal, usuarioExistente.getNome());
            assertEquals(telefoneOriginal, usuarioExistente.getTelefone());
        }

        /**
         * Verifica que campos vazios (blank) não alteram dados existentes.
         */
        @Test
        @DisplayName("Não deve atualizar campos quando valores vazios")
        void atualizar_quandoCamposVazios_naoDeveAlterar() {
            // Arrange
            Long id = 1L;
            String nomeOriginal = usuarioExistente.getNome();
            String telefoneOriginal = usuarioExistente.getTelefone();
            UsuarioUpdateDTO dto = new UsuarioUpdateDTO("", "   ", null);

            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));
            when(repository.save(any(Usuario.class))).thenReturn(usuarioExistente);

            // Act
            service.atualizar(id, dto);

            // Assert
            assertEquals(nomeOriginal, usuarioExistente.getNome());
            assertEquals(telefoneOriginal, usuarioExistente.getTelefone());
        }
    }

    // ========================================================================
    // BUSCAR USUÁRIO - 6 testes
    // ========================================================================

    @Nested
    @DisplayName("Buscar Usuário")
    class BuscarUsuarioTests {

        private Usuario usuarioExistente;

        @BeforeEach
        void setUp() {
            usuarioExistente = UsuarioTestBuilder.criarUsuarioValido();
        }

        /**
         * Verifica busca por ID com sucesso.
         */
        @Test
        @DisplayName("Deve buscar usuário por ID quando existe")
        void buscarPorId_quandoUsuarioExiste_deveRetornarUsuario() {
            // Arrange
            Long id = 1L;
            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));

            // Act
            UsuarioResponseDTO resultado = service.buscarPorId(id);

            // Assert
            assertNotNull(resultado);
            assertEquals(usuarioExistente.getLogin(), resultado.login());
            assertEquals(usuarioExistente.getEmail(), resultado.email());
        }

        /**
         * Verifica busca por login com normalização.
         */
        @Test
        @DisplayName("Deve buscar usuário por login ignorando case")
        void buscarPorLogin_quandoUsuarioExiste_deveRetornarUsuario() {
            // Arrange
            String login = "USUARIO.TESTE"; // uppercase
            when(repository.findByLoginIgnoreCase("usuario.teste")).thenReturn(Optional.of(usuarioExistente));

            // Act
            UsuarioResponseDTO resultado = service.buscarPorLogin(login);

            // Assert
            assertNotNull(resultado);
            assertEquals(usuarioExistente.getEmail(), resultado.email());
        }

        /**
         * Verifica busca por nome com resultados.
         */
        @Test
        @DisplayName("Deve buscar usuários por nome quando encontra resultados")
        void buscarPorNome_quandoEncontraResultados_deveRetornarLista() {
            // Arrange
            String nome = "João";
            when(repository.findByNomeContainingIgnoreCase("João"))
                    .thenReturn(List.of(usuarioExistente));

            // Act
            List<UsuarioResponseDTO> resultado = service.buscarPorNome(nome);

            // Assert
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
            assertTrue(resultado.get(0).nome().contains("João"));
        }

        /**
         * Verifica exceção quando usuário não encontrado por ID.
         */
        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe por ID")
        void buscarPorId_quandoUsuarioNaoExiste_deveLancarExcecao() {
            // Arrange
            Long idInexistente = 999L;
            when(repository.findAtivoById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.buscarPorId(idInexistente)
            );
        }

        /**
         * Verifica exceção quando nome de busca está vazio.
         */
        @Test
        @DisplayName("Deve lançar exceção quando buscar por nome vazio")
        void buscarPorNome_quandoNomeVazio_deveLancarExcecao() {
            // Arrange
            String nomeVazio = "   ";

            // Act & Assert
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.buscarPorNome(nomeVazio)
            );
            assertEquals("Nome não pode ser vazio para busca.", ex.getMessage());
        }

        /**
         * Verifica retorno de lista vazia quando nenhum usuário encontrado.
         */
        @Test
        @DisplayName("Deve retornar lista vazia quando nenhum usuário encontrado por nome")
        void buscarPorNome_quandoNenhumResultado_deveRetornarListaVazia() {
            // Arrange
            String nome = "NomeInexistente";
            when(repository.findByNomeContainingIgnoreCase("NomeInexistente"))
                    .thenReturn(Collections.emptyList());

            // Act
            List<UsuarioResponseDTO> resultado = service.buscarPorNome(nome);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        /**
         * Verifica busca por email com sucesso.
         */
        @Test
        @DisplayName("Deve buscar usuário por email quando existe")
        void buscarPorEmail_quandoUsuarioExiste_deveRetornarUsuario() {
            // Arrange
            String email = "USUARIO@TESTE.COM"; // uppercase para testar normalização
            when(repository.findByEmailIgnoreCase("usuario@teste.com")).thenReturn(Optional.of(usuarioExistente));

            // Act
            UsuarioResponseDTO resultado = service.buscarPorEmail(email);

            // Assert
            assertNotNull(resultado);
            assertEquals(usuarioExistente.getLogin(), resultado.login());
        }

        /**
         * Verifica exceção quando usuário não encontrado por email.
         */
        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe por email")
        void buscarPorEmail_quandoUsuarioNaoExiste_deveLancarExcecao() {
            // Arrange
            String emailInexistente = "naoexiste@teste.com";
            when(repository.findByEmailIgnoreCase(emailInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.buscarPorEmail(emailInexistente)
            );
            assertTrue(ex.getMessage().contains("email"));
        }

        /**
         * Verifica exceção quando usuário não encontrado por login.
         */
        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe por login")
        void buscarPorLogin_quandoUsuarioNaoExiste_deveLancarExcecao() {
            // Arrange
            String loginInexistente = "login.inexistente";
            when(repository.findByLoginIgnoreCase(loginInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.buscarPorLogin(loginInexistente)
            );
            assertTrue(ex.getMessage().contains("login"));
        }


    }

    // ========================================================================
    // EXCLUIR USUÁRIO - 2 testes
    // ========================================================================

    @Nested
    @DisplayName("Excluir Usuário")
    class ExcluirUsuarioTests {

        private Usuario usuarioExistente;

        @BeforeEach
        void setUp() {
            usuarioExistente = UsuarioTestBuilder.criarUsuarioValido();
        }

        /**
         * Verifica exclusão (soft delete) com sucesso.
         */
        @Test
        @DisplayName("Deve excluir usuário com sucesso quando existe")
        void excluir_quandoUsuarioExiste_deveExcluirComSucesso() {
            // Arrange
            Long id = 1L;
            when(repository.findAtivoById(id)).thenReturn(Optional.of(usuarioExistente));

            // Act
            assertDoesNotThrow(() -> service.excluir(id));

            // Assert
            verify(repository).delete(usuarioExistente);
        }

        /**
         * Verifica exceção quando usuário não existe para exclusão.
         */
        @Test
        @DisplayName("Deve lançar exceção quando usuário não existe para excluir")
        void excluir_quandoUsuarioNaoExiste_deveLancarExcecao() {
            // Arrange
            Long idInexistente = 999L;
            when(repository.findAtivoById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.excluir(idInexistente)
            );
            verify(repository, never()).delete(any());
        }
    }

    // ========================================================================
// LISTAR USUÁRIOS - 3 testes
// ========================================================================

    @Nested
    @DisplayName("Listar Usuários")
    class ListarUsuariosTests {

        private Usuario usuarioExistente;

        @BeforeEach
        void setUp() {
            usuarioExistente = UsuarioTestBuilder.criarUsuarioValido();
        }

        /**
         * Verifica listagem de todos os usuários ativos.
         */
        @Test
        @DisplayName("Deve listar todos os usuários ativos")
        void listarTodos_quandoExistemUsuarios_deveRetornarLista() {
            // Arrange
            when(repository.findAllAtivos()).thenReturn(List.of(usuarioExistente));

            // Act
            List<UsuarioResponseDTO> resultado = service.listarTodos();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(usuarioExistente.getLogin(), resultado.get(0).login());
        }

        /**
         * Verifica listagem vazia quando não há usuários.
         */
        @Test
        @DisplayName("Deve retornar lista vazia quando não há usuários")
        void listarTodos_quandoNaoExistemUsuarios_deveRetornarListaVazia() {
            // Arrange
            when(repository.findAllAtivos()).thenReturn(Collections.emptyList());

            // Act
            List<UsuarioResponseDTO> resultado = service.listarTodos();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        /**
         * Verifica listagem paginada de usuários ativos.
         */
        @Test
        @DisplayName("Deve listar usuários com paginação")
        void listar_quandoChamadoComPageable_deveRetornarPaginado() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Usuario> paginaUsuarios = new PageImpl<>(List.of(usuarioExistente), pageable, 1);
            when(repository.findAllAtivos(pageable)).thenReturn(paginaUsuarios);

            // Act
            Page<UsuarioResponseDTO> resultado = service.listar(pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            assertEquals(1, resultado.getContent().size());
            assertEquals(usuarioExistente.getLogin(), resultado.getContent().get(0).login());
        }
    }

}