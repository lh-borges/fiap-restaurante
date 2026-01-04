package com.restaurantefiap.entities.usuario;

import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.enums.Role;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;

import com.restaurantefiap.validation.ValidationPatterns;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema.
 *
 * <p>Contém dados de autenticação, perfil e endereço.</p>
 *
 * <p><b>Responsabilidades:</b></p>
 * <ul>
 *   <li>Armazenar dados cadastrais do usuário</li>
 *   <li>Aplicar regras de negócio para atualização de perfil</li>
 *   <li>Gerenciar alteração de senha com política e hash</li>
 *   <li>Normalizar campos antes da persistência</li>
 * </ul>
 *
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
@Entity
@Table(name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "login")
        }
)
@SQLDelete(sql = "UPDATE usuarios SET deletado_em = NOW() WHERE id = ?")
@SQLRestriction("deletado_em IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, updatable = false, unique = true, length = 100)
    private String login;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(nullable = false, length = 30)
    @Pattern(regexp = ValidationPatterns.TELEFONE_BR, message = "Telefone inválido (padrão BR)")
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @Embedded
    private Endereco endereco;

    // ========== Métodos de Negócio ==========

    /**
     * Atualiza dados do perfil do usuário.
     *
     * <p>Somente campos não nulos e não vazios são aplicados.
     * <b>Role não é alterável aqui</b> (segurança).
     * login também não é alterável</p>
     *
     * @param origem objeto com novos valores
     */
    public void atualizarPerfil(Usuario origem) {

        if (possuiValor(origem.getNome())) {
            this.nome = origem.getNome();
        }

        if (possuiValor(origem.getTelefone())) {
            this.telefone = origem.getTelefone();
        }

        if (possuiValor(origem.getEmail())) {
            this.email = normalizar(origem.getEmail());
        }


    }

    /**
     * Altera a senha aplicando política e hash.
     *
     * @param senhaPlana senha em texto plano
     * @param policy política de validação
     * @param hasher estratégia de hash
     */
    public void alterarSenha(String senhaPlana, PasswordPolicy policy, PasswordHasher hasher) {
        if (!possuiValor(senhaPlana)) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        policy.validateOrThrow(senhaPlana);
        this.password = hasher.hash(senhaPlana);
    }

    /**
     * Indica se o usuário está ativo (não deletado).
     */
    public boolean estaAtivo() {
        return this.deletadoEm == null;
    }

    // ========== Callbacks JPA ==========

    /**
     * Normaliza campos antes de persistir ou atualizar.
     *
     * <p>Define role padrão como {@link Role#CLIENTE} se não informado.</p>
     */
    @PrePersist
    @PreUpdate
    private void normalizarCampos() {

        this.login = normalizar(this.login);
        this.email = normalizar(this.email);

        if (this.role == null) {
            this.role = Role.CLIENTE;
        }
    }

    // ========== Métodos Auxiliares ==========

    /**
     * Normaliza string: trim + lowercase.
     *
     * @param valor string a normalizar
     * @return string normalizada ou null
     */
    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim().toLowerCase();
    }

    /**
     * Verifica se a string possui valor (não nula e não vazia).
     *
     * @param valor string a verificar
     * @return true se possui valor
     */
    private static boolean possuiValor(String valor) {
        return valor != null && !valor.isBlank();
    }
}