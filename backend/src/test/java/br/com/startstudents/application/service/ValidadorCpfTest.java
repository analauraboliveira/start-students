package br.com.startstudents.application.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ValidadorCpfTest {
 private final ValidadorCpf validador=new ValidadorCpf();
 @Test void deveAceitarCpfValido(){assertTrue(validador.valido("529.982.247-25"));}
 @Test void deveRecusarCpfRepetido(){assertFalse(validador.valido("111.111.111-11"));}
}