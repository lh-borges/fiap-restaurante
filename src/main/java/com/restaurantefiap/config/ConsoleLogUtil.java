package com.restaurantefiap.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utilitário para formatação de logs no console utilizando códigos ANSI.
 * Permite a personalização de cores e estilos de texto (negrito, itálico, etc).
 * @author Danilo de Paula
 * @since 04/11/2022
 */
public class ConsoleLogUtil {

    /**
     * Enumeração que define as cores de texto e fundo disponíveis para o console.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Cor {
        DEFAULT("0m"),
        NORMAL("1m"),
        PRETO("30m"),
        VERMELHO("31m"),
        VERDE("32m"),
        AMARELO("33m"),
        AZUL("34m"),
        ROXO("35m"),
        CIANO("36m"),
        CINZA("37m"),
        FUNDO_PRETO_TEXTO_BRANCO("40m"),
        FUNDO_VERMELHO_TEXTO_BRANCO("41m"),
        FUNDO_VERDE_TEXTO_BRANCO("42m"),
        FUNDO_AMARELO_TEXTO_BRANCO("43m"),
        FUNDO_AZUL_TEXTO_BRANCO("44m"),
        FUNDO_ROXO_TEXTO_BRANCO("45m"),
        FUNDO_CIANO_TEXTO_BRANCO("46m"),
        FUNDO_CINZA_TEXTO_BRANCO("47m");

        @NonNull
        private final String codigo;
    }

    /**
     * Enumeração que define os estilos de formatação de texto.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Estilo {
        NORMAL(0),
        NEGRITO(1),
        ITALICO(3),
        SUBLINHADO(4),
        DESTACADO(7),
        RISCADO(9);

        @NonNull
        private final Integer codigo;
    }

    /**
     * Formata um texto com uma cor específica e estilo normal.
     *
     * @param texto O conteúdo a ser formatado.
     * @param cor   A cor desejada para o texto.
     * @return String formatada com códigos ANSI.
     */
    public static String formatar(String texto, Cor cor) {
        return formatar(texto, cor, Estilo.NORMAL);
    }

    /**
     * Formata um texto com a cor normal e um ou mais estilos.
     *
     * @param texto   O conteúdo a ser formatado.
     * @param estilos Lista de estilos (ex: NEGRITO, SUBLINHADO).
     * @return String formatada com códigos ANSI.
     */
    public static String formatar(String texto, Estilo... estilos) {
        return formatar(texto, Cor.NORMAL, estilos);
    }

    /**
     * Formata um texto com cor e múltiplos estilos.
     *
     * @param texto   O conteúdo a ser formatado.
     * @param cor     A cor desejada.
     * @param estilos Lista de estilos.
     * @return String formatada com códigos ANSI.
     */
    public static String formatar(String texto, Cor cor, Estilo... estilos) {
        return gerarCodigoAnsi(cor, estilos) + texto + gerarCodigoAnsi(Cor.DEFAULT, Estilo.NORMAL);
    }

    /**
     * Constrói a sequência de escape ANSI para a combinação de cor e estilos.
     *
     * @param cor     A cor selecionada.
     * @param estilos Array de estilos selecionados.
     * @return String contendo o código de escape ANSI.
     */
    private static String gerarCodigoAnsi(Cor cor, Estilo... estilos) {
        String codigosEstilo = Arrays.stream(estilos)
                .map(estilo -> String.valueOf(estilo.getCodigo()))
                .collect(Collectors.joining(";"));

        return "\u001B[" + codigosEstilo + ";" + cor.getCodigo();
    }
}