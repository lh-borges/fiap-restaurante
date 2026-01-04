package com.restaurantefiap.dto.request;

import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(

        @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
        @Pattern(regexp = ValidationPatterns.NOME_PROPRIO, message = "Numeros e simbolos não são aceitos")
        String nome,

        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        @Pattern(regexp = ValidationPatterns.TELEFONE_BR, message = "Telefone inválido (padrão BR)")
        String telefone,

        @Valid
        DadosEndereco endereco
) {
}
