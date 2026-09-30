package br.com.startstudents.auth;
import io.jsonwebtoken.*;import io.jsonwebtoken.security.Keys;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Service;import javax.crypto.SecretKey;import java.nio.charset.StandardCharsets;import java.util.*;

// cria e valida o token usado depois do login
@Service
public class JwtService {
 private final SecretKey chave; private final long expiracao;
 public JwtService(@Value("${app.jwt.secret}")String segredo,@Value("${app.jwt.expiration-ms}")long expiracao){this.chave=Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));this.expiracao=expiracao;}
 public String gerar(String usuario,String perfil){return Jwts.builder().subject(usuario).claim("perfil",perfil).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiracao)).signWith(chave).compact();}
 public Claims ler(String token){return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload();}
}