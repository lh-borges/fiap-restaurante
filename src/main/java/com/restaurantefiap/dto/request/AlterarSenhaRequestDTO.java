package com.restaurantefiap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de alteração de senha.
 *
 * @author Danilo de Paula
 */
@Schema(description = "Dados para alteração de senha")
public record AlterarSenhaRequestDTO(

        @Schema(description = "Senha atual do usuário", example = "SenhaAtual123")
        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual,

        @Schema(description = "Nova senha do usuário", example = "NovaSenha456")
        @NotBlank(message = "Nova senha é obrigatória")
        String novaSenha
) {}
