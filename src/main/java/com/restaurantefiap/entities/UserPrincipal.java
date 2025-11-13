// com/fiap/restaurante/security/UserPrincipal.java
package com.restaurantefiap.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Adapter
 * ------------------------------------------------------------
 * Função: traduz a interface esperada pelo cliente (Spring Security via UserDetails)
 * para a interface/classe que já existe no domínio (Usuario). Em vez de a entidade
 * Usuario implementar UserDetails (acoplando o domínio ao framework), criamos este
 * "adaptador" que encapsula Usuario e expõe os métodos exigidos pelo Spring.
 *
 * Benefícios:
 *  - Desacoplamento: o modelo de domínio permanece limpo, sem dependência de Security.
 *  - Testabilidade e evolução: trocar/atualizar a tecnologia de segurança não exige
 *    alterar a entidade de domínio nem seus testes.
 *  - Responsabilidade única: regras de autenticação/autorização ficam fora da entidade.
 *
 * Observações:
 *  - Esta classe NÃO é entidade JPA (não anotar com @Entity, nem criar repository).
 *  - O prefixo "ROLE_" é aplicado aqui para compatibilidade com o Spring.
 */

public final class UserPrincipal implements UserDetails {
    private final Usuario user;

    public UserPrincipal(Usuario user) { this.user = user; }

    public Usuario getUsuario() { return user; }

    @Override public String getUsername() { return user.getEmail(); }
    @Override public String getPassword() { return user.getPassword(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefixo ROLE_ (exigido pelo Spring)
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
