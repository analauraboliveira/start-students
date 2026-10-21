import {Directive, forwardRef} from '@angular/core';
import {AbstractControl, NG_VALIDATORS, ValidationErrors, Validator} from '@angular/forms';

export function cpfValido(valor:unknown):boolean {
  const cpf=String(valor??'').replace(/\D/g,'');
  if(cpf.length!==11||/^(\d)\1{10}$/.test(cpf))return false;
  
  for(let tamanho=9;tamanho<=10;tamanho++){
    let soma=0;
    for(let i=0;i<tamanho;i++)soma+=Number(cpf[i])*(tamanho+1-i);
    const resto=11-(soma%11);
    const digito=resto>=10?0:resto;
    if(digito!==Number(cpf[tamanho]))return false;
  }
  return true;
}

@Directive({selector:'[appCpf][ngModel]',standalone:true,providers:[{provide:NG_VALIDATORS,useExisting:forwardRef(()=>CpfValidatorDirective),multi:true}]})
export class CpfValidatorDirective implements Validator {
  validate(control:AbstractControl):ValidationErrors|null {
    // O required cuida do campo vazio; este validador verifica os dígitos.
    return !control.value||cpfValido(control.value)?null:{cpf:true};
  }
}
