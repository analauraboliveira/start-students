package br.com.startstudents.auth;

/** Token e informações do perfil devolvidos após o login. */
public record LoginResponse(
        String token,
        String usuario,
        PerfilUsuario perfil
) {
}
