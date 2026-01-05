package com.restaurantefiap.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Serviço responsável pelo gerenciamento do ciclo de vida de JSON Web Tokens (JWT).
 * * <p>Esta classe lida com a geração, extração de dados e validação de tokens
 * utilizados no processo de autenticação e autorização stateless.</p>
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretBase64;

    @Value("${security.jwt.expiration-ms:86400000}")
    private long expirationMs;

    /**
     * Extrai o identificador do usuário (Subject) de um token JWT.
     *
     * @param token O token JWT fornecido na requisição.
     * @return O username (login) contido no token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Método genérico para extrair qualquer informação (Claim) específica do payload do token.
     *
     * @param <T>      O tipo do dado a ser retornado.
     * @param token    O token JWT.
     * @param resolver Uma função que define qual claim deve ser extraída.
     * @return O dado extraído do token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = parseAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Gera um novo token JWT para um usuário autenticado.
     *
     * @param user Os detalhes do usuário fornecidos pelo Spring Security.
     * @return Uma String representando o JWT compactado e assinado.
     */
    public String generateToken(UserDetails user) {
        return buildToken(Map.of(), user.getUsername());
    }

    /**
     * Valida se o token pertence ao usuário informado e se ainda está dentro do prazo de validade.
     *
     * @param token O token JWT a ser validado.
     * @param user  O usuário que está tentando realizar a operação.
     * @return {@code true} se o token for íntegro e válido, {@code false} caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return username.equalsIgnoreCase(user.getUsername()) && !isExpired(token);
    }

    // ——— Métodos Privados de Apoio ———

    /**
     * Constrói o token JWT configurando claims customizadas, subject, data de emissão e expiração.
     */
    private String buildToken(Map<String, Object> extraClaims, String subject) {
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifica se o token já expirou comparando a claim de expiração com a data atual.
     */
    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Realiza o parsing do token utilizando a chave de assinatura para validar a integridade.
     * * @throws JwtException Caso o token esteja corrompido ou a assinatura seja inválida.
     */
    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decodifica a chave secreta de Base64 para gerar a assinatura HMAC-SHA.
     */
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}