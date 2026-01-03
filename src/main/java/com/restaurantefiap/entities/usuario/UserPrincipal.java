package com.restaurantefiap.entities.usuario;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter que traduz a interface {@link UserDetails} (Spring Security)
 * para a entidade de domínio {@link Usuario}.
 *
 * <p>Mantém o domínio desacoplado do framework de segurança.</p>
 *
 * <p><b>Padrão aplicado:</b> Adapter</p>
 *
 * <p><b>Benefícios:</b></p>
 * <ul>
 *   <li>Desacoplamento: modelo de domínio sem dependência de Security</li>
 *   <li>Testabilidade: trocar tecnologia de segurança não afeta a entidade</li>
 *   <li>Responsabilidade única: regras de autenticação fora da entidade</li>
 * </ul>
 *
 * <p><b>Observação:</b> Esta classe NÃO é entidade JPA.</p>
 *
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
@Getter
public final class UserPrincipal implements UserDetails {

    /**
     * -- GETTER --
     *  Retorna a entidade de domínio encapsulada.
     *
     */
    private final Usuario usuario;

    /**
     * Constrói o adapter a partir de um usuário do domínio.
     *
     * @param usuario entidade de domínio
     */
    public UserPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String getUsername() {
        return usuario.getLogin();
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    /**
     * Retorna as autoridades do usuário.
     *
     * @return coleção com a authority do usuário
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" +usuario.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}