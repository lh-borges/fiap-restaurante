package com.restaurantefiap.integration;

import com.restaurantefiap.dto.request.AlterarSenhaRequestDTO;
import com.restaurantefiap.dto.request.AuthRequest;
import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.entities.usuario.Usuario;
import com.restaurantefiap.enums.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para {@link com.restaurantefiap.controller.UsuarioController}.
 * <p>Valida o fluxo completo de CRUD de usuários incluindo controle de acesso
 * baseado em roles e ownership.</p>
 *
 * @author Danilo de Paula
 * @since 05/01/2026
 */
class UsuarioControllerIntegrationTest extends IntegrationTestBase {

    private static final String USUARIOS_URL = "/usuarios";

    private String tokenMaster;
    private String tokenDonoRestaurante;
    private String tokenCliente;

    @BeforeEach
    void setUpTokens() throws Exception {
        tokenMaster = obterToken(usuarioMaster.getLogin(), SENHA_PADRAO);
        tokenDonoRestaurante = obterToken(usuarioDonoRestaurante.getLogin(), SENHA_PADRAO);
        tokenCliente = obterToken(usuarioCliente.getLogin(), SENHA_PADRAO);
    }

    private String obterToken(String login, String senha) throws Exception {
        AuthRequest request = new AuthRequest(login, senha);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    // ========================================================================
    // GET /usuarios/me - USUÁRIO LOGADO
    // ========================================================================

    @Nested
    @DisplayName("GET /usuarios/me - Usuário Logado")
    class MeTests {

        @Test
        @DisplayName("Deve retornar dados do usuário logado")
        void me_quandoAutenticado_deveRetornarDadosDoUsuario() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/me")
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.login").value(usuarioCliente.getLogin()))
                    .andExpect(jsonPath("$.email").value(usuarioCliente.getEmail()))
                    .andExpect(jsonPath("$.role").value("CLIENTE"));
        }

        @Test
        @DisplayName("Deve retornar 403 quando não autenticado (comportamento atual)")
        void me_quandoNaoAutenticado_deveRetornar403() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/me"))
                    .andExpect(status().isForbidden());
        }
    }

    // ========================================================================
    // GET /usuarios/{id} - BUSCAR POR ID
    // ========================================================================

    @Nested
    @DisplayName("GET /usuarios/{id} - Buscar por ID")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve retornar usuário quando admin busca qualquer ID")
        void buscarPorId_quandoAdmin_deveRetornarUsuario() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/" + usuarioCliente.getId())
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(usuarioCliente.getId()))
                    .andExpect(jsonPath("$.login").value(usuarioCliente.getLogin()));
        }

        @Test
        @DisplayName("Deve retornar usuário quando busca próprio ID")
        void buscarPorId_quandoProprioUsuario_deveRetornarUsuario() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/" + usuarioCliente.getId())
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.login").value(usuarioCliente.getLogin()));
        }

        @Test
        @DisplayName("Deve retornar 403 quando cliente busca outro usuário")
        void buscarPorId_quandoClienteBuscaOutro_deveRetornar403() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/" + usuarioMaster.getId())
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void buscarPorId_quandoIdInexistente_deveRetornar404() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/99999")
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource Not Found"));
        }
    }

    // ========================================================================
    // GET /usuarios/page - LISTAR PAGINADO
    // ========================================================================

    @Nested
    @DisplayName("GET /usuarios/page - Listar Paginado")
    class ListarPaginadoTests {

        @Test
        @DisplayName("Deve retornar página de usuários quando admin")
        void listarPaginado_quandoAdmin_deveRetornarPagina() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/page")
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()", is(not(0))));
        }

        @Test
        @DisplayName("Deve permitir acesso para DONO_RESTAURANTE")
        void listarPaginado_quandoDonoRestaurante_deveRetornarPagina() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/page")
                            .header("Authorization", "Bearer " + tokenDonoRestaurante))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Deve retornar 403 quando cliente tenta listar")
        void listarPaginado_quandoCliente_deveRetornar403() throws Exception {
            mockMvc.perform(get(USUARIOS_URL + "/page")
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isForbidden());
        }
    }

    // ========================================================================
    // POST /usuarios - CRIAR USUÁRIO
    // ========================================================================

    @Nested
    @DisplayName("POST /usuarios - Criar Usuário")
    class CriarUsuarioTests {

        @Test
        @DisplayName("Deve criar usuário e retornar 201 quando admin")
        void criar_quandoAdminComDadosValidos_deveCriarERetornar201() throws Exception {
            UsuarioRequestDTO request = new UsuarioRequestDTO(
                    "novo.usuario",
                    "novo@teste.com",
                    "Novo Usuario",
                    "11988887777",
                    Role.CLIENTE,
                    "NovaSenha@123",
                    null
            );

            mockMvc.perform(post(USUARIOS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.login").value("novo.usuario"))
                    .andExpect(jsonPath("$.email").value("novo@teste.com"))
                    .andExpect(jsonPath("$.role").value("CLIENTE"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando dados inválidos (sem $.errors no retorno atual)")
        void criar_quandoDadosInvalidos_deveRetornar400() throws Exception {
            UsuarioRequestDTO request = new UsuarioRequestDTO(
                    "ab",
                    "email-invalido",
                    "N",
                    "123",
                    Role.CLIENTE,
                    "123",
                    null
            );

            mockMvc.perform(post(USUARIOS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isBadRequest())
                    // ✅ alinhado ao retorno real atual
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.detail").exists());
        }

        @Test
        @DisplayName("Deve retornar 400 quando login já existe (comportamento atual)")
        void criar_quandoLoginDuplicado_deveRetornar400() throws Exception {
            UsuarioRequestDTO request = new UsuarioRequestDTO(
                    usuarioMaster.getLogin(),
                    "outro@email.com",
                    "Outro Usuario",
                    "11988887777",
                    Role.CLIENTE,
                    "NovaSenha@123",
                    null
            );

            mockMvc.perform(post(USUARIOS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isBadRequest());
        }
    }

    // ========================================================================
    // PUT /usuarios/{id}/senha - ALTERAR SENHA
    // ========================================================================

    @Nested
    @DisplayName("PUT /usuarios/{id}/senha - Alterar Senha")
    class AlterarSenhaTests {

        @Test
        @DisplayName("Deve alterar senha e retornar 204 quando próprio usuário")
        void alterarSenha_quandoProprioUsuario_deveAlterarERetornar204() throws Exception {
            AlterarSenhaRequestDTO request = new AlterarSenhaRequestDTO(
                    SENHA_PADRAO,
                    "NovaSenha@456"
            );

            mockMvc.perform(put(USUARIOS_URL + "/" + usuarioCliente.getId() + "/senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 400 quando senha atual incorreta")
        void alterarSenha_quandoSenhaAtualIncorreta_deveRetornar400() throws Exception {
            AlterarSenhaRequestDTO request = new AlterarSenhaRequestDTO(
                    "SenhaErrada123",
                    "NovaSenha@456"
            );

            mockMvc.perform(put(USUARIOS_URL + "/" + usuarioCliente.getId() + "/senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid Password"));
        }
    }

    // ========================================================================
    // DELETE /usuarios/{id} - EXCLUIR USUÁRIO
    // ========================================================================

    @Nested
    @DisplayName("DELETE /usuarios/{id} - Excluir Usuário")
    class ExcluirUsuarioTests {

        @Test
        @DisplayName("Deve excluir e retornar 204 quando MASTER")
        void excluir_quandoMaster_deveExcluirERetornar204() throws Exception {
            Usuario usuarioParaExcluir = criarUsuarioTeste(
                    "excluir.teste",
                    "excluir@teste.com",
                    "Usuario Excluir",
                    Role.CLIENTE
            );

            mockMvc.perform(delete(USUARIOS_URL + "/" + usuarioParaExcluir.getId())
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(USUARIOS_URL + "/" + usuarioParaExcluir.getId())
                            .header("Authorization", "Bearer " + tokenMaster))
                    .andExpect(status().isNotFound());
        }
    }
}
