package com.restaurantefiap.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.restaurantefiap.service.JwtService;

/**
 * Filtro de interceptação para autenticação via JWT.
 * * <p>Este filtro é executado uma vez por requisição ({@link OncePerRequestFilter}) e
 * verifica a presença do cabeçalho 'Authorization' com o prefixo 'Bearer '.</p>
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsSvc;

    /**
     * Realiza a filtragem da requisição para validar o token JWT.
     * <ol>
     * <li>Extrai o token do cabeçalho Authorization.</li>
     * <li>Valida a estrutura e a validade do token via {@link JwtService}.</li>
     * <li>Se válido, carrega os detalhes do usuário e configura o {@link SecurityContextHolder}.</li>
     * </ol>
     * @param request  Objeto da requisição HTTP.
     * @param response Objeto da resposta HTTP.
     * @param chain    Cadeia de filtros do Spring Security.
     * @throws ServletException Se ocorrer um erro no processamento do servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String auth = request.getHeader("Authorization");

        // 1. Verifica se o cabeçalho existe e começa com "Bearer "
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = auth.substring(7);
        final String username = jwtService.extractUsername(token);

        // 2. Verifica se o usuário foi extraído e se ainda não está autenticado no contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsSvc.loadUserByUsername(username);

            // 3. Valida se o token é íntegro e pertence ao usuário
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Adiciona detalhes da requisição (IP, SessionID) ao token de autenticação
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Define o usuário como autenticado no contexto do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continua a execução para o próximo filtro na corrente
        chain.doFilter(request, response);
    }
}