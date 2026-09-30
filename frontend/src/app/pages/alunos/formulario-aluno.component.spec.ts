import {TestBed} from '@angular/core/testing';
import {provideRouter,Router} from '@angular/router';
import {RouterTestingHarness} from '@angular/router/testing';
import {of} from 'rxjs';
import {FormularioAlunoComponent} from './formulario-aluno.component';
import {ListaAlunosComponent} from './lista-alunos.component';
import {AlunoService} from '../../core/services/aluno.service';
import {AuthService} from '../../core/auth/auth.service';
import {alteracoesPendentesGuard} from '../../core/guards/alteracoes-pendentes.guard';
import {routes} from '../../app.routes';

describe('Formulário de aluno e saída com alterações',()=>{
 let service:any;
 beforeEach(()=>{
  sessionStorage.clear();
  service={listar:jasmine.createSpy().and.returnValue(of({content:[],totalElements:0,totalPages:0,number:0,size:10})),cadastrar:jasmine.createSpy()};
  TestBed.configureTestingModule({providers:[provideRouter([
   {path:'alunos/novo',component:FormularioAlunoComponent,canDeactivate:[alteracoesPendentesGuard]},
   {path:'alunos',component:ListaAlunosComponent}
  ]),{provide:AlunoService,useValue:service},{provide:AuthService,useValue:{administrador:()=>true}}]});
 });
 it('bloqueia o envio com CPF inválido e habilita com CPF válido',async()=>{
  const harness=await RouterTestingHarness.create();
  const component=await harness.navigateByUrl('/alunos/novo',FormularioAlunoComponent);
  const preencher=async(cpf:string)=>{
   for(const [nome,valor] of Object.entries({nome:'Ana Souza',email:'ana@email.com',telefone:'81999999999',cpf})){
    const input=harness.routeNativeElement!.querySelector(`[name="${nome}"]`) as HTMLInputElement;
    input.value=valor;input.dispatchEvent(new Event('input'));input.dispatchEvent(new Event('blur'));
   }
   harness.detectChanges();await harness.fixture.whenStable();harness.detectChanges();
  };
  await preencher('52998224726');
  const submit=harness.routeNativeElement!.querySelector('button:not([type])') as HTMLButtonElement;
  expect(submit.disabled).toBeTrue();expect(harness.routeNativeElement!.textContent).toContain('Informe um CPF válido.');
  component.salvar();expect(service.cadastrar).not.toHaveBeenCalled();
  component.errosCampos={};await preencher('529.982.247-25');expect(submit.disabled).toBeFalse();
 });
 it('mantém o cadastro aberto quando a saída é recusada',async()=>{
  const confirm=spyOn(window,'confirm').and.returnValue(false);
  const harness=await RouterTestingHarness.create();
  const component=await harness.navigateByUrl('/alunos/novo',FormularioAlunoComponent);
  component.aluno.nomeCompleto='Ana Souza';
  expect(await TestBed.inject(Router).navigateByUrl('/alunos')).toBeFalse();
  expect(TestBed.inject(Router).url).toBe('/alunos/novo');expect(confirm).toHaveBeenCalledTimes(1);
 });
 it('confirma uma única vez ao cancelar e permite sair ao aceitar',async()=>{
  const confirm=spyOn(window,'confirm').and.returnValue(true);
  const harness=await RouterTestingHarness.create();
  const component=await harness.navigateByUrl('/alunos/novo',FormularioAlunoComponent);
  component.aluno.nomeCompleto='Ana Souza';component.cancelar();
  await harness.fixture.whenStable();
  expect(TestBed.inject(Router).url).toBe('/alunos');expect(confirm).toHaveBeenCalledTimes(1);
 });
 it('permite sair sem confirmação quando não há alterações ou após salvar',async()=>{
  const confirm=spyOn(window,'confirm');const harness=await RouterTestingHarness.create();
  await harness.navigateByUrl('/alunos/novo',FormularioAlunoComponent);
  expect(await TestBed.inject(Router).navigateByUrl('/alunos')).toBeTrue();
  const component=await harness.navigateByUrl('/alunos/novo',FormularioAlunoComponent);
  component.aluno.nomeCompleto='Ana Souza';component.salvo=true;
  expect(await TestBed.inject(Router).navigateByUrl('/alunos')).toBeTrue();expect(confirm).not.toHaveBeenCalled();
 });
 it('protege as rotas reais de cadastro e edição',()=>{
  for(const path of ['alunos/novo','alunos/:id/editar'])expect(routes.find(route=>route.path===path)?.canDeactivate).toContain(alteracoesPendentesGuard);
 });
});
