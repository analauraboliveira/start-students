package br.com.startstudents.adapters.out.persistence;
import br.com.startstudents.domain.model.StatusAluno;
import jakarta.persistence.*;
import lombok.*;

// essa classe representa a tabela alunos no banco
@Entity @Table(name="alunos") @Getter @Setter @NoArgsConstructor
public class AlunoEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String nomeCompleto;
 @Column(nullable=false) private String email;
 // cpf e matricula precisam ser unicos
 @Column(nullable=false,unique=true,length=11) private String cpf;
 @Column(nullable=false,length=11) private String telefone;
 @Lob private String foto;
 @Column(nullable=false,unique=true,updatable=false) private String matricula;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private StatusAluno status;
 // usado para exclusao logica
 @Column(nullable=false) private boolean usuarioExcluido;
}