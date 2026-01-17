package com.restaurantefiap.security;

import com.restaurantefiap.entities.usuario.UserPrincipal;
import com.restaurantefiap.enums.Role;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service para verificação de autorização de acesso a recursos.
 *
 * <p>Centraliza a lógica de ownership e verificação de roles,
 * permitindo uso via SpEL em anotações {@code @PreAuthorize}.</p>
 *
 * <p><b>Padrão aplicado:</b> Authorization Service Pattern</p>
 *
 * @author Danilo de Paula
 */
@Service("autorizacaoService")
public class AutorizacaoService {

    /**
     * Verifica se o usuário autenticado é o próprio dono do recurso.
     *
     * <p>Usado para operações que somente o próprio usuário pode executar,
     * como troca de senha.</p>
     *
     * @param idRecurso ID do recurso sendo acessado
     * @return true se o usuário autenticado é o dono do recurso
     */
    public boolean isProprioUsuario(Long idRecurso) {
        Long idUsuarioLogado = obterIdUsuarioLogado();
        return idUsuarioLogado != null && idUsuarioLogado.equals(idRecurso);
    }

    /**
     * Verifica se o usuário é admin (MASTER ou DONO_RESTAURANTE) ou o próprio dono do recurso.
     *
     * <p>Usado para operações de leitura/atualização de dados do usuário.</p>
     *
     * @param idRecurso ID do recurso sendo acessado
     * @return true se é admin ou próprio usuário
     */
    public boolean isAdminOuProprio(Long idRecurso) {
        return isAdmin() || isProprioUsuario(idRecurso);
    }

    /**
     * Verifica se o usuário possui role administrativa (MASTER ou DONO_RESTAURANTE).
     *
     * <p>Usado para operações de listagem e busca de usuários.</p>
     *
     * @return true se é MASTER ou DONO_RESTAURANTE
     */
    public boolean isAdmin() {
        Role role = obterRoleUsuarioLogado();
        return role == Role.MASTER || role == Role.DONO_RESTAURANTE;
    }

    /**
     * Verifica se o usuário possui role MASTER.
     *
     * <p>Usado para operações críticas como exclusão de usuários.</p>
     *
     * @return true se é MASTER
     */
    public boolean isMaster() {
        return obterRoleUsuarioLogado() == Role.MASTER;
    }

    // ========== Métodos Auxiliares ==========

    /**
     * Obtém o ID do usuário logado a partir do contexto de segurança.
     *
     * @return ID do usuário ou null se não autenticado
     */
    private Long obterIdUsuarioLogado() {
        UserPrincipal principal = obterPrincipal();
        return principal != null ? principal.getUsuario().getId() : null;
    }

    /**
     * Verifica se o usuário é MASTER ou o próprio dono do recurso.
     *
     * <p>Usado para operações sensíveis como alteração de senha,
     * onde apenas o próprio usuário ou um MASTER pode executar.</p>
     *
     * @param idRecurso ID do recurso sendo acessado
     * @return true se é MASTER ou próprio usuário
     */
    public boolean isMasterOuProprio(Long idRecurso) {
        return isMaster() || isProprioUsuario(idRecurso);
    }


    /**
     * Obtém a role do usuário logado a partir do contexto de segurança.
     *
     * @return Role do usuário ou null se não autenticado
     */
    private Role obterRoleUsuarioLogado() {
        UserPrincipal principal = obterPrincipal();
        return principal != null ? principal.getUsuario().getRole() : null;
    }

    /**
     * Obtém o UserPrincipal do contexto de segurança.
     *
     * @return UserPrincipal ou null se não autenticado
     */
    private UserPrincipal obterPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }

        return null;
    }

}
