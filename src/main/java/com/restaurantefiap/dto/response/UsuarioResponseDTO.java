package com.restaurantefiap.dto.response;

import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.enums.Role;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para representação detalhada de um usuário nas respostas da API.
 * <p>Utiliza o recurso de Records do Java para garantir imutabilidade e concisão no transporte
 * de dados entre a camada de serviço e o cliente final.</p>
 * @param id           Identificador único do usuário no banco de dados.
 * @param login        Nome de usuário utilizado para autenticação.
 * @param email        Endereço de e-mail principal.
 * @param nome         Nome completo ou razão social.
 * @param telefone     Número de contato formatado.
 * @param role         Perfil de acesso (MASTER, DONO_RESTAURANTE, CLIENTE, etc).
 * @param criadoEm     Data e hora em que o registro foi criado.
 * @param atualizadoEm Data e hora da última modificação no cadastro.
 * @param endereco     Objeto contendo os detalhes geográficos do usuário.
 * @author Juliana Olio
 * @author Danilo Fernando
 * @since 04/01/2026
 */
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