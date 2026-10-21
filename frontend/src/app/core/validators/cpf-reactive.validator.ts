import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { cpfValido } from './cpf.validator';

/** Valida os dígitos do CPF. O Validators.required verifica o campo vazio. */
export const cpfReactiveValidator: ValidatorFn = (
  controle: AbstractControl,
): ValidationErrors | null => {
  const cpf = String(controle.value ?? '').trim();
  return !cpf || cpfValido(cpf) ? null : { cpfInvalido: true };
};
