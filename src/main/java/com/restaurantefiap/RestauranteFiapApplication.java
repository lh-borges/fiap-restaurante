package com.restaurantefiap;

import com.restaurantefiap.config.ConsoleLogUtil;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class RestauranteFiapApplication {


    private String versao = "2026/01/10";

    public static void main(String[] args) {
        SpringApplication.run(RestauranteFiapApplication.class, args);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void printStartedBanner() {
        String labelVersao = ConsoleLogUtil.formatar("Versão", ConsoleLogUtil.Cor.AMARELO, ConsoleLogUtil.Estilo.NEGRITO);
        String formatarVersao = ConsoleLogUtil.formatar("%s", ConsoleLogUtil.Cor.VERMELHO);
        String poweredBy = ConsoleLogUtil.formatar("Powered by Grupo 8", ConsoleLogUtil.Cor.VERDE);

        String banner =
                "  __  __           _____       _  _______ \n" +
                        " /_ |/_ |    /\\   |  __ \\     | ||__   __|\n" +
                        "  | | | |   /  \\  | |  | |    | |   | |   \n" +
                        "  | | | |  / /\\ \\ | |  | |    | |   | |   \n" +
                        "  | | | | / ____ \\| |__| | |__| |   | |   \n" +
                        "  |_| |_|/_/    \\_\\_____/ \\____/    |_|   \n\n" +
                        ":: " + labelVersao + " " + formatarVersao + " ::\n" +
                        ":: " + poweredBy + " ::\n";

        System.out.printf((banner) + "%n", versao);
    }

}
