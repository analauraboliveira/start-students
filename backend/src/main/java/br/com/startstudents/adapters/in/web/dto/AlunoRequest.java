package br.com.startstudents.adapters.in.web.dto;
import br.com.startstudents.domain.model.StatusAluno;
import jakarta.validation.constraints.*;
public record AlunoRequest(
 @NotBlank @Size(min=3,max=120) @Pattern(regexp="^[\\p{L} .'-]+$") String nomeCompleto,
 @NotBlank @Email String email,
 @NotBlank String cpf,
 @NotBlank @Pattern(regexp="\\D*(?:\\d\\D*){10,11}") String telefone,
 String foto,
 StatusAluno status
){}