import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Aluno, Pagina, StatusAluno } from '../../models/aluno.model';

/** Filtros aceitos pelo endpoint de listagem. */
export interface FiltrosAlunos {
  nome: string;
  matricula: string;
  status: '' | StatusAluno;
  page: number;
  sort?: string;
}

/**
 * Centraliza as chamadas HTTP para a API Spring Boot.
 * Assim os componentes não precisam conhecer URLs nem montar parâmetros.
 */
@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly api = 'http://localhost:8080/api/alunos';

  constructor(private readonly http: HttpClient) {}

  /** Lista alunos com paginação, filtros opcionais e ordenação. */
  listar(filtros: FiltrosAlunos) {
    let parametros = new HttpParams()
      .set('page', filtros.page)
      .set('size', 10)
      .set('sort', filtros.sort || 'nomeCompleto,asc');

    if (filtros.nome.trim()) {
      parametros = parametros.set('nome', filtros.nome.trim());
    }

    if (filtros.matricula.trim()) {
      parametros = parametros.set('matricula', filtros.matricula.trim());
    }

    if (filtros.status) {
      parametros = parametros.set('status', filtros.status);
    }

    return this.http.get<Pagina<Aluno>>(this.api, { params: parametros });
  }

  /** Consulta um aluno pelo identificador da rota. */
  buscar(identificadorAluno: number) {
    return this.http.get<Aluno>(`${this.api}/${identificadorAluno}`);
  }

  /** Envia os dados de cadastro. A matrícula é gerada pelo backend. */
  cadastrar(dadosAluno: Partial<Aluno>) {
    return this.http.post<Aluno>(this.api, dadosAluno);
  }

  /** Atualiza um registro existente, mantendo o ID na URL. */
  editar(identificadorAluno: number, dadosAluno: Partial<Aluno>) {
    return this.http.put<Aluno>(`${this.api}/${identificadorAluno}`, dadosAluno);
  }

  /** Exclui um aluno depois da confirmação feita na listagem. */
  excluir(identificadorAluno: number) {
    return this.http.delete(`${this.api}/${identificadorAluno}`);
  }
}
