package com.restaurantefiap.adapters;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import com.restaurantefiap.security.PasswordHasher;

/**
 * Implementação de {@link PasswordHasher} usando Spring Security.
 *
 * <p>Delega operações de hash para o {@link PasswordEncoder} configurado (BCrypt).</p>
 * @author Thiago de Jesus
 * @author Danilo de Paula
 */
@Component
@RequiredArgsConstructor
public class SpringPasswordHasher implements PasswordHasher {

    private final PasswordEncoder encoder;     // BCrypt/Argon2 do Spring

    /**
     * Gera um hash seguro a partir de uma senha em texto plano.
     * @param senhaPlana A senha em texto claro que será criptografada.
     * @return O hash gerado utilizando o algoritmo BCrypt por padrão do Spring.
     */
    @Override
    public String hash(String senhaPlana) {
        return encoder.encode(senhaPlana);
    }

    /**
     * Verifica se a senha em texto plano fornecida corresponde ao hash armazenado.
     * Este método é essencial para processos de login e validação de troca de senha.
     * @param senhaPlana A senha digitada pelo usuário.
     * @param hashArmazenado O hash salvo anteriormente no banco de dados.
     * @return {@code true} se a senha corresponder ao hash, {@code false} caso contrário.
     */
    @Override
    public boolean matches(String senhaPlana, String hashArmazenado) {
        return encoder.matches(senhaPlana, hashArmazenado);
    }
}


