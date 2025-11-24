package com.restaurantefiap.mapper;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.Usuario;

public class UsuarioMapper {

    public static UsuarioResponseDTO toDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getEmail(),
                u.getNome(),
                u.getTelefone(),
                u.getRole(),
                u.getCriadoEm(),
                u.getAtualizadoEm()
        );
    }

    public static Usuario fromDTO(UsuarioRequestDTO dto) {
        Usuario u = new Usuario();
        u.setNome(dto.nome());
        u.setTelefone(dto.telefone());
        u.setRole(dto.role());
        u.setEmail(dto.email());
        return u;
    }
}
