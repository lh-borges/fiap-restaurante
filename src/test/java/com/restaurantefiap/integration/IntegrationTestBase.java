package com.restaurantefiap.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;
import com.restaurantefiap.repository.UsuarioRepository;
import com.restaurantefiap.security.PasswordHasher;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Classe base para testes de integração.
 * <p>Configura o contexto Spring completo com banco H2 e usuários de teste
 * pré-cadastrados para cada role do sistema.</p>
 *
 * @author Danilo de Paula
 * @since 05/01/2026
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected PasswordHasher passwordHasher;

    // Usuários de teste (IDs serão preenchidos após persist)
    protected Usuario usuarioMaster;
    protected Usuario usuarioDonoRestaurante;
    protected Usuario usuarioCliente;

    // Senha padrão para todos os usuários de teste
    protected static final String SENHA_PADRAO = "Teste@123";

    private static String sufixoUnico() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeEach
    void setUpBase() {
        // Limpa banco antes de cada teste
        //usuarioRepository.deleteAll();

        // Cria usuários de teste para cada role
        usuarioMaster = criarUsuarioTeste("master.teste." + sufixoUnico(), "master+" + sufixoUnico() + "@teste.com", "Master Teste", Role.MASTER);
        usuarioDonoRestaurante = criarUsuarioTeste("dono.teste." + sufixoUnico(), "dono+" + sufixoUnico() + "@teste.com", "Dono Teste", Role.DONO_RESTAURANTE);
        usuarioCliente = criarUsuarioTeste("cliente.teste." + sufixoUnico(), "cliente+" + sufixoUnico() + "@teste.com", "Cliente Teste", Role.CLIENTE);
    }

    /**
     * Cria e persiste um usuário de teste.
     *
     * @param login login do usuário
     * @param email email do usuário
     * @param nome  nome do usuário
     * @param role  role do usuário
     * @return usuário persistido com ID
     */
    protected Usuario criarUsuarioTeste(String login, String email, String nome, Role role) {
        Usuario usuario = Usuario.builder()
                .login(login)
                .email(email)
                .nome(nome)
                .telefone("11999999999")
                .role(role)
                .password(passwordHasher.hash(SENHA_PADRAO))
                .build();

        return usuarioRepository.save(usuario);
    }

    /**
     * Converte objeto para JSON.
     */
    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}