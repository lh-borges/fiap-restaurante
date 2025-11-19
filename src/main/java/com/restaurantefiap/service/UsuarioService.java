// service/UsuarioService.java
package com.restaurantefiap.service;

import java.util.List;

import com.restaurantefiap.exception.DuplicateResourceException;
import com.restaurantefiap.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantefiap.entities.Usuario;
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
    public Usuario create(Usuario input) {

        if (input.getEmail() == null || input.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        }

        final String email = normalize(input.getEmail());

        if (repo.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email", email);
        }

        Usuario u = new Usuario();
        u.setNome(input.getNome());
        u.setTelefone(input.getTelefone());
        u.setRole(input.getRole());
        u.setEmail(email);

        // regra de domínio: policy + hasher (Strategy)
        u.alterarSenha(input.getPassword(), policy, hasher);

        Usuario salvo = repo.save(u);

        salvo.setPassword(null);

        return salvo;
    }

    // ========= READ =========
    @Transactional(readOnly = true)
    public Usuario getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    @Transactional(readOnly = true)
    public Usuario getByEmail(String email) {
        final String key = normalize(email);
        return repo.findByEmailIgnoreCase(key)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", key));
    }

    @Transactional(readOnly = true)
    public Page<Usuario> list(Pageable pageable) { return repo.findAll(pageable); }

    @Transactional(readOnly = true)
    public List<Usuario> listAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public List<Usuario> findByNomeContaining(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio para busca.");
        }
        return repo.findByNomeContainingIgnoreCase(nome.trim());
    }

    // ========= UPDATE (perfil) =========
    @Transactional
    public Usuario update(Long id, Usuario changes) {
        Usuario u = getById(id);

        if (changes.getEmail() != null && !changes.getEmail().isBlank()) {
            final String newEmail = normalize(changes.getEmail());
        }

        // domínio aplica as mudanças (normalização já feita acima)
        u.atualizarPerfil(changes);

        return repo.save(u);
    }

    // ========= CHANGE PASSWORD =========
    @Transactional
    public void changePassword(Long id, String senhaPlana) {
 
        Usuario u = getById(id);
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
