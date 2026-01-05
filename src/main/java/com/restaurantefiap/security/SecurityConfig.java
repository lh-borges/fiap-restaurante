package com.restaurantefiap.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Configuração central de segurança da aplicação.
 * <p>Define políticas de acesso, gerenciamento de sessão stateless (JWT),
 * criptografia de senhas e integração com o filtro de autenticação customizado.</p>
 * @author Thiago de Jesus
 * @author Danilo Fernando
 * @since 04/01/2026
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configura a corrente de filtros de segurança (Security Filter Chain).
     * * <ul>
     * <li>Desabilita CSRF, pois a API utiliza tokens JWT.</li>
     * <li>Configura a sessão como STATELESS (sem estado no servidor).</li>
     * <li>Define rotas públicas (Swagger, Documentação e Auth).</li>
     * <li>Exige autenticação para todos os outros endpoints.</li>
     * <li>Insere o filtro JWT antes do filtro de autenticação padrão por senha.</li>
     * </ul>
     * * @param http Configurador de segurança HTTP.
     * @return A corrente de filtros configurada.
     * @throws Exception Caso ocorra erro na configuração.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html")
                        .permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Define o provedor de autenticação que integra o serviço de usuários com
     * o algoritmo de hash de senhas.
     * @return Um {@link DaoAuthenticationProvider} configurado.
     */
    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    /**
     * Bean para codificação de senhas utilizando o algoritmo BCrypt.
     * @return Uma instância de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o Gerenciador de Autenticação padrão do Spring para uso no AuthController.
     * @param cfg Configuração de autenticação injetada.
     * @return O {@link AuthenticationManager} configurado.
     * @throws Exception Caso ocorra erro ao recuperar o manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}