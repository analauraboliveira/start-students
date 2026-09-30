import {HttpInterceptorFn} from '@angular/common/http';import {inject} from '@angular/core';import {AuthService} from './auth.service';import {Router} from '@angular/router';import {catchError,throwError} from 'rxjs';
// coloca o token nas requisicoes e trata sessao expirada
export const authInterceptor:HttpInterceptorFn=(req,next)=>{
 const auth=inject(AuthService),router=inject(Router),token=auth.sessao()?.token;
 const nova=token?req.clone({setHeaders:{Authorization:'Bearer '+token}}):req;
 return next(nova).pipe(catchError(e=>{
  if(e.status===401){auth.logout();sessionStorage.setItem('avisoSessao','Sua sessão expirou. Entre novamente.');router.navigate(['/login'])}
  return throwError(()=>e)
 }))
};