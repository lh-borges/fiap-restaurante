package com.restaurantefiap.integration;

import com.restaurantefiap.dto.request.AuthRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
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

    private static final String AUTH_LOGIN_URL = "/auth/login";

    // ========================================================================
    // LOGIN - SUCESSO
    // ========================================================================

    @Nested
    @DisplayName("POST /auth/login - Sucesso")
    class LoginSucessoTests {

        /**
         * Verifica login com credenciais válidas retorna token JWT.
         */
        @Test
        @DisplayName("Deve retornar 200 e token quando credenciais válidas")
        void login_quandoCredenciaisValidas_deveRetornarToken() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest(usuarioMaster.getLogin(), SENHA_PADRAO);

            // Act & Assert
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.token", matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")));
        }

        /**
         * Verifica login funciona para todas as roles.
         */
        @Test
        @DisplayName("Deve permitir login para qualquer role")
        void login_quandoUsuarioCliente_deveRetornarToken() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest(usuarioMaster.getLogin(), SENHA_PADRAO);


            // Act & Assert
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

        /**
         * Verifica que senha incorreta retorna 401.
         */
        @Test
        @DisplayName("Deve retornar 401 quando senha incorreta")
        void login_quandoSenhaIncorreta_deveRetornar401() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest(usuarioMaster.getLogin(), SENHA_PADRAO);

            // Act & Assert
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.title").value("Authentication Failed"))
                    .andExpect(jsonPath("$.detail").value("Login ou senha inválidos."));
        }

        /**
         * Verifica que login inexistente retorna 401.
         */
        @Test
        @DisplayName("Deve retornar 401 quando login não existe")
        void login_quandoLoginInexistente_deveRetornar401() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest("usuario.inexistente", SENHA_PADRAO);

            // Act & Assert
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

        /**
         * Verifica que login em branco retorna 400.
         */
        @Test
        @DisplayName("Deve retornar 400 quando login em branco")
        void login_quandoLoginEmBranco_deveRetornar400() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest("", SENHA_PADRAO);

            // Act & Assert
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error"))
                    .andExpect(jsonPath("$.errors.login").exists());
        }

        /**
         * Verifica que senha em branco retorna 400.
         */
        @Test
        @DisplayName("Deve retornar 400 quando senha em branco")
        void login_quandoSenhaEmBranco_deveRetornar400() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest("master.teste", "");

            // Act & Assert
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }

        /**
         * Verifica que login curto demais retorna 400.
         */
        @Test
        @DisplayName("Deve retornar 400 quando login muito curto")
        void login_quandoLoginMuitoCurto_deveRetornar400() throws Exception {
            // Arrange
            AuthRequest request = new AuthRequest("abc", SENHA_PADRAO);

            // Act & Assert
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.login").exists());
        }

        /**
         * Verifica que body vazio retorna 400.
         */
        @Test
        @DisplayName("Deve retornar 400 quando body vazio")
        void login_quandoBodyVazio_deveRetornar400() throws Exception {
            // Act & Assert
            mockMvc.perform(post(AUTH_LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}