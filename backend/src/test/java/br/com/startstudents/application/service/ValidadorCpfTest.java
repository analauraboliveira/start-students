package br.com.startstudents.application.service;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
class ValidadorCpfTest {
 private final ValidadorCpf validador=new ValidadorCpf();

 // testes
 // 1. deve aceitar cpf válido, 2. deve recusar cpf repetido

 @Test void deveAceitarCpfValido(){assertTrue(validador.valido("529.982.247-25"));}

 @Test void deveRecusarCpfRepetido(){assertFalse(validador.valido("111.111.111-11"));}
}