import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { routes } from '../../app.routes';
import { AuthService } from '../../core/auth/auth.service';
import { alteracoesPendentesGuard } from '../../core/guards/alteracoes-pendentes.guard';
import { AlunoService } from '../../core/services/aluno.service';
import { Aluno } from '../../models/aluno.model';
import { FormularioAlunoComponent } from './formulario-aluno.component';
import { NovoAlunoComponent } from './novo-aluno.component';

describe('Rotas de alunos', () => {
  it('separa cadastro e edição', () => {
    expect(routes.find((rota) => rota.path === 'alunos/novo')?.component).toBe(NovoAlunoComponent);
    expect(routes.find((rota) => rota.path === 'alunos/:id/editar')?.component).toBe(FormularioAlunoComponent);
  });

  it('protege os formulários contra perda de dados', () => {
    for (const caminho of ['alunos/novo', 'alunos/:id/editar']) {
      expect(routes.find((rota) => rota.path === caminho)?.canDeactivate)
        .toContain(alteracoesPendentesGuard);
    }
  });
});

describe('FormularioAlunoComponent', () => {
  let componente: FormularioAlunoComponent;
  let servicoAlunos: jasmine.SpyObj<AlunoService>;
  let roteador: Router;

  const alunoExistente: Aluno = {
    id: 7,
    nomeCompleto: 'Ana Souza',
    cpf: '52998224725',
    email: 'ana@email.com',
    telefone: '81999999999',
    matricula: '20260001',
    status: 'ATIVO',
    version: 1,
  };

  beforeEach(() => {
    servicoAlunos = jasmine.createSpyObj<AlunoService>('AlunoService', ['buscar', 'editar']);
    servicoAlunos.buscar.and.returnValue(of(alunoExistente));
    servicoAlunos.editar.and.returnValue(of(alunoExistente));

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AlunoService, useValue: servicoAlunos },
        { provide: AuthService, useValue: { administrador: () => true } },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '7' }) } },
        },
      ],
    });

    roteador = TestBed.inject(Router);
    spyOn(roteador, 'navigate').and.resolveTo(true);

    const paginaEdicao = TestBed.createComponent(FormularioAlunoComponent);
    componente = paginaEdicao.componentInstance;
    paginaEdicao.detectChanges();
  });

  it('busca o aluno da rota e preenche o formulário', () => {
    expect(servicoAlunos.buscar).toHaveBeenCalledWith(7);
    expect(componente.formulario.controls.nomeCompleto.value).toBe('Ana Souza');
    expect(componente.formulario.controls.email.value).toBe('ana@email.com');
    expect(componente.formulario.pristine).toBeTrue();
  });

  it('não envia dados se nada foi alterado', () => {
    componente.salvar();
    expect(servicoAlunos.editar).not.toHaveBeenCalled();
  });

  it('não envia alterações quando o formulário é inválido', () => {
    componente.formulario.controls.email.setValue('email-invalido');
    componente.salvar();

    expect(componente.formulario.invalid).toBeTrue();
    expect(servicoAlunos.editar).not.toHaveBeenCalled();
  });

  it('envia alterações válidas com CPF e versão originais', () => {
    componente.formulario.controls.nomeCompleto.setValue('Ana Oliveira');
    componente.salvar();

    expect(servicoAlunos.editar).toHaveBeenCalledWith(
      7,
      jasmine.objectContaining({
        nomeCompleto: 'Ana Oliveira',
        cpf: '52998224725',
        version: 1,
      }),
    );
    expect(componente.salvo).toBeTrue();
    expect(roteador.navigate).toHaveBeenCalledWith(['/alunos']);
  });

  it('mostra o erro da API sem marcar o formulário como salvo', () => {
    servicoAlunos.editar.and.returnValue(
      throwError(() => ({ error: { mensagem: 'Erro ao salvar.' } })),
    );
    componente.formulario.controls.nomeCompleto.setValue('Ana Oliveira');
    componente.salvar();

    expect(componente.erro).toBe('Erro ao salvar.');
    expect(componente.salvo).toBeFalse();
    expect(componente.salvando).toBeFalse();
  });

  it('não pede confirmação se não houve alterações', () => {
    expect(componente.podeSair()).toBeTrue();
  });
});
