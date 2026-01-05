package com.restaurantefiap.entities.endereco;

import com.restaurantefiap.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Representa os dados de endereço de um usuário ou estabelecimento.
 * <p>Este Record atua como um Value Object imutável, garantindo que as informações
 * de localização sejam transportadas com integridade e validadas na entrada.</p>
 * @param logradouro  Nome da rua, avenida ou logradouro.
 * @param bairro      Bairro ou distrito.
 * @param cep         Código de Endereçamento Postal (apenas números).
 * @param cidade      Nome da cidade.
 * @param uf          Sigla da Unidade Federativa (Estado).
 * @param numero      Número da residência ou estabelecimento.
 * @param complemento Informações adicionais (apartamento, bloco, etc).
 * * @author Danilo Fernando
 * @since 04/01/2026
 */
public record DadosEndereco(
        @NotBlank(message = "Logradouro é obrigatório")
        String logradouro,

        @NotBlank(message = "Bairro é obrigatório")
        String bairro,

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = ValidationPatterns.CEP, message = "CEP deve estar no formato 00000-000 ou 00000000")
        String cep,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotBlank(message = "UF é obrigatória")
        @Pattern(regexp = ValidationPatterns.UF_BR, message = "UF deve ser uma sigla válida de estado brasileiro (ex: SP, RJ)")
        String uf,

        @NotBlank(message = "Número é obrigatório")
        String numero,

        String complemento
) {
}