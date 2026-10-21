import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { AlunoService } from '../../core/services/aluno.service';
import { NovoAlunoComponent } from './novo-aluno.component';

describe('NovoAlunoComponent', () => {
  let componente: NovoAlunoComponent;
  let servicoAlunos: jasmine.SpyObj<AlunoService>;

  beforeEach(() => {
    servicoAlunos = jasmine.createSpyObj<AlunoService>('AlunoService', ['cadastrar']);
    servicoAlunos.cadastrar.and.returnValue(of({
      id: 1,
      nomeCompleto: 'Ana Souza',
      cpf: '52998224725',
      email: 'ana@email.com',
      telefone: '81999999999',
      matricula: '20260001',
      status: 'ATIVO',
    }));

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AlunoService, useValue: servicoAlunos },
        { provide: AuthService, useValue: { administrador: () => true } },
      ],
    });

    const fixture = TestBed.createComponent(NovoAlunoComponent);
    componente = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('não envia um cadastro com CPF inválido', () => {
    componente.formulario.setValue({
      nomeCompleto: 'Ana Souza',
      cpf: '11111111111',
      email: 'ana@email.com',
      telefone: '81999999999',
      foto: '',
    });

    componente.salvar();

    expect(componente.formulario.controls.cpf.invalid).toBeTrue();
    expect(servicoAlunos.cadastrar).not.toHaveBeenCalled();
  });

  it('envia os dados válidos com status ativo', () => {
    componente.formulario.setValue({
      nomeCompleto: 'Ana Souza',
      cpf: '52998224725',
      email: 'ana@email.com',
      telefone: '81999999999',
      foto: '',
    });

    componente.salvar();

    expect(servicoAlunos.cadastrar).toHaveBeenCalledWith(
      jasmine.objectContaining({ nomeCompleto: 'Ana Souza', status: 'ATIVO' }),
    );
    expect(componente.salvo).toBeTrue();
  });

  it('não pede confirmação antes de preencher o formulário', () => {
    expect(componente.podeSair()).toBeTrue();
  });
});
