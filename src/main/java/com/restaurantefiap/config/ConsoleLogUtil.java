package com.restaurantefiap.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 04/11/2022
 */

public class ConsoleLogUtil {

    @RequiredArgsConstructor
    public enum Cor{
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
        private String codigo;
    }

    @RequiredArgsConstructor
    public enum Estilo{
        NORMAL(0),
        NEGRITO(1),
        ITALICO(3),
        SUBLINHADO(4),
        DESTACADO(7),
        RISCADO(9);

        @NonNull
        private Integer codigo;
    }

    public static String formatar(String texto, Cor cor){
        return formatar(texto, cor, Estilo.NORMAL);
    }

    public static String formatar(String texto, Estilo... estilos){
        return formatar(texto, Cor.NORMAL, estilos);
    }

    public static String formatar(String texto, Cor cor, Estilo... estilos){
        return code(cor, estilos) + texto + code(Cor.DEFAULT, Estilo.NORMAL);
    }

    private static String code(Cor cor, Estilo... estilos){
        return
                "\u001B[" +
                        Arrays.stream(estilos).map(estilo -> String.valueOf(estilo.codigo)).collect(Collectors.joining(";")) +
                        ";"+cor.codigo;
    }
}
