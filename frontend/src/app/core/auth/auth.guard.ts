import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

/**
 * Impede o acesso a telas protegidas quando não existe uma sessão válida.
 * Se a pessoa não estiver autenticada, o Angular redireciona para o login.
 */
export const authGuard: CanActivateFn = () => {
  const autenticacao = inject(AuthService);
  const roteador = inject(Router);

  return autenticacao.autenticado()
    ? true
    : roteador.createUrlTree(['/login']);
};
