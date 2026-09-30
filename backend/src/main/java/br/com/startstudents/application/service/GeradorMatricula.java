package br.com.startstudents.application.service;
import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;
@Component @RequiredArgsConstructor
public class GeradorMatricula {
 private final AlunoRepositoryPort repository;
 public String gerar(){
  String matricula;
  // junta o ano atual com 4 numeros aleatorios
  do { matricula=Year.now().getValue()+String.format("%04d",ThreadLocalRandom.current().nextInt(1,10000)); }
  // se ja existir, gera outra
  while(repository.existeMatricula(matricula));
  return matricula;
 }
}