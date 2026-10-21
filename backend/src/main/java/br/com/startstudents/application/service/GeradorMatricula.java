package br.com.startstudents.application.service;

import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Gera uma matrícula com o ano atual e quatro dígitos numéricos. */
@Component
@RequiredArgsConstructor
public class GeradorMatricula {

    private final AlunoRepositoryPort repositorioAlunos;

    public String gerar() {
        String matriculaGerada;

        // Tenta novamente caso o número já exista no banco.
        do {
            int numeroAleatorio = ThreadLocalRandom.current().nextInt(1, 10000);
            matriculaGerada = Year.now().getValue() + String.format("%04d", numeroAleatorio);
        } while (repositorioAlunos.existeMatricula(matriculaGerada));

        return matriculaGerada;
    }
}
