// service/UsuarioService.java
package com.restaurantefiap.service;

import java.util.List;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.exception.DuplicateResourceException;
import com.restaurantefiap.exception.ResourceNotFoundException;
import com.restaurantefiap.mapper.UsuarioMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.repository.UsuarioRepository;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordPolicy policy;
    private final PasswordHasher hasher;

    public UsuarioService(UsuarioRepository repo, PasswordPolicy policy, PasswordHasher hasher) {
        this.repo = repo;
        this.policy = policy;
        this.hasher = hasher;
    }

    // ========= CREATE =========
    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO input) {

        if (input.email() == null || input.email().isBlank()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        }

        final String email = normalize(input.email());

        if (repo.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email", email);
        }

        Usuario u = UsuarioMapper.fromDTO(input);
        u.setEmail(email);

        // regra de domínio: policy + hasher (Strategy)
        u.alterarSenha(input.password(), policy, hasher);

        Usuario salvo = repo.save(u);

        return UsuarioMapper.toDTO(salvo);
    }

    // ========= READ =========
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getByIdDTO(Long id) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        return UsuarioMapper.toDTO(u);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getByEmailDTO(String email) {
        Usuario u = repo.findByEmailIgnoreCase(normalize(email))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
        return UsuarioMapper.toDTO(u);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> list(Pageable pageable) {
        return repo.findAll(pageable).map(UsuarioMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listAll() {
        return repo.findAll()
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findByNomeContaining(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio para busca.");
        }

        return repo.findByNomeContainingIgnoreCase(nome.trim())
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    // ========= UPDATE (perfil) =========
    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioUpdateDTO dto) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            u.setNome(dto.nome());
        }

        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            u.setTelefone(dto.telefone());
        }

        if (dto.endereco() != null) {
            if (u.getEndereco() == null) {
                u.setEndereco(new Endereco(dto.endereco()));
            } else {
                u.getEndereco().atualizarEndereco(dto.endereco());
            }
        }

        Usuario salvo = repo.save(u);
        return UsuarioMapper.toDTO(salvo);
    }

    // ========= CHANGE PASSWORD =========
    @Transactional
    public void changePassword(Long id, String senhaPlana) {
        Usuario u = repo.getReferenceById(id);
        u.alterarSenha(senhaPlana, policy, hasher);
        repo.save(u);
    }

    // ========= DELETE =========
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Usuário", id);
        }
        repo.deleteById(id);
    }

    // ========= helpers =========
    private static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
