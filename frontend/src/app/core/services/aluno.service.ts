import {Injectable} from '@angular/core';import {HttpClient,HttpParams} from '@angular/common/http';import {Aluno,Pagina,StatusAluno} from '../../models/aluno.model';

// centraliza as chamadas do frontend para a API de alunos
@Injectable({providedIn:'root'})
export class AlunoService{
 private api='http://localhost:8080/api/alunos';constructor(private http:HttpClient){}
 listar(f:{nome:string;matricula:string;status:''|StatusAluno;page:number;sort?:string}){let p=new HttpParams().set('page',f.page).set('size',10).set('sort',f.sort||'nomeCompleto,asc');if(f.nome.trim())p=p.set('nome',f.nome.trim());if(f.matricula.trim())p=p.set('matricula',f.matricula.trim());if(f.status)p=p.set('status',f.status);return this.http.get<Pagina<Aluno>>(this.api,{params:p})}
 buscar(id:number){return this.http.get<Aluno>(this.api+'/'+id)}
 cadastrar(a:Partial<Aluno>){return this.http.post<Aluno>(this.api,a)}
 editar(id:number,a:Partial<Aluno>){return this.http.put<Aluno>(this.api+'/'+id,a)}
 excluir(id:number){return this.http.delete(this.api+'/'+id)}
}