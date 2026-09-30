package br.com.startstudents.auth;
import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.Map;

// login local do prototipo com os dois perfis pedidos
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
 private final JwtService jwt;
 @PostMapping("/login")
 public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r){
  PerfilUsuario perfil=null;
  if(r.usuario().equals("adminuser")&&r.senha().equals("admin123"))perfil=PerfilUsuario.ADMINISTRADOR;
  if(r.usuario().equals("leitoruser")&&r.senha().equals("leitor123"))perfil=PerfilUsuario.LEITOR;
  if(perfil==null)return ResponseEntity.status(401).body(Map.of("mensagem","Usuário ou senha inválidos."));
  return ResponseEntity.ok(new LoginResponse(jwt.gerar(r.usuario(),perfil.name()),r.usuario(),perfil));
 }
}