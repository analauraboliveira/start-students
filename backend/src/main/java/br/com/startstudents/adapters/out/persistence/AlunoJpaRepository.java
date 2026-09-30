package br.com.startstudents.adapters.out.persistence;
import br.com.startstudents.domain.model.StatusAluno;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface AlunoJpaRepository extends JpaRepository<AlunoEntity,Long>{
 @Query("select a from AlunoEntity a where a.usuarioExcluido=false and (:nome is null or :nome='' or lower(a.nomeCompleto) like lower(concat('%',:nome,'%'))) and (:matricula is null or :matricula='' or a.matricula like concat(:matricula,'%')) and (:status is null or a.status=:status)")
 Page<AlunoEntity> filtrar(@Param("nome")String nome,@Param("matricula")String matricula,@Param("status")StatusAluno status,Pageable pageable);
 @Query("select count(a)>0 from AlunoEntity a where a.cpf=:valor and (:id is null or a.id<>:id)")
 boolean existeCpf(@Param("valor")String valor,@Param("id")Long id);
 @Query("select count(a)>0 from AlunoEntity a where lower(a.email)=lower(:valor) and (:id is null or a.id<>:id)")
 boolean existeEmail(@Param("valor")String valor,@Param("id")Long id);
 boolean existsByMatricula(String matricula);
}