/** Valores de status aceitos pela API. */
export type StatusAluno = 'ATIVO' | 'INATIVO';

/** Dados de um aluno recebidos do backend. */
export interface Aluno {
  id: number;
  nomeCompleto: string;
  email: string;
  cpf: string;
  telefone: string;
  foto?: string;
  matricula: string;
  status: StatusAluno;
  version?: number;
}

/** Estrutura de resposta paginada usada pelo Spring Boot. */
export interface Pagina<TipoItem> {
  content: TipoItem[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
