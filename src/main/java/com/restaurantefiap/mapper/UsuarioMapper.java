package com.restaurantefiap.mapper;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.entities.endereco.Endereco;
import com.restaurantefiap.entities.usuario.Usuario;

/**
 * Mapper para conversão entre entidade {@link Usuario} e seus DTOs.
 *
 * <p>Classe utilitária com métodos estáticos — não deve ser instanciada.</p>
 *
 * @author Juliana Olio
 * @author Danilo Fernando
 */
public final class UsuarioMapper {

    /**
     * Construtor privado para impedir instanciação.
     */
    private UsuarioMapper() {
        // Utility class
    }

    // ========== Entidade -> DTO ==========

    /**
     * Converte entidade {@link Usuario} para {@link UsuarioResponseDTO}.
     *
     * @param usuario entidade a ser convertida
     * @return DTO de resposta
     */
    public static UsuarioResponseDTO paraDto(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getTelefone(),
                usuario.getRole(),
                usuario.getCriadoEm(),
                usuario.getAtualizadoEm(),
                paraEnderecoDto(usuario.getEndereco())
        );
    }

    /**
     * Converte {@link Endereco} para {@link DadosEndereco}.
     *
     * @param endereco entidade de endereço
     * @return DTO de endereço ou null se entrada for null
     */
    private static DadosEndereco paraEnderecoDto(Endereco endereco) {
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

    // ========== DTO -> Entidade ==========

    /**
     * Converte {@link UsuarioRequestDTO} para entidade {@link Usuario}.
     *
     * <p>A senha NÃO é definida aqui — deve ser hasheada no service.</p>
     *
     * @param dto DTO de requisição
     * @return entidade Usuario (sem ID e sem senha)
     */
    public static Usuario paraEntidade(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setLogin(dto.login());
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setRole(dto.role());

        if (dto.endereco() != null) {
            usuario.setEndereco(new Endereco(dto.endereco()));
        }

        return usuario;
    }
}