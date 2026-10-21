package br.com.startstudents.adapters.in.web.dto;

import br.com.startstudents.domain.model.*;

public record AlunoResponse(Long id,String nomeCompleto,String email,String cpf,String telefone,String foto,String matricula,StatusAluno status,Long version){
 public static AlunoResponse de(Aluno a){return new AlunoResponse(a.getId(),a.getNomeCompleto(),a.getEmail(),a.getCpf(),a.getTelefone(),a.getFoto(),a.getMatricula(),a.getStatus(),a.getVersion());}
}