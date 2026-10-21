package br.com.startstudents.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Verifica o token antes que a requisição chegue às rotas protegidas. */
@Component
@RequiredArgsConstructor
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest requisicao,
            HttpServletResponse resposta,
            FilterChain cadeiaFiltros) throws ServletException, IOException {

        String autorizacao = requisicao.getHeader("Authorization");

        if (autorizacao != null && autorizacao.startsWith("Bearer ")) {
            try {
                Claims dadosToken = jwtService.ler(autorizacao.substring(7));
                String perfil = dadosToken.get("perfil", String.class);

                // O Spring usa o prefixo ROLE_ para conferir as permissões.
                var autenticacao = new UsernamePasswordAuthenticationToken(
                        dadosToken.getSubject(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + perfil))
                );
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            } catch (Exception erroToken) {
                // Um token inválido não deve autenticar a requisição.
                SecurityContextHolder.clearContext();
            }
        }

        cadeiaFiltros.doFilter(requisicao, resposta);
    }
}
