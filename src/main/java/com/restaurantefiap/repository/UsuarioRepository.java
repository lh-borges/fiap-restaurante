// com/fiap/restaurante/repository/UsuarioRepository.java
package com.restaurantefiap.repository;

import com.restaurantefiap.entities.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}
