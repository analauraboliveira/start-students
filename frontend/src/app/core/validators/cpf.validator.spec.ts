import { FormControl } from '@angular/forms';

import { cpfValido, CpfValidatorDirective } from './cpf.validator';

describe('Validação de CPF', () => {
  it('aceita CPF válido com ou sem máscara', () => {
    expect(cpfValido('52998224725')).toBeTrue();
    expect(cpfValido('529.982.247-25')).toBeTrue();
    expect(cpfValido('11144477735')).toBeTrue();
  });

  it('rejeita sequências repetidas, tamanho incorreto e dígitos inválidos', () => {
    const cpfsInvalidos = [
      '00000000000',
      '11111111111',
      '123',
      '52998224726',
      '52998224715',
      undefined,
    ];

    for (const cpfInvalido of cpfsInvalidos) {
      expect(cpfValido(cpfInvalido)).toBeFalse();
    }
  });

  it('integra a diretiva com o formulário e deixa o vazio para required', () => {
    const validador = new CpfValidatorDirective();

    expect(validador.validate(new FormControl('52998224726')))
      .toEqual({ cpf: true });
    expect(validador.validate(new FormControl('529.982.247-25')))
      .toBeNull();
    expect(validador.validate(new FormControl('')))
      .toBeNull();
  });
});
