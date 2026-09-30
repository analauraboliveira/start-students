import {Component} from '@angular/core';import {CommonModule} from '@angular/common';import {FormsModule} from '@angular/forms';import {Router} from '@angular/router';import {AuthService} from '../../core/auth/auth.service';
// tela de login do sistema
@Component({selector:'app-login',standalone:true,imports:[CommonModule,FormsModule],template:`<main class="login"><section class="card"><h1>Start Students</h1><p>Entre para acessar a gestão de alunos.</p><form #f="ngForm" (ngSubmit)="entrar()"><label>Usuário<input name="usuario" [(ngModel)]="usuario" pattern="[A-Za-z0-9]{8,}" required></label><label>Senha<input name="senha" [(ngModel)]="senha" type="password" pattern="(?=.*[A-Za-z])(?=.*[0-9]).{8,20}" required></label><p class="erro" *ngIf="erro" aria-live="polite">{{erro}}</p><button [disabled]="f.invalid||carregando">{{carregando?'Entrando...':'Acessar'}}</button></form></section></main>`})
export class LoginComponent{
 usuario='';senha='';erro='';carregando=false;
 constructor(private auth:AuthService,private router:Router){}
 entrar(){this.erro='';this.carregando=true;// chama o backend e so entra se estiver certo
 this.auth.login(this.usuario,this.senha).subscribe({next:()=>this.router.navigate(['/alunos']),error:e=>{this.erro=e.error?.mensagem||'Não foi possível entrar.';this.carregando=false}})}
}