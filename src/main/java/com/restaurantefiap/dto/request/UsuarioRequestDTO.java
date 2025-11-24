package com.restaurantefiap.dto.request;

import com.restaurantefiap.enums.Role;

public record UsuarioRequestDTO(
        String email,
        String nome,
        String telefone,
        Role role,
        String password
) {
}
