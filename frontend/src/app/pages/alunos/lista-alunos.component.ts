import {Component,OnInit} from '@angular/core';import {CommonModule} from '@angular/common';import {FormsModule} from '@angular/forms';import {Router,RouterLink} from '@angular/router';import {Aluno,Pagina,StatusAluno} from '../../models/aluno.model';import {AlunoService} from '../../core/services/aluno.service';import {AuthService} from '../../core/auth/auth.service';
// lista os dados que chegam do backend
@Component({selector:'app-lista-alunos',standalone:true,imports:[CommonModule,FormsModule,RouterLink],template:`<header><div><strong>Start Students</strong><span>Gestão de alunos</span></div><button class="secundario" (click)="sair()">Sair</button></header><main><div class="topo"><div><h1>Alunos</h1><p>Consulte e gerencie os alunos cadastrados.</p></div><a *ngIf="admin" routerLink="/alunos/novo" class="botao">Novo aluno</a></div><p class="sucesso" *ngIf="feedback" aria-live="polite">{{feedback}}</p><section class="filtros"><input aria-label="Buscar por nome" placeholder="Buscar por nome" [(ngModel)]="nome" (keyup.enter)="buscar()"><input aria-label="Buscar por matrícula" placeholder="Matrícula" [(ngModel)]="matricula" (keyup.enter)="buscar()"><select aria-label="Filtrar por status" [(ngModel)]="status" (change)="buscar()"><option value="">Todos os status</option><option value="ATIVO">Ativo</option><option value="INATIVO">Inativo</option></select><button (click)="buscar()">Buscar</button><button class="secundario" (click)="limpar()">Limpar</button></section><p *ngIf="dados">Total encontrado: {{dados.totalElements}}</p><div class="estado" *ngIf="carregando">Carregando alunos...</div><div class="estado erro" *ngIf="erro">{{erro}} <button (click)="carregar(pagina)">Tentar novamente</button></div><div class="estado" *ngIf="!carregando&&!erro&&dados?.content?.length===0">Nenhum aluno encontrado com esses filtros. <button (click)="limpar()">Limpar filtros</button></div><div class="tabela" *ngIf="!carregando&&!erro&&dados?.content?.length"><table><thead><tr><th>Nome</th><th>Matrícula</th><th>Status</th><th>Ações</th></tr></thead><tbody><tr *ngFor="let a of dados?.content"><td>{{a.nomeCompleto}}</td><td>{{a.matricula}}</td><td><span class="tag">{{a.status==='ATIVO'?'Ativo':'Inativo'}}</span></td><td><a title="Ver detalhes" [routerLink]="['/alunos',a.id]">Ver detalhes</a><a title="Editar" *ngIf="admin" [routerLink]="['/alunos',a.id,'editar']">Editar</a><button title="Excluir" class="link perigo" *ngIf="admin" (click)="excluir(a)">Excluir</button></td></tr></tbody></table></div><nav *ngIf="dados&&dados.totalPages>1"><button [disabled]="pagina===0" (click)="carregar(pagina-1)">Anterior</button><span>Página {{pagina+1}} de {{dados.totalPages}}</span><button [disabled]="pagina+1>=dados.totalPages" (click)="carregar(pagina+1)">Próxima</button></nav></main>`})
export class ListaAlunosComponent implements OnInit{
 dados?:Pagina<Aluno>;nome='';matricula='';status:''|StatusAluno='';pagina=0;carregando=false;erro='';feedback='';admin=false;
 constructor(private alunos:AlunoService,private auth:AuthService,private router:Router){}
 ngOnInit(){this.admin=this.auth.administrador();this.restaurar();this.carregar(this.pagina)}
 buscar(){this.carregar(0)}
 limpar(){this.nome='';this.matricula='';this.status='';this.carregar(0)}
 carregar(p:number){
  this.pagina=p;this.carregando=true;this.erro='';this.salvarEstado();
  this.alunos.listar({nome:this.nome,matricula:this.matricula,status:this.status,page:p}).subscribe({next:d=>{this.dados=d;this.carregando=false},error:()=>{this.erro='Não foi possível carregar os alunos.';this.carregando=false}})
 }
 excluir(a:Aluno){
  // a confirmacao mostra nome e matricula
  if(!confirm('Excluir '+a.nomeCompleto+' ('+a.matricula+')? O aluno deixará de aparecer na lista.'))return;
  this.alunos.excluir(a.id).subscribe({next:()=>{
   this.feedback='Aluno inativado com sucesso.';
   // se era o ultimo da pagina, volta para uma pagina valida
   if((this.dados?.content.length||0)===1&&this.pagina>0)this.pagina--;
   this.carregar(this.pagina);
  },error:e=>this.erro=e.status===403?'Você não tem permissão para esta ação.':'Não foi possível excluir.'})
 }
 private salvarEstado(){sessionStorage.setItem('listaAlunos',JSON.stringify({nome:this.nome,matricula:this.matricula,status:this.status,pagina:this.pagina}))}
 private restaurar(){try{const e=JSON.parse(sessionStorage.getItem('listaAlunos')||'null');if(e){this.nome=e.nome||'';this.matricula=e.matricula||'';this.status=e.status||'';this.pagina=e.pagina||0}}catch{}}
 sair(){sessionStorage.removeItem('listaAlunos');this.auth.logout();this.router.navigate(['/login'])}
}