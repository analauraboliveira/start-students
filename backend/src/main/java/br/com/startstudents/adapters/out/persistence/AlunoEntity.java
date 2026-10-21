package br.com.startstudents.adapters.out.persistence;

import br.com.startstudents.domain.model.StatusAluno;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Representação do aluno na tabela do banco de dados. */
@Entity
@Table(name = "alunos")
@Getter
@Setter
@NoArgsConstructor
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, length = 11)
    private String telefone;

    @Lob
    private String foto;

    // A matrícula é criada no cadastro e não pode mudar depois.
    @Column(nullable = false, unique = true, updatable = false)
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluno status;

    // Exclusão lógica: mantém o registro, mas oculta o aluno da listagem normal.
    @Column(nullable = false)
    private boolean usuarioExcluido;

    // Controla a versão do registro para detectar atualizações concorrentes.
    @Version
    private Long version;
}
