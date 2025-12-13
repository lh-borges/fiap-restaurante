package com.restaurantefiap.dto.request;

import com.restaurantefiap.entities.endereco.DadosEndereco;

public record UsuarioUpdateDTO(
        String nome,
        String telefone,
        DadosEndereco endereco
) {
}
