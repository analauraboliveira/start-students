// formato dos dados que o backend devolve para o front
export type StatusAluno='ATIVO'|'INATIVO';
export interface Aluno{id:number;nomeCompleto:string;email:string;cpf:string;telefone:string;foto?:string;matricula:string;status:StatusAluno;version?:number}

// formato da paginação do Spring
export interface Pagina<T>{content:T[];totalElements:number;totalPages:number;number:number;size:number}