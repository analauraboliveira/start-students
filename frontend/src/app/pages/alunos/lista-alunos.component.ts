import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { AlunoService } from '../../core/services/aluno.service';
import { Aluno, Pagina, StatusAluno } from '../../models/aluno.model';
/** Lista alunos, mantém filtros e abre os fluxos de detalhes, edição e exclusão. */
@Component({
  selector: 'app-lista-alunos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './lista-alunos.component.html',
})

export class ListaAlunosComponent implements OnInit {
  dados?:Pagina<Aluno>;termo='';nome='';matricula='';status:''|StatusAluno='';ordenacao='nomeCompleto,asc';pagina=0;carregando=false;erro='';feedback='';admin=false;alunoExcluir?:Aluno;excluindo=false;acionador?:HTMLElement;constructor(
    private readonly alunos: AlunoService,
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {}
// Recupera os filtros usados antes de entrar nos detalhes.
  ngOnInit() {this.admin=this.auth.administrador();this.restaurar();this.termo=this.nome||this.matricula;this.carregar(this.pagina)}// Identifica se a busca principal contém nome ou matrícula.
  buscarTermo() {const t=this.termo.trim();const matricula=/^\d+$/.test(t);this.nome=matricula?'':t;this.matricula=matricula?t:'';this.buscar()}buscar(){this.carregar(0)}limpar(){this.termo='';this.nome='';this.matricula='';this.status='';this.carregar(0)}// A API aplica filtros, ordenação e paginação.
  carregar(p: number) {this.pagina=p;this.carregando=true;this.erro='';this.salvarEstado();this.alunos.listar({nome:this.nome,matricula:this.matricula,status:this.status,page:p,sort:this.ordenacao}).subscribe({next:d=>{this.dados=d;this.carregando=false},error:()=>{this.erro='Não foi possível carregar os alunos.';this.carregando=false}})}paginas(){return Array.from({length:this.dados?.totalPages||0},(_,i)=>i)}// Abre a confirmação e move o foco para o modal.
  abrirExclusao(a: Aluno, e: Event) {this.alunoExcluir=a;this.acionador=e.currentTarget as HTMLElement;setTimeout(()=>document.querySelector<HTMLElement>('.modal .secundario')?.focus())}cancelarExclusao(){this.alunoExcluir=undefined;setTimeout(()=>this.acionador?.focus())}@HostListener('document:keydown',['$event'])teclaModal(e:KeyboardEvent){if(!this.alunoExcluir)return;if(e.key==='Escape'){e.preventDefault();this.cancelarExclusao();return}if(e.key!=='Tab')return;const botoes=Array.from(document.querySelectorAll<HTMLElement>('.modal button:not([disabled])'));if(!botoes.length)return;const primeiro=botoes[0],ultimo=botoes[botoes.length-1];if(e.shiftKey&&document.activeElement===primeiro){e.preventDefault();ultimo.focus()}else if(!e.shiftKey&&document.activeElement===ultimo){e.preventDefault();primeiro.focus()}}// Exclui somente depois da confirmação da pessoa usuária.
  confirmarExclusao() {if(!this.alunoExcluir)return;this.excluindo=true;this.alunos.excluir(this.alunoExcluir.id).subscribe({next:()=>{this.feedback='Aluno excluído com sucesso.';if((this.dados?.content.length||0)===1&&this.pagina>0)this.pagina--;this.alunoExcluir=undefined;this.excluindo=false;this.carregar(this.pagina);setTimeout(()=>this.acionador?.focus())},error:e=>{this.erro=e.status===403?'Você não tem permissão para esta ação.':'Não foi possível excluir.';this.excluindo=false;this.alunoExcluir=undefined;setTimeout(()=>this.acionador?.focus())}})}// Guarda os filtros no navegador para não perder a busca.
  private salvarEstado() {sessionStorage.setItem('listaAlunos',JSON.stringify({nome:this.nome,matricula:this.matricula,status:this.status,ordenacao:this.ordenacao,pagina:this.pagina}))}private restaurar(){try{const e=JSON.parse(sessionStorage.getItem('listaAlunos')||'null');if(e){this.nome=e.nome||'';this.matricula=e.matricula||'';this.status=e.status||'';this.ordenacao=e.ordenacao||'nomeCompleto,asc';this.pagina=e.pagina||0}}catch{}}sair(){sessionStorage.removeItem('listaAlunos');this.auth.logout();this.router.navigate(['/login'])}}