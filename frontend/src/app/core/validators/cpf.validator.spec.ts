import {FormControl} from '@angular/forms';
import {cpfValido,CpfValidatorDirective} from './cpf.validator';

describe('Validação de CPF',()=>{
 it('aceita CPF válido com ou sem máscara',()=>{
  expect(cpfValido('52998224725')).toBeTrue();
  expect(cpfValido('529.982.247-25')).toBeTrue();
  expect(cpfValido('11144477735')).toBeTrue();
 });

 it('rejeita sequência repetida, tamanho incorreto e dígitos inválidos',()=>{
  for(const cpf of ['00000000000','11111111111','123','52998224726','52998224715',undefined])expect(cpfValido(cpf)).toBeFalse();
 });
 
 it('integra o erro cpf ao formulário e deixa o vazio para required',()=>{
  const validator=new CpfValidatorDirective();
  expect(validator.validate(new FormControl('52998224726'))).toEqual({cpf:true});
  expect(validator.validate(new FormControl('529.982.247-25'))).toBeNull();
  expect(validator.validate(new FormControl(''))).toBeNull();
 });
});
