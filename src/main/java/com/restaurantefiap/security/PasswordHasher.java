package com.restaurantefiap.security;

/**
 * Abstração para operações de hash de senha.
 *
 * <p>Permite trocar a implementação de criptografia sem afetar o domínio.</p>
 *
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
public interface PasswordHasher {

    /**
     * Gera hash da senha em texto plano.
     *
     * @param senhaPlana senha em texto plano
     * @return hash da senha
     */
    String hash(String senhaPlana);

    /**
     * Verifica se a senha em texto plano corresponde ao hash armazenado.
     *
     * @param senhaPlana senha em texto plano
     * @param hashArmazenado hash armazenado no banco
     * @return true se a senha corresponde ao hash
     */
    boolean matches(String senhaPlana, String hashArmazenado);
}
