package com.restaurantefiap.dto.response;

import com.restaurantefiap.enums.Role;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String email,
        String nome,
        String telefone,
        Role role,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
