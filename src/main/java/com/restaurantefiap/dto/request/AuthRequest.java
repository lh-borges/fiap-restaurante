package com.restaurantefiap.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(

        @NotBlank(message = "Login é obrigatório")
        @Size(min = 6, max = 100, message = "Login deve ter entre 6 e 100 caracteres")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
        String password
) {
}
