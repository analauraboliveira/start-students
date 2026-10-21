package br.com.startstudents.application.service;

import org.springframework.stereotype.Component;

/** Confere os dois dígitos verificadores do CPF. */
@Component
public class ValidadorCpf {

    public boolean valido(String cpf) {
        String cpfNumerico = cpf == null ? "" : cpf.replaceAll("\\D", "");

        // Rejeita CPFs incompletos ou com todos os números iguais.
        if (cpfNumerico.length() != 11 || cpfNumerico.chars().distinct().count() == 1) {
            return false;
        }

        int somaPonderada = 0;

        // Primeiro dígito: considera os nove números iniciais.
        for (int indiceDigito = 0; indiceDigito < 9; indiceDigito++) {
            somaPonderada += (cpfNumerico.charAt(indiceDigito) - '0') * (10 - indiceDigito);
        }

        int primeiroDigito = 11 - (somaPonderada % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        somaPonderada = 0;

        // Segundo dígito: inclui o primeiro dígito verificador.
        for (int indiceDigito = 0; indiceDigito < 10; indiceDigito++) {
            somaPonderada += (cpfNumerico.charAt(indiceDigito) - '0') * (11 - indiceDigito);
        }

        int segundoDigito = 11 - (somaPonderada % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        return primeiroDigito == cpfNumerico.charAt(9) - '0'
                && segundoDigito == cpfNumerico.charAt(10) - '0';
    }
}
