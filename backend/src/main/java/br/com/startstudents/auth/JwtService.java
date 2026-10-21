package br.com.startstudents.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Cria e valida os tokens usados nas requisições autenticadas. */
@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracao;

    public JwtService(
            @Value("${app.jwt.secret}") String segredo,
            @Value("${app.jwt.expiration-ms}") long expiracao) {

        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracao = expiracao;
    }

    /** Coloca o usuário e o perfil no token com prazo de expiração. */
    public String gerar(String usuario, String perfil) {
        Date agora = new Date();
        Date dataExpiracao = new Date(System.currentTimeMillis() + expiracao);

        return Jwts.builder()
                .subject(usuario)
                .claim("perfil", perfil)
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(chave)
                .compact();
    }

    /** Lê o token depois de verificar sua assinatura. */
    public Claims ler(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
