package com.restaurantefiap.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link GlobalExceptionHandler}.
 * <p>Valida o mapeamento correto de exceções para respostas HTTP
 * seguindo o padrão RFC 7807 (Problem Details).</p>
 *
 * @author Danilo de Paula
 * @since 04/01/2026
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    // ========================================================================
    // EXCEÇÕES DE AUTENTICAÇÃO (401 UNAUTHORIZED)
    // ========================================================================

    @Nested
    @DisplayName("Exceções de Autenticação (401)")
    class AutenticacaoTests {

        /**
         * Verifica que credenciais inválidas retornam 401.
         */
        @Test
        @DisplayName("Deve retornar 401 quando credenciais inválidas")
        void handleBadCredentials_quandoChamado_deveRetornar401() {
            // Arrange
            BadCredentialsException ex = new BadCredentialsException("Bad credentials");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleBadCredentials(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Authentication Failed", response.getBody().getTitle());
            assertEquals("Login ou senha inválidos.", response.getBody().getDetail());
            assertNotNull(response.getBody().getProperties().get("timestamp"));
        }

        /**
         * Verifica que token JWT expirado retorna 401.
         */
        @Test
        @DisplayName("Deve retornar 401 quando token JWT expirado")
        void handleExpiredJwt_quandoChamado_deveRetornar401() {
            // Arrange
            ExpiredJwtException ex = mock(ExpiredJwtException.class);

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleExpiredJwt(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Token Expired", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("expirado"));
        }

        /**
         * Verifica que token JWT malformado retorna 401.
         */
        @Test
        @DisplayName("Deve retornar 401 quando token JWT malformado")
        void handleMalformedJwt_quandoChamado_deveRetornar401() {
            // Arrange
            MalformedJwtException ex = new MalformedJwtException("Invalid token");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleMalformedJwt(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("inválido"));
        }

        /**
         * Verifica que assinatura JWT inválida retorna 401.
         */
        @Test
        @DisplayName("Deve retornar 401 quando assinatura JWT inválida")
        void handleInvalidSignature_quandoChamado_deveRetornar401() {
            // Arrange
            SignatureException ex = new SignatureException("Invalid signature");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleInvalidSignature(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token Signature", response.getBody().getTitle());
        }
    }

    // ========================================================================
    // EXCEÇÕES DE AUTORIZAÇÃO (403 FORBIDDEN)
    // ========================================================================

    @Nested
    @DisplayName("Exceções de Autorização (403)")
    class AutorizacaoTests {

        /**
         * Verifica que acesso negado retorna 403.
         */
        @Test
        @DisplayName("Deve retornar 403 quando acesso negado")
        void handleAccessDenied_quandoChamado_deveRetornar403() {
            // Arrange
            AccessDeniedException ex = new AccessDeniedException("Access denied");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleAccessDenied(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Access Denied", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("permissão"));
        }
    }

    // ========================================================================
    // EXCEÇÕES DE VALIDAÇÃO (400 BAD REQUEST)
    // ========================================================================

    @Nested
    @DisplayName("Exceções de Validação (400)")
    class ValidacaoTests {

        /**
         * Verifica que erros de Bean Validation retornam 400 com mapa de erros.
         */
        @Test
        @DisplayName("Deve retornar 400 com mapa de erros quando validação falha")
        void handleValidationErrors_quandoMultiplosCamposInvalidos_deveRetornarMapaDeErros() {
            // Arrange
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError1 = new FieldError("usuario", "email", "Email é obrigatório");
            FieldError fieldError2 = new FieldError("usuario", "nome", "Nome é obrigatório");

            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleValidationErrors(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Validation Error", response.getBody().getTitle());

            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().getProperties().get("errors");
            assertNotNull(errors);
            assertEquals(2, errors.size());
            assertEquals("Email é obrigatório", errors.get("email"));
            assertEquals("Nome é obrigatório", errors.get("nome"));
        }

        /**
         * Verifica que IllegalArgumentException retorna 400.
         */
        @Test
        @DisplayName("Deve retornar 400 quando IllegalArgumentException")
        void handleIllegalArgument_quandoChamado_deveRetornar400() {
            // Arrange
            IllegalArgumentException ex = new IllegalArgumentException("Login não pode ser vazio.");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleIllegalArgument(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Business Rule Violation", response.getBody().getTitle());
            assertEquals("Login não pode ser vazio.", response.getBody().getDetail());
        }

        /**
         * Verifica que InvalidPasswordException retorna 400.
         */
        @Test
        @DisplayName("Deve retornar 400 quando senha inválida")
        void handleInvalidPassword_quandoChamado_deveRetornar400() {
            // Arrange
            InvalidPasswordException ex = new InvalidPasswordException("Senha atual incorreta");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleInvalidPassword(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid Password", response.getBody().getTitle());
            assertEquals("Senha atual incorreta", response.getBody().getDetail());
        }
    }

    // ========================================================================
    // EXCEÇÕES DE RECURSO NÃO ENCONTRADO (404 NOT FOUND)
    // ========================================================================

    @Nested
    @DisplayName("Exceções de Recurso Não Encontrado (404)")
    class RecursoNaoEncontradoTests {

        /**
         * Verifica que ResourceNotFoundException retorna 404.
         */
        @Test
        @DisplayName("Deve retornar 404 quando recurso não encontrado")
        void handleResourceNotFound_quandoChamado_deveRetornar404() {
            // Arrange
            ResourceNotFoundException ex = new ResourceNotFoundException("Usuário", 1L);

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleResourceNotFound(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Resource Not Found", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("Usuário"));
            assertTrue(response.getBody().getDetail().contains("1"));
        }

        /**
         * Verifica que ResourceNotFoundException com campo retorna mensagem correta.
         */
        @Test
        @DisplayName("Deve retornar 404 com mensagem de campo quando recurso não encontrado por campo")
        void handleResourceNotFound_quandoBuscaPorCampo_deveRetornarMensagemCorreta() {
            // Arrange
            ResourceNotFoundException ex = new ResourceNotFoundException("Usuário", "email", "teste@teste.com");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleResourceNotFound(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().getDetail().contains("email"));
            assertTrue(response.getBody().getDetail().contains("teste@teste.com"));
        }
    }

    // ========================================================================
    // EXCEÇÕES DE CONFLITO (409 CONFLICT)
    // ========================================================================

    @Nested
    @DisplayName("Exceções de Conflito (409)")
    class ConflitoTests {

        /**
         * Verifica que DuplicateResourceException retorna 409.
         */
        @Test
        @DisplayName("Deve retornar 409 quando recurso duplicado")
        void handleDuplicateResource_quandoChamado_deveRetornar409() {
            // Arrange
            DuplicateResourceException ex = new DuplicateResourceException("Email", "teste@teste.com");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleDuplicateResource(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Duplicate Resource", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("Email"));
            assertTrue(response.getBody().getDetail().contains("teste@teste.com"));
        }

        /**
         * Verifica que IllegalStateException retorna 409.
         */
        @Test
        @DisplayName("Deve retornar 409 quando estado ilegal")
        void handleIllegalState_quandoChamado_deveRetornar409() {
            // Arrange
            IllegalStateException ex = new IllegalStateException("Operação não permitida neste estado");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleIllegalState(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Illegal State", response.getBody().getTitle());
            assertEquals("Operação não permitida neste estado", response.getBody().getDetail());
        }

        /**
         * Verifica que violação de integridade de dados retorna 409.
         */
        @Test
        @DisplayName("Deve retornar 409 quando violação de integridade de dados")
        void handleDataIntegrityViolation_quandoChamado_deveRetornar409() {
            // Arrange
            DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleDataIntegrityViolation(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Data Integrity Violation", response.getBody().getTitle());
        }

        /**
         * Verifica que violação de email duplicado retorna mensagem específica.
         */
        @Test
        @DisplayName("Deve retornar mensagem específica quando email duplicado")
        void handleDataIntegrityViolation_quandoEmailDuplicado_deveRetornarMensagemEspecifica() {
            // Arrange
            DataIntegrityViolationException ex = new DataIntegrityViolationException(
                    "could not execute statement; constraint [email]; nested exception"
            );

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleDataIntegrityViolation(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertTrue(response.getBody().getDetail().contains("Email"));
        }
    }

    // ========================================================================
    // EXCEÇÕES DE REGRA DE NEGÓCIO (422 UNPROCESSABLE ENTITY)
    // ========================================================================

    @Nested
    @DisplayName("Exceções de Regra de Negócio (422)")
    class RegraNegocioTests {

        /**
         * Verifica que BusinessRuleException retorna 422.
         */
        @Test
        @DisplayName("Deve retornar 422 quando regra de negócio violada")
        void handleBusinessRule_quandoChamado_deveRetornar422() {
            // Arrange
            BusinessRuleException ex = new BusinessRuleException("Usuário não pode excluir a si mesmo");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleBusinessRule(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals("Business Rule Violation", response.getBody().getTitle());
            assertEquals("Usuário não pode excluir a si mesmo", response.getBody().getDetail());
        }
    }

    // ========================================================================
    // EXCEÇÕES GENÉRICAS (500 INTERNAL SERVER ERROR)
    // ========================================================================

    @Nested
    @DisplayName("Exceções Genéricas (500)")
    class ExcecaoGenericaTests {

        /**
         * Verifica que exceção não tratada retorna 500.
         */
        @Test
        @DisplayName("Deve retornar 500 quando exceção não tratada")
        void handleGenericException_quandoChamado_deveRetornar500() {
            // Arrange
            Exception ex = new RuntimeException("Erro inesperado");
            when(webRequest.getDescription(false)).thenReturn("uri=/usuarios");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleGenericException(ex, webRequest);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Internal Server Error", response.getBody().getTitle());
            assertTrue(response.getBody().getDetail().contains("erro interno"));
        }

        /**
         * Verifica que exceção genérica não expõe detalhes internos.
         */
        @Test
        @DisplayName("Não deve expor detalhes internos da exceção")
        void handleGenericException_quandoChamado_naoDeveExporDetalhesInternos() {
            // Arrange
            Exception ex = new NullPointerException("Detalhes sensíveis do sistema");
            when(webRequest.getDescription(false)).thenReturn("uri=/usuarios");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleGenericException(ex, webRequest);

            // Assert
            assertFalse(response.getBody().getDetail().contains("NullPointerException"));
            assertFalse(response.getBody().getDetail().contains("sensíveis"));
        }
    }

    // ========================================================================
    // ESTRUTURA DO PROBLEM DETAIL
    // ========================================================================

    @Nested
    @DisplayName("Estrutura do ProblemDetail (RFC 7807)")
    class EstruturaProblemDetailTests {

        /**
         * Verifica que todos os ProblemDetail possuem timestamp.
         */
        @Test
        @DisplayName("Deve incluir timestamp em todas as respostas")
        void problemDetail_quandoCriado_deveIncluirTimestamp() {
            // Arrange
            ResourceNotFoundException ex = new ResourceNotFoundException("Teste", 1L);

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleResourceNotFound(ex, webRequest);

            // Assert
            assertNotNull(response.getBody().getProperties());
            assertNotNull(response.getBody().getProperties().get("timestamp"));
        }

        /**
         * Verifica que todos os ProblemDetail possuem type URI.
         */
        @Test
        @DisplayName("Deve incluir type URI em todas as respostas")
        void problemDetail_quandoCriado_deveIncluirTypeUri() {
            // Arrange
            InvalidPasswordException ex = new InvalidPasswordException("Senha incorreta");

            // Act
            ResponseEntity<ProblemDetail> response = exceptionHandler.handleInvalidPassword(ex, webRequest);

            // Assert
            assertNotNull(response.getBody().getType());
            assertTrue(response.getBody().getType().toString().startsWith("https://api.restaurante.com/errors/"));
        }
    }
}