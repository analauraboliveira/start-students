import { FormControl } from '@angular/forms';
import { cpfReactiveValidator } from './cpf-reactive.validator';

describe('Validador reativo de CPF', () => {
  it('aceita CPF válido com e sem máscara', () => {
    expect(cpfReactiveValidator(new FormControl('52998224725'))).toBeNull();
    expect(cpfReactiveValidator(new FormControl('529.982.247-25'))).toBeNull();
  });

  it('rejeita CPF com dígitos incorretos', () => {
    expect(cpfReactiveValidator(new FormControl('11111111111')))
      .toEqual({ cpfInvalido: true });
  });

  it('deixa a validação de campo vazio para Validators.required', () => {
    expect(cpfReactiveValidator(new FormControl(''))).toBeNull();
  });
});
