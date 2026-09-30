import {Component,OnInit} from '@angular/core';import {CommonModule} from '@angular/common';import {ActivatedRoute,RouterLink} from '@angular/router';import {AlunoService} from '../../core/services/aluno.service';import {Aluno} from '../../models/aluno.model';
// tela somente de leitura
@Component({selector:'app-detalhes-aluno',standalone:true,imports:[CommonModule,RouterLink],template:`<main><a routerLink="/alunos">← Voltar</a><div class="estado" *ngIf="carregando">Carregando...</div><div class="estado erro" *ngIf="erro">{{erro}}</div><section class="card detalhes" *ngIf="aluno"><img *ngIf="aluno.foto" [src]="aluno.foto" [alt]="'Foto de '+aluno.nomeCompleto" class="preview"><div *ngIf="!aluno.foto" class="sem-foto" aria-label="Aluno sem foto">Sem foto</div><h1>{{aluno.nomeCompleto}}</h1><p><b>Matrícula:</b> {{aluno.matricula}}</p><p><b>E-mail:</b> {{aluno.email}}</p><p><b>CPF:</b> {{aluno.cpf}}</p><p><b>Telefone:</b> {{aluno.telefone}}</p><p><b>Status:</b> {{aluno.status==='ATIVO'?'Ativo':'Inativo'}}</p></section></main>`})
export class DetalhesAlunoComponent implements OnInit{
 aluno?:Aluno;carregando=true;erro='';
 constructor(private service:AlunoService,private route:ActivatedRoute){}
 ngOnInit(){// pega o id da URL e busca o aluno na API
 this.service.buscar(Number(this.route.snapshot.paramMap.get('id'))).subscribe({next:a=>{this.aluno=a;this.carregando=false},error:e=>{this.erro=e.status===404?'Aluno não encontrado.':'Não foi possível carregar.';this.carregando=false}})}
}