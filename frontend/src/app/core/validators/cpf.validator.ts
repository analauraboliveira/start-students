import { Directive, forwardRef } from '@angular/core';
import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';

/**
 * Verifica os dígitos de um CPF, com ou sem pontuação.
 *
 * Esta função contém a regra de negócio e não depende de componentes.
 * Pode ser usada por formulários reativos e formulários com ngModel.
 */
export function cpfValido(valor: unknown): boolean {
  // Remove pontos, traços e outros caracteres que não sejam números.
  const cpf = String(valor ?? '').replace(/\D/g, '');

  // CPF tem 11 dígitos e não pode ser uma sequência repetida.
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) {
    return false;
  }

  // Calcula e compara o primeiro e o segundo dígitos verificadores.
  for (let tamanhoBase = 9; tamanhoBase <= 10; tamanhoBase++) {
    let somaPonderada = 0;

    for (let indiceDigito = 0; indiceDigito < tamanhoBase; indiceDigito++) {
      const digitoAtual = Number(cpf[indiceDigito]);
      const peso = tamanhoBase + 1 - indiceDigito;

      somaPonderada += digitoAtual * peso;
    }

    const resto = 11 - (somaPonderada % 11);
    const digitoEsperado = resto >= 10 ? 0 : resto;
    const digitoInformado = Number(cpf[tamanhoBase]);

    if (digitoEsperado !== digitoInformado) {
      return false;
    }
  }

  return true;
}

/**
 * Diretiva para telas antigas que ainda usam ngModel.
 * O cadastro novo usa o cpfReactiveValidator, em arquivo separado.
 */
@Directive({
  selector: '[appCpf][ngModel]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CpfValidatorDirective),
      multi: true,
    },
  ],
})
export class CpfValidatorDirective implements Validator {
  validate(controle: AbstractControl): ValidationErrors | null {
    // Validators.required cuida do campo vazio.
    if (!controle.value || cpfValido(controle.value)) {
      return null;
    }

    return { cpf: true };
  }
}
