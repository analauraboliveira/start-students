package br.com.startstudents.adapters.out.persistence;

import br.com.startstudents.domain.model.StatusAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Consultas ao banco utilizadas pelo adapter de alunos. */
public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {

    /** Filtra por nome, matrícula e status, respeitando a paginação. */
    @Query("""
            select aluno from AlunoEntity aluno
            where (:status is not null or aluno.usuarioExcluido = false)
              and (:nome is null or :nome = ''
                   or lower(aluno.nomeCompleto) like lower(concat('%', :nome, '%')))
              and (:matricula is null or :matricula = ''
                   or aluno.matricula like concat(:matricula, '%'))
              and (:status is null or aluno.status = :status)
            """)
    Page<AlunoEntity> filtrar(
            @Param("nome") String nome,
            @Param("matricula") String matricula,
            @Param("status") StatusAluno status,
            Pageable paginacao
    );

    /** Evita cadastrar um CPF que já pertence a outro registro. */
    @Query("""
            select count(aluno) > 0 from AlunoEntity aluno
            where aluno.cpf = :valor
              and (:id is null or aluno.id <> :id)
            """)
    boolean existeCpf(@Param("valor") String cpf, @Param("id") Long identificadorIgnorado);

    @Query("""
            select count(aluno) > 0 from AlunoEntity aluno
            where lower(aluno.email) = lower(:valor)
              and (:id is null or aluno.id <> :id)
            """)
    boolean existeEmail(@Param("valor") String email, @Param("id") Long identificadorIgnorado);

    boolean existsByMatricula(String matricula);

    /** Exclusão lógica: marca o aluno como excluído e inativo. */
    @Modifying
    @Query("""
            update AlunoEntity aluno
            set aluno.usuarioExcluido = true,
                aluno.status = br.com.startstudents.domain.model.StatusAluno.INATIVO
            where aluno.id = :id and aluno.usuarioExcluido = false
            """)
    int inativar(@Param("id") Long identificadorAluno);
}
