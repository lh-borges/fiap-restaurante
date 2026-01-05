package com.restaurantefiap.util;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.entities.endereco.DadosEndereco;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;

/**
 * Fábrica de objetos para suporte a testes (Test Data Builder).
 * <p>Centraliza a criação de instâncias de modelos e DTOs para garantir
 * consistência nos dados de teste e facilitar a manutenção dos casos de teste.</p>
 * @author Danilo Fernando
 * @since 04/01/2026
 */
public final class UsuarioTestBuilder {

    private UsuarioTestBuilder() {
        // Construtor privado para impedir instanciação de classe utilitária
    }

    /**
     * Constrói um {@link UsuarioRequestDTO} pré-preenchido com dados válidos
     * que atendem a todas as restrições de validação (Bean Validation).
     * @return Uma instância de DTO pronta para ser enviada em requisições POST/PUT.
     */
    public static UsuarioRequestDTO criarUsuarioRequestDTOValido() {
        return new UsuarioRequestDTO(
                "usuario.teste",
                "usuario@teste.com",
                "João Silva",
                "11987654321",
                Role.CLIENTE,
                "ValidPass123",
                criarEnderecoValido()
        );
    }

    /**
     * Constrói um objeto {@link DadosEndereco} com valores fictícios válidos.
     * @return Um Value Object de endereço pronto para uso.
     */
    public static DadosEndereco criarEnderecoValido() {
        return new DadosEndereco(
                "Rua Teste",
                "Centro",
                "12345678",
                "São Paulo",
                "SP",
                "123",
                null
        );
    }

    /**
     * Constrói uma entidade {@link Usuario} populada, simulando um objeto
     * já persistido ou pronto para manipulação na camada de serviço.
     * @return Uma entidade Usuario com ID e senha (hash) definidos.
     */
    public static Usuario criarUsuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("usuario.teste");
        usuario.setEmail("usuario@teste.com");
        usuario.setNome("João Silva");
        usuario.setTelefone("11987654321");
        usuario.setRole(Role.CLIENTE);
        usuario.setPassword("hashedPassword"); // Simula senha já criptografada
        return usuario;
    }
}