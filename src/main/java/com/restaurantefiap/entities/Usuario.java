// com/fiap/restaurante/entities/Usuario.java
package com.restaurantefiap.entities;

import com.restaurantefiap.enums.Role;
import com.restaurantefiap.security.PasswordHasher;
import com.restaurantefiap.security.PasswordPolicy;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
