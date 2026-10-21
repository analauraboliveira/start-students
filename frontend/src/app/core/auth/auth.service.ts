import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';

/** Informações devolvidas pelo backend após um login válido. */
interface Sessao {
  token: string;
  usuario: string;
  perfil: 'ADMINISTRADOR' | 'LEITOR';
}

/**
 * Centraliza o login, a leitura da sessão e as verificações de permissão.
 * Os componentes usam este serviço em vez de acessar o localStorage diretamente.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = 'http://localhost:8080/api/auth';

  constructor(private readonly http: HttpClient) {}

  /** Faz login e guarda a sessão somente após a resposta da API. */
  login(usuario: string, senha: string) {
    return this.http
      .post<Sessao>(`${this.api}/login`, { usuario, senha })
      .pipe(
        tap((sessaoRecebida) => {
          localStorage.setItem('sessao', JSON.stringify(sessaoRecebida));
        }),
      );
  }

  /** Encerra a sessão armazenada no navegador. */
  logout(): void {
    localStorage.removeItem('sessao');
  }

  /** Recupera a sessão e evita quebrar a aplicação se o JSON estiver inválido. */
  sessao(): Sessao | null {
    try {
      return JSON.parse(localStorage.getItem('sessao') || 'null') as Sessao | null;
    } catch {
      return null;
    }
  }

  /** Confere se existe um token na sessão atual. */
  autenticado(): boolean {
    return Boolean(this.sessao()?.token);
  }

  /** Define se a pessoa pode usar as ações exclusivas de administrador. */
  administrador(): boolean {
    return this.sessao()?.perfil === 'ADMINISTRADOR';
  }
}
