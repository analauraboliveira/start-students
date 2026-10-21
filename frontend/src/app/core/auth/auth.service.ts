import {Injectable} from '@angular/core';import {HttpClient} from '@angular/common/http';import {tap} from 'rxjs';

// cuida do login e guarda a sessão no navegador
interface Sessao{token:string;usuario:string;perfil:'ADMINISTRADOR'|'LEITOR'}

@Injectable({providedIn:'root'})
export class AuthService{
 private api='http://localhost:8080/api/auth';
 constructor(private http:HttpClient){}
 login(usuario:string,senha:string){return this.http.post<Sessao>(this.api+'/login',{usuario,senha}).pipe(tap(x=>localStorage.setItem('sessao',JSON.stringify(x))))}
 logout(){localStorage.removeItem('sessao')}
 sessao():Sessao|null{try{return JSON.parse(localStorage.getItem('sessao')||'null')}catch{return null}}
 autenticado(){return!!this.sessao()?.token}
 administrador(){return this.sessao()?.perfil==='ADMINISTRADOR'}
}