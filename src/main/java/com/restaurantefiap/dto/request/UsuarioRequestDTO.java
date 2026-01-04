package com.restaurantefiap.dto.request;

import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.enums.Role;

import com.restaurantefiap.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisições de criação/atualização de usuário.
 *
 * @param login    identificador único para autenticação
 * @param email    email do usuário
 * @param nome     nome completo
 * @param telefone telefone de contato
 * @param role     papel/perfil do usuário
 * @param password senha em texto plano (será hasheada)
 * @param endereco dados de endereço (opcional)
 *
 * @author Juliana Olio
 * @author Danilo de Paula
 */
public record UsuarioRequestDTO(

        @NotBlank(message = "Login é obrigatório")
        @Size(min = 5, max = 100, message = "Login deve ter entre 5 e 100 caracteres")
        @Pattern(regexp = ValidationPatterns.LOGIN_USUARIO,
                message = "Aceita apenas letras minúsculas, números, underscores, ponto e hífens")
        String login,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
        String email,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
        @Pattern(regexp = ValidationPatterns.NOME_PROPRIO, message = "O nome deve conter apenas letras")
        String nome,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        @Pattern(regexp = ValidationPatterns.TELEFONE_BR, message = "Telefone inválido (padrão BR)")
        String telefone,

        Role role,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
        @Pattern(regexp = ValidationPatterns.SENHA_FORTE, message = "A senha deve conter ao menos 8 caracteres, letras, números e símbolos")
        String password,

        @Valid
        DadosEndereco endereco

) {
}