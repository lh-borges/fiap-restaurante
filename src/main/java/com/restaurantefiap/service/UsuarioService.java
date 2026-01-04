package com.restaurantefiap.service;

import com.restaurantefiap.dto.request.AlterarSenhaRequestDTO;
import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.exception.DuplicateResourceException;
import com.restaurantefiap.exception.InvalidPasswordException;
import com.restaurantefiap.exception.ResourceNotFoundException;
import com.restaurantefiap.mapper.UsuarioMapper;
import com.restaurantefiap.repository.UsuarioRepository;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service para operações de negócio relacionadas a {@link Usuario}.
 *
 * <p>Gerencia criação, atualização, busca e exclusão (soft delete) de usuários.</p>
 *
 * @author Thiago de Jesus
 * @author Danilo de Paula
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordHasher passwordHasher;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordPolicy passwordPolicy,
            PasswordHasher passwordHasher
    ) {
        this.repository = repository;
        this.passwordPolicy = passwordPolicy;
        this.passwordHasher = passwordHasher;
    }

    // ========== CREATE ==========

    /**
     * Cria um novo usuário.
     *
     * <p>Valida duplicidade de login e email antes de persistir.
     * A senha é validada pela policy e hasheada.</p>
     *
     * @param dto dados do usuário
     * @return DTO do usuário criado
     * @throws IllegalArgumentException    se login ou email forem vazios
     * @throws DuplicateResourceException  se login ou email já existirem
     */
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        validarCamposObrigatorios(dto);

        String loginNormalizado = normalizar(dto.login());
        String emailNormalizado = normalizar(dto.email());

        validarDuplicidade(loginNormalizado, emailNormalizado);

        Usuario usuario = UsuarioMapper.paraEntidade(dto);
        usuario.setLogin(loginNormalizado);
        usuario.setEmail(emailNormalizado);
        usuario.alterarSenha(dto.password(), passwordPolicy, passwordHasher);

        Usuario salvo = repository.save(usuario);

        return UsuarioMapper.paraDto(salvo);
    }

    // ========== READ ==========

    /**
     * Busca usuário ativo por ID.
     *
     * @param id identificador do usuário
     * @return DTO do usuário
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = buscarUsuarioAtivoPorId(id);
        return UsuarioMapper.paraDto(usuario);
    }

    /**
     * Busca usuário ativo por login.
     *
     * @param login identificador de login
     * @return DTO do usuário
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorLogin(String login) {
        Usuario usuario = repository.findByLoginIgnoreCase(normalizar(login))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "login", login));

        return UsuarioMapper.paraDto(usuario);
    }

    /**
     * Busca usuário ativo por email.
     *
     * @param email email do usuário
     * @return DTO do usuário
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = repository.findByEmailIgnoreCase(normalizar(email))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));

        return UsuarioMapper.paraDto(usuario);
    }

    /**
     * Lista usuários ativos com paginação.
     *
     * @param pageable configuração de paginação
     * @return página de usuários
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return repository.findAllAtivos(pageable)
                .map(UsuarioMapper::paraDto);
    }

    /**
     * Busca usuários ativos pelo nome (busca parcial).
     *
     * @param nome termo de busca
     * @return lista de usuários encontrados
     * @throws IllegalArgumentException se nome for vazio
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        if (!possuiValor(nome)) {
            throw new IllegalArgumentException("Nome não pode ser vazio para busca.");
        }

        return repository.findByNomeContainingIgnoreCase(nome.trim())
                .stream()
                .map(UsuarioMapper::paraDto)
                .toList();
    }

    // ========== UPDATE ==========

    /**
     * Atualiza dados do perfil do usuário.
     *
     * <p>Atualiza apenas campos não nulos do DTO.</p>
     *
     * @param id  identificador do usuário
     * @param dto dados a atualizar
     * @return DTO do usuário atualizado
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = buscarUsuarioAtivoPorId(id);

        atualizarCamposBasicos(usuario, dto);
        atualizarEndereco(usuario, dto);

        Usuario salvo = repository.save(usuario);

        return UsuarioMapper.paraDto(salvo);
    }

    /**
     * Altera a senha do usuário.
     *
     * <p>Valida a senha atual antes de permitir a alteração.
     * A nova senha é validada pela policy e hasheada.</p>
     *
     * @param id  identificador do usuário
     * @param dto dados com senha atual e nova senha
     * @throws ResourceNotFoundException se usuário não encontrado
     * @throws InvalidPasswordException  se senha atual incorreta
     * @throws IllegalArgumentException  se nova senha não atende à policy
     */
    @Transactional
    public void alterarSenha(Long id, AlterarSenhaRequestDTO dto) {
        Usuario usuario = buscarUsuarioAtivoPorId(id);

        validarSenhaAtual(usuario, dto.senhaAtual());

        usuario.alterarSenha(dto.novaSenha(), passwordPolicy, passwordHasher);
        repository.save(usuario);
    }

    /**
     * Valida se a senha atual informada corresponde à senha do usuário.
     *
     * @param usuario    usuário a validar
     * @param senhaAtual senha informada pelo usuário
     * @throws InvalidPasswordException se senha não corresponde
     */
    private void validarSenhaAtual(Usuario usuario, String senhaAtual) {
        if (!passwordHasher.matches(senhaAtual, usuario.getPassword())) {
            throw new InvalidPasswordException("Senha atual incorreta");
        }
    }

    // ========== DELETE (Soft Delete) ==========

    /**
     * Exclui usuário (soft delete).
     *
     * <p>Marca o campo {@code deletadoEm} com a data atual.</p>
     *
     * @param id identificador do usuário
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarUsuarioAtivoPorId(id);
        repository.delete(usuario);
    }


    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAllAtivos()
                .stream()
                .map(UsuarioMapper::paraDto)
                .toList();
    }


    // ========== Métodos Auxiliares (privados) ==========

    /**
     * Busca usuário ativo por ID ou lança exceção.
     */
    private Usuario buscarUsuarioAtivoPorId(Long id) {
        return repository.findAtivoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    /**
     * Valida campos obrigatórios do DTO de criação.
     */
    private void validarCamposObrigatorios(UsuarioRequestDTO dto) {
        if (!possuiValor(dto.login())) {
            throw new IllegalArgumentException("Login não pode ser vazio.");
        }
        if (!possuiValor(dto.email())) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        }
    }

    /**
     * Valida se login ou email já existem no banco.
     */
    private void validarDuplicidade(String login, String email) {
        if (repository.existsByLoginIgnoreCase(login)) {
            throw new DuplicateResourceException("Login", login);
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email", email);
        }
    }

    /**
     * Atualiza campos básicos do usuário (nome, telefone).
     */
    private void atualizarCamposBasicos(Usuario usuario, UsuarioUpdateDTO dto) {
        if (possuiValor(dto.nome())) {
            usuario.setNome(dto.nome());
        }
        if (possuiValor(dto.telefone())) {
            usuario.setTelefone(dto.telefone());
        }
    }

    /**
     * Atualiza ou cria endereço do usuário.
     */
    private void atualizarEndereco(Usuario usuario, UsuarioUpdateDTO dto) {
        if (dto.endereco() == null) {
            return;
        }

        if (usuario.getEndereco() == null) {
            usuario.setEndereco(new Endereco(dto.endereco()));
        } else {
            usuario.getEndereco().atualizarEndereco(dto.endereco());
        }
    }

    /**
     * Normaliza string: trim + lowercase.
     */
    private String normalizar(String valor) {
        return valor == null ? null : valor.trim().toLowerCase();
    }

    /**
     * Verifica se string possui valor (não nula e não vazia).
     */
    private boolean possuiValor(String valor) {
        return valor != null && !valor.isBlank();
    }
}