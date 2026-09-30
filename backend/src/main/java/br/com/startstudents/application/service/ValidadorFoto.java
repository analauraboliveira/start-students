package br.com.startstudents.application.service;
import org.springframework.stereotype.Component;
import java.util.Base64;
// valida a foto antes de mandar pro banco
@Component
public class ValidadorFoto {
 private static final int LIMITE=5*1024*1024;
 public void validar(String foto){
  if(foto==null||foto.isBlank())return;
  // o front manda a imagem como data url
  boolean jpeg=foto.startsWith("data:image/jpeg;base64,");
  boolean png=foto.startsWith("data:image/png;base64,");
  if(!jpeg&&!png)throw new IllegalArgumentException("A foto deve ser JPEG ou PNG.");
  try{
   String base64=foto.substring(foto.indexOf(',')+1);
   if(Base64.getDecoder().decode(base64).length>LIMITE)throw new IllegalArgumentException("A foto deve ter no máximo 5 MB.");
  }catch(IllegalArgumentException e){
   if(e.getMessage()!=null&&e.getMessage().startsWith("A foto"))throw e;
   throw new IllegalArgumentException("A foto enviada é inválida.");
  }
 }
}