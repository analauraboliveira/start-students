import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from './auth.service';

/**
 * Acrescenta o token de autenticação nas requisições HTTP.
 * Também trata respostas 401, que indicam uma sessão não autorizada.
 */
export const authInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const autenticacao = inject(AuthService);
  const roteador = inject(Router);
  const token = autenticacao.sessao()?.token;

  // HttpRequest é imutável: clone() cria a cópia com o cabeçalho.
  const requisicaoAutenticada = token
    ? requisicao.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      })
    : requisicao;

  return proximo(requisicaoAutenticada).pipe(
    catchError((erroHttp) => {
      // Limpa a sessão expirada e apresenta o aviso na tela de login.
      if (erroHttp.status === 401) {
        autenticacao.logout();

        sessionStorage.setItem(
          'avisoSessao',
          'Sua sessão expirou. Entre novamente.',
        );

        void roteador.navigate(['/login']);
      }

      // Mantém o erro disponível para o componente que fez a chamada.
      return throwError(() => erroHttp);
    }),
  );
};
