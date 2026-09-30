import {Component,HostListener,OnInit} from '@angular/core';import {CommonModule} from '@angular/common';import {FormsModule} from '@angular/forms';import {ActivatedRoute,Router,RouterLink} from '@angular/router';import {AlunoService} from '../../core/services/aluno.service';import {AuthService} from '../../core/auth/auth.service';import {Aluno} from '../../models/aluno.model';
// o mesmo formulario serve para cadastro e edicao
@Component({selector:'app-formulario-aluno',standalone:true,imports:[CommonModule,FormsModule,RouterLink],template:`<main><a routerLink="/alunos" (click)="confirmarSaida($event)">← Voltar</a><section class="card formulario"><h1>{{id?'Editar aluno':'Novo aluno'}}</h1><div class="estado erro" *ngIf="semPermissao">Você não tem permissão para acessar esta ação.</div><form #f="ngForm" *ngIf="!semPermissao" (ngSubmit)="salvar()"><label>Nome completo<input name="nome" [(ngModel)]="aluno.nomeCompleto" required minlength="3" maxlength="120" pattern="[A-Za-zÀ-ÿ .'-]+"></label><label>E-mail<input name="email" [(ngModel)]="aluno.email" type="email" required></label><label>CPF<input name="cpf" [(ngModel)]="aluno.cpf" [disabled]="!!id" required></label><label>Telefone<input name="telefone" [(ngModel)]="aluno.telefone" required pattern="\\D*(?:\\d\\D*){10,11}"></label><label>Foto JPEG ou PNG (até 5 MB)<input type="file" accept="image/jpeg,image/png" (change)="fotoSelecionada($event)"></label><img *ngIf="aluno.foto" [src]="aluno.foto" alt="Pré-visualização da foto do aluno" class="preview"><button type="button" class="secundario" *ngIf="aluno.foto" (click)="removerFoto()">Remover foto</button><p *ngIf="id"><b>Matrícula:</b> {{aluno.matricula}} (não pode ser alterada)</p><label *ngIf="id">Status<select name="status" [(ngModel)]="aluno.status"><option value="ATIVO">Ativo</option><option value="INATIVO">Inativo</option></select></label><p class="erro" *ngIf="erro" aria-live="polite">{{erro}}</p><p class="sucesso" *ngIf="sucesso" aria-live="polite">{{sucesso}}</p><button [disabled]="f.invalid||salvando||(!!id&&!alterado())">{{salvando?'Salvando...':(id?'Salvar alterações':'Cadastrar')}}</button></form></section></main>`})
export class FormularioAlunoComponent implements OnInit{
 id?:number;aluno:Partial<Aluno>={status:'ATIVO'};original='';erro='';sucesso='';salvando=false;semPermissao=false;salvo=false;
 constructor(private service:AlunoService,private auth:AuthService,private route:ActivatedRoute,private router:Router){}
 ngOnInit(){
  if(!this.auth.administrador()){this.semPermissao=true;return}
  const x=this.route.snapshot.paramMap.get('id');
  // quando tem id, busca os dados reais antes de editar
  if(x){this.id=+x;this.service.buscar(this.id).subscribe({next:a=>{this.aluno=a;this.original=this.snapshot()},error:()=>this.erro='Não foi possível carregar o aluno.'})}
  else this.original=this.snapshot();
 }
 alterado(){return this.snapshot()!==this.original}
 private snapshot(){return JSON.stringify({nomeCompleto:this.aluno.nomeCompleto||'',email:this.aluno.email||'',cpf:this.aluno.cpf||'',telefone:this.aluno.telefone||'',foto:this.aluno.foto||'',status:this.aluno.status||'ATIVO'})}
 fotoSelecionada(event:Event){
  const input=event.target as HTMLInputElement,file=input.files?.[0];if(!file)return;
  // valida antes de transformar a imagem em base64
  if(!['image/jpeg','image/png'].includes(file.type)){this.erro='A foto deve ser JPEG ou PNG.';input.value='';return}
  if(file.size>5*1024*1024){this.erro='A foto deve ter no máximo 5 MB.';input.value='';return}
  const leitor=new FileReader();leitor.onload=()=>{this.aluno.foto=String(leitor.result);this.erro=''};leitor.readAsDataURL(file);
 }
 removerFoto(){this.aluno.foto=''}
 salvar(){
  this.salvando=true;this.erro='';this.sucesso='';
  const req=this.id?this.service.editar(this.id,this.aluno):this.service.cadastrar(this.aluno);
  // se der erro, nao limpa o formulario
  req.subscribe({next:a=>{this.salvo=true;this.sucesso=this.id?'Alterações salvas.':'Aluno cadastrado. Matrícula: '+a.matricula;setTimeout(()=>this.router.navigate(['/alunos']),700)},error:e=>{this.erro=e.error?.mensagem||'Confira os dados e tente novamente.';this.salvando=false}})
 }
 confirmarSaida(event:Event){if(this.alterado()&&!this.salvo&&!confirm('Você tem alterações não salvas. Deseja sair mesmo?'))event.preventDefault()}
 @HostListener('window:beforeunload',['$event']) sairPagina(e:BeforeUnloadEvent){if(this.alterado()&&!this.salvo)e.preventDefault()}
}