package com.restaurantefiap.controller;

import com.restaurantefiap.dto.request.AuthRequest;
import com.restaurantefiap.dto.response.AuthResponse;
import com.restaurantefiap.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelos processos de autenticação.
 * * <p>Provê os endpoints necessários para que os usuários possam se autenticar
 * e obter tokens de acesso JWT para interagir com recursos protegidos.</p>
 * @author Juliana Dal Olio
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de login e geração de tokens")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Realiza a autenticação do usuário e retorna um token JWT.
     * <p>O processo envolve a validação das credenciais pelo {@link AuthenticationManager},
     * que por sua vez utiliza o {@link UserDetailsService} e o algoritmo de criptografia
     * configurado na aplicação.</p>
     * @param req Objeto contendo o login (login) e a senha em texto plano.
     * @return {@link ResponseEntity} contendo o {@link AuthResponse} com o token gerado.
     */
    @Operation(
            summary = "Login do usuário",
            description = "Valida as credenciais e retorna um token JWT válido por 24 horas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login realizado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Login ou senha inválidos"),
                    @ApiResponse(responseCode = "400", description = "Dados da requisição mal formatados")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {

        // 1. Tenta autenticar as credenciais enviadas
        // Se falhar, o AuthenticationManager lançará uma exceção capturada pelo GlobalExceptionHandler
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.login(), req.password())
        );

        // 2. Recupera os detalhes do usuário autenticado
        var userDetails = userDetailsService.loadUserByUsername(req.login());

        // 3. Gera o token de acesso (Bearer Token)
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}