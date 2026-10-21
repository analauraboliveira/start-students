package br.com.startstudents.application.service;

import org.springframework.stereotype.Component;

@Component
public class ValidadorCpf {
 public boolean valido(String cpf){
  
  // tira pontos e traço antes de validar
  String n=cpf==null?"":cpf.replaceAll("\\D","");
  
  // cpf precisa ter 11 números e não pode ser tudo igual
  if(n.length()!=11||n.chars().distinct().count()==1)return false;
  int soma=0;
  
  // calcula o primeiro dígito verificador
  for(int i=0;i<9;i++) soma+=(n.charAt(i)-'0')*(10-i);
  int d1=11-(soma%11); if(d1>=10)d1=0;
  soma=0;
  
  // calcula o segundo dígito verificador
  for(int i=0;i<10;i++) soma+=(n.charAt(i)-'0')*(11-i);
  int d2=11-(soma%11); if(d2>=10)d2=0;
  return d1==n.charAt(9)-'0'&&d2==n.charAt(10)-'0';
 }
}