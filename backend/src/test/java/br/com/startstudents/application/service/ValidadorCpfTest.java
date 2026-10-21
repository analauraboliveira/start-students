package br.com.startstudents.application.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Confere a validação de CPF usada antes de cadastrar um aluno. */
class ValidadorCpfTest {

    private final ValidadorCpf validadorCpf = new ValidadorCpf();

    @Test
    void deveAceitarCpfValido() {
        assertTrue(validadorCpf.valido("529.982.247-25"));
    }

    @Test
    void deveRecusarCpfRepetido() {
        assertFalse(validadorCpf.valido("111.111.111-11"));
    }

    @Test
    void deveRecusarCpfComDigitoIncorreto() {
        assertFalse(validadorCpf.valido("529.982.247-26"));
    }

    @Test
    void deveRecusarCpfVazio() {
        assertFalse(validadorCpf.valido(null));
        assertFalse(validadorCpf.valido(""));
    }
}
