package com.restaurantefiap.adapters;

import org.springframework.stereotype.Component;
import com.restaurantefiap.security.PasswordPolicy;

/**
 * Implementação simplificada de política de segurança para senhas.
 * * <p>Esta classe verifica se a senha atende aos requisitos mínimos de complexidade
 * antes de permitir o processamento pelo sistema.</p>
 * @author Thiago de Jesus
 * @author Danilo Fernando
 */
@Component
public class SimplePasswordPolicy implements PasswordPolicy {

    private static final int MIN_LENGTH = 8;
    private static final String REGEX_DIGITO = ".*\\d.*";
    private static final String REGEX_MAIUSCULA = ".*[A-Z].*";

    /**
     * Valida a complexidade da senha fornecida.
     * * <p>Os critérios atuais são:</p>
     * <ul>
     * <li>Pelo menos {@value #MIN_LENGTH} caracteres.</li>
     * <li>Presença de ao menos um dígito numérico.</li>
     * <li>Presença de ao menos uma letra maiúscula.</li>
     * </ul>
     *
     * @param raw A senha em texto plano a ser validada.
     * @throws IllegalArgumentException Caso a senha não atenda a qualquer um dos requisitos,
     * contendo a mensagem específica do erro.
     */
    @Override
    public void validateOrThrow(String raw) {
        if (raw == null || raw.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("A senha deve ter no mínimo " + MIN_LENGTH + " caracteres.");
        }

        if (!raw.matches(REGEX_DIGITO)) {
            throw new IllegalArgumentException("A senha deve conter pelo menos um dígito numérico.");
        }

        if (!raw.matches(REGEX_MAIUSCULA)) {
            throw new IllegalArgumentException("A senha deve conter pelo menos uma letra maiúscula.");
        }
    }
}