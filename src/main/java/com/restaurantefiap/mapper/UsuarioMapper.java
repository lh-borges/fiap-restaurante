package com.restaurantefiap.mapper;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.entities.usuario.Usuario;

public class UsuarioMapper {

    public static UsuarioResponseDTO toDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getEmail(),
                u.getNome(),
                u.getTelefone(),
                u.getRole(),
                u.getCriadoEm(),
                u.getAtualizadoEm(),
                toEnderecoDTO(u.getEndereco())
        );
    }

    public static Usuario fromDTO(UsuarioRequestDTO dto) {
        Usuario u = new Usuario();
        u.setNome(dto.nome());
        u.setTelefone(dto.telefone());
        u.setRole(dto.role());
        u.setEmail(dto.email());

        if (dto.endereco() != null) {
            u.setEndereco(new Endereco(dto.endereco()));
        }

        return u;
    }

    private static DadosEndereco toEnderecoDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new DadosEndereco(
                endereco.getLogradouro(),
                endereco.getBairro(),
                endereco.getCep(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getNumero(),
                endereco.getComplemento()
        );
    }
}
