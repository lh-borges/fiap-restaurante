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

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final String key = email == null ? "" : email.trim();
        return usuarioRepository.findByEmailIgnoreCase(key)
                .map(UserPrincipal::new) // ADAPTER aqui
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado: " + key));
    }
}
