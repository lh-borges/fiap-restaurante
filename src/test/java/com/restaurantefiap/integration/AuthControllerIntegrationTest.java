package com.restaurantefiap.integration;

import com.restaurantefiap.dto.request.AuthRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para {@link com.restaurantefiap.controller.AuthController}.
 * <p>Valida o fluxo completo de autenticação incluindo geração de token JWT.</p>
 *
 * @author Danilo de Paula
 * @since 05/01/2026
 */
class AuthControllerIntegrationTest extends IntegrationTestBase {

    private static final String AUTH_LOGIN_URL = "/v1/auth/login";

    // ========================================================================
    // LOGIN - SUCESSO
    // ========================================================================

    @Nested
    @DisplayName("POST /auth/login - Sucesso")
    class LoginSucessoTests {

        @Test
        @DisplayName("Deve retornar 200 e token quando credenciais válidas")
        void login_quandoCredenciaisValidas_deveRetornarToken() throws Exception {
            AuthRequest request = new AuthRequest(usuarioMaster.getLogin(), SENHA_PADRAO);

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.token",
                            matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")));
        }

        @Test
        @DisplayName("Deve permitir login para qualquer role")
        void login_quandoUsuarioCliente_deveRetornarToken() throws Exception {
            AuthRequest request = new AuthRequest(usuarioCliente.getLogin(), SENHA_PADRAO);

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }
    }

    // ========================================================================
    // LOGIN - FALHA AUTENTICAÇÃO (401)
    // ========================================================================

    @Nested
    @DisplayName("POST /auth/login - Falha Autenticação (401)")
    class LoginFalhaAutenticacaoTests {

        @Test
        @DisplayName("Deve retornar 401 quando senha incorreta")
        void login_quandoSenhaIncorreta_deveRetornar401() throws Exception {
            AuthRequest request = new AuthRequest(usuarioMaster.getLogin(), "SenhaErrada@123");

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.title").value("Authentication Failed"))
                    .andExpect(jsonPath("$.detail").value("Login ou senha inválidos."));
        }

        @Test
        @DisplayName("Deve retornar 401 quando login não existe")
        void login_quandoLoginInexistente_deveRetornar401() throws Exception {
            AuthRequest request = new AuthRequest("usuario.inexistente", SENHA_PADRAO);

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ========================================================================
    // LOGIN - FALHA VALIDAÇÃO (400)
    // ========================================================================

    @Nested
    @DisplayName("POST /auth/login - Falha Validação (400)")
    class LoginFalhaValidacaoTests {

        @Test
        @DisplayName("Deve retornar 400 quando login em branco")
        void login_quandoLoginEmBranco_deveRetornar400() throws Exception {
            AuthRequest request = new AuthRequest("", SENHA_PADRAO);

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    // ✅ comportamento real atual: sem $.errors.*
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.detail").exists());
        }

        @Test
        @DisplayName("Deve retornar 400 quando senha em branco")
        void login_quandoSenhaEmBranco_deveRetornar400() throws Exception {
            AuthRequest request = new AuthRequest(usuarioMaster.getLogin(), "");

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    // ✅ comportamento real atual: sem $.errors.*
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.detail").exists());
        }

        @Test
        @DisplayName("Deve retornar 400 quando login muito curto")
        void login_quandoLoginMuitoCurto_deveRetornar400() throws Exception {
            AuthRequest request = new AuthRequest("abc", SENHA_PADRAO);

            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    // ✅ comportamento real atual: sem $.errors.*
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.detail").exists());
        }

        @Test
        @DisplayName("Deve retornar 400 quando body vazio")
        void login_quandoBodyVazio_deveRetornar400() throws Exception {
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}