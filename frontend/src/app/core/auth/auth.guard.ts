import {inject} from '@angular/core';import {CanActivateFn,Router} from '@angular/router';import {AuthService} from './auth.service';


// impede acesso a uma rota protegida quando não tem sessão
// não deixa abrir tela protegida sem estar logado
export const authGuard:CanActivateFn=()=>{const auth=inject(AuthService);return auth.autenticado()?true:inject(Router).createUrlTree(['/login'])};
