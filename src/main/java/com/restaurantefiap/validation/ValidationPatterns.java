package com.restaurantefiap.validation;

/**
 * Padrões reutilizáveis de validação (regex).
 *
 * <p>Centraliza regex comuns para serem utilizados em anotações Bean Validation (@Pattern).</p>
 * * @author Danilo Fernando
 * @since 04/01/2026
 */
public final class ValidationPatterns {

    private ValidationPatterns() { }

    /**
     * Aceita:
     * - (11) 91234-5678
     * - 11912345678
     * - 11 91234 5678
     * - +55 (11) 91234-5678
     * - telefones fixos: (11) 1234-5678 / 1112345678
     */
    public static final String TELEFONE_BR =
            "^(\\+55\\s?)?(\\(?\\d{2}\\)?\\s?)?(9?\\d{4})-?\\d{4}$";

    /**
     * CPF (Cadastro de Pessoa Física).
     * <p>Aceita formatos: 000.000.000-00 ou 00000000000.</p>
     */
    public static final String CPF = "^(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2})$";

    /**
     * CEP (Código de Endereçamento Postal).
     * <p>Aceita formatos: 00000-000 ou 00000000.</p>
     */
    public static final String CEP = "^\\d{5}-?\\d{3}$";

    /**
     * Senha Forte.
     * <p>Critérios: Mínimo 8 caracteres, uma letra maiúscula, uma minúscula,
     * um número e um caractere especial.</p>
     */
    public static final String SENHA_FORTE = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    /**
     * Nome Próprio.
     * <p>Aceita letras (incluindo acentuação) e espaços. Impede números ou símbolos.</p>
     */
    public static final String NOME_PROPRIO = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s']+$";

    /**
     * Unidade Federativa (UF).
     * <p>Aceita apenas as siglas dos 26 estados brasileiros + DF em maiúsculo.</p>
     */
    public static final String UF_BR = "^(AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO)$";

    /**
     * Login/Username.
     * <p>Aceita letras minúsculas, números, underscores, ponto e hífens.
     * Deve começar com letra e ter entre 3 e 20 caracteres.</p>
     */
    public static final String LOGIN_USUARIO = "^[a-z][a-z0-9_.-]{2,19}$";
}

