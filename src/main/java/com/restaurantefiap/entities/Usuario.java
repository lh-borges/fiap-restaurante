// com/fiap/restaurante/entities/Usuario.java
package com.restaurantefiap.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.restaurantefiap.enums.Role;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;

import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "password") // evita logar senha por acidente
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Email
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(nullable = false, length = 30)
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

    /** Regras de atualização do perfil: normaliza e aplica apenas valores válidos. */
    public void atualizarPerfil(Usuario from) {
        if (from.getNome() != null && !from.getNome().isBlank())         this.nome = from.getNome();
        if (from.getTelefone() != null && !from.getTelefone().isBlank()) this.telefone = from.getTelefone();
        if (from.getRole() != null)                                       this.role = from.getRole();
        if (from.getEmail() != null && !from.getEmail().isBlank())        this.email = normalize(from.getEmail());
    }

    /** Troca a senha aplicando política e hash via portas (SOLID: Strategy + DIP). */
    public void alterarSenha(String senhaPlana, PasswordPolicy policy, PasswordHasher hasher) {
        if (senhaPlana == null || senhaPlana.isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        policy.validateOrThrow(senhaPlana);
        this.password = hasher.hash(senhaPlana);
    }

    @PrePersist @PreUpdate
    private void normalizeFields() {
        this.email = normalize(this.email);
        if (this.role == null) this.role = Role.ROLE_USER;
    }

    // ---- Helpers ----
    private static String normalize(String e) {
        return e == null ? null : e.trim().toLowerCase();
    }
}
