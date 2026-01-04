package com.restaurantefiap.dto.response;

import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.enums.Role;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String login,
        String email,
        String nome,
        String telefone,
        Role role,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        DadosEndereco endereco
) {
}
