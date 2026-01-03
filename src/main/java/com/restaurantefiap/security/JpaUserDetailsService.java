// com/fiap/restaurante/security/JpaUserDetailsService.java
package com.restaurantefiap.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurantefiap.entities.usuario.UserPrincipal;
import lombok.RequiredArgsConstructor;
import com.restaurantefiap.repository.UsuarioRepository;

/**
 * Implementação de {@link UserDetailsService} usando JPA.
 *
 * <p>Busca o usuário pelo campo {@code login} e retorna
 * um {@link UserPrincipal} (Adapter) para o Spring Security.</p>
 *
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carrega o usuário pelo login para autenticação.
     *
     * @param login identificador de login do usuário
     * @return {@link UserDetails} com os dados de autenticação
     * @throws UsernameNotFoundException se o login não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String loginNormalizado = normalizar(login);

        return usuarioRepository.findByLoginIgnoreCase(loginNormalizado)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Login não encontrado: " + loginNormalizado));
    }

    /**
     * Normaliza o login: trim e lowercase.
     *
     * @param valor string a normalizar
     * @return string normalizada ou vazia se nula
     */
    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }
}
