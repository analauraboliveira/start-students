package br.com.startstudents.application.service;

import java.util.Base64;
import org.springframework.stereotype.Component;

/** Valida o tipo e o tamanho da foto recebida pelo formulário. */
@Component
public class ValidadorFoto {

    private static final int LIMITE_FOTO_BYTES = 5 * 1024 * 1024;

    public void validar(String foto) {
        // O cadastro permite continuar sem foto.
        if (foto == null || foto.isBlank()) {
            return;
        }

        // O frontend envia a imagem no formato data URL.
        boolean fotoJpeg = foto.startsWith("data:image/jpeg;base64,");
        boolean fotoPng = foto.startsWith("data:image/png;base64,");

        if (!fotoJpeg && !fotoPng) {
            throw new IllegalArgumentException("A foto deve ser JPEG ou PNG.");
        }

        try {
            String conteudoBase64 = foto.substring(foto.indexOf(',') + 1);
            byte[] imagemDecodificada = Base64.getDecoder().decode(conteudoBase64);

            if (imagemDecodificada.length > LIMITE_FOTO_BYTES) {
                throw new IllegalArgumentException("A foto deve ter no máximo 5 MB.");
            }
        } catch (IllegalArgumentException erroValidacao) {
            if (erroValidacao.getMessage() != null
                    && erroValidacao.getMessage().startsWith("A foto")) {
                throw erroValidacao;
            }

            throw new IllegalArgumentException("A foto enviada é inválida.");
        }
    }
}
