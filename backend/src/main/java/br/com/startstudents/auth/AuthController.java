package br.com.startstudents.auth;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Login local do protótipo, com perfis de administrador e leitor. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest requisicao) {
        PerfilUsuario perfil = null;

        // Estes usuários são fixos porque a autenticação é apenas demonstrativa.
        if (requisicao.usuario().equals("adminuser") && requisicao.senha().equals("admin123")) {
            perfil = PerfilUsuario.ADMINISTRADOR;
        }

        if (requisicao.usuario().equals("leitoruser") && requisicao.senha().equals("leitor123")) {
            perfil = PerfilUsuario.LEITOR;
        }

        if (perfil == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensagem", "Usuário ou senha inválidos."));
        }

        String token = jwtService.gerar(requisicao.usuario(), perfil.name());
        return ResponseEntity.ok(new LoginResponse(token, requisicao.usuario(), perfil));
    }
}
