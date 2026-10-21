import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { AlunoService } from '../../core/services/aluno.service';
import { Aluno } from '../../models/aluno.model';
import { ListaAlunosComponent } from './lista-alunos.component';

describe('ListaAlunosComponent', () => {
  let fixture: ComponentFixture<ListaAlunosComponent>;
  let servicoAlunos: jasmine.SpyObj<AlunoService>;

  const alunoExemplo: Aluno = {
    id: 1,
    nomeCompleto: 'Ana Souza',
    email: 'ana@email.com',
    cpf: '52998224725',
    telefone: '81999999999',
    matricula: '20260001',
    status: 'ATIVO',
  };

  beforeEach(async () => {
    sessionStorage.removeItem('listaAlunos');

    // Substitui chamadas HTTP por respostas controladas em cada teste.
    servicoAlunos = jasmine.createSpyObj<AlunoService>('AlunoService', ['listar', 'excluir']);
    servicoAlunos.listar.and.returnValue(of({
      content: [alunoExemplo],
      totalElements: 1,
      totalPages: 1,
      number: 0,
      size: 10,
    }));
    servicoAlunos.excluir.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [ListaAlunosComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: AlunoService, useValue: servicoAlunos },
        { provide: AuthService, useValue: { administrador: () => true, logout: () => {} } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaAlunosComponent);
    fixture.detectChanges();
  });

  it('exibe os alunos retornados pela API', () => {
    expect(fixture.nativeElement.textContent).toContain('Ana Souza');
  });

  it('só exclui o aluno após a confirmação no modal', () => {
    const botaoExcluir = fixture.nativeElement.querySelector('.acao.excluir') as HTMLButtonElement;
    botaoExcluir.click();
    fixture.detectChanges();

    expect(servicoAlunos.excluir).not.toHaveBeenCalled();

    const botaoConfirmar = fixture.nativeElement.querySelector('.modal .perigo') as HTMLButtonElement;
    botaoConfirmar.click();
    fixture.detectChanges();

    expect(servicoAlunos.excluir).toHaveBeenCalledWith(1);
    expect(fixture.componentInstance.feedback).toBe('Aluno excluído com sucesso.');
  });

  it('interpreta uma busca textual como nome', () => {
    const componente = fixture.componentInstance;
    componente.termo = '  ana  ';
    componente.buscarTermo();

    expect(componente.nome).toBe('ana');
    expect(componente.matricula).toBe('');
    expect(componente.pagina).toBe(0);
  });

  it('interpreta uma busca numérica como matrícula', () => {
    const componente = fixture.componentInstance;
    componente.termo = ' 202600 ';
    componente.buscarTermo();

    expect(componente.nome).toBe('');
    expect(componente.matricula).toBe('202600');
    expect(componente.pagina).toBe(0);
  });

  it('limpa os filtros e volta à primeira página', () => {
    const componente = fixture.componentInstance;
    componente.termo = 'Ana';
    componente.nome = 'Ana';
    componente.matricula = '2026';
    componente.status = 'ATIVO';
    componente.pagina = 2;

    componente.limpar();

    expect(componente.termo).toBe('');
    expect(componente.nome).toBe('');
    expect(componente.matricula).toBe('');
    expect(componente.status).toBe('');
    expect(componente.pagina).toBe(0);
  });

  it('cancela a exclusão sem chamar o backend', () => {
    const componente = fixture.componentInstance;
    componente.abrirExclusao(alunoExemplo, new Event('click'));
    componente.cancelarExclusao();

    expect(componente.alunoExcluir).toBeUndefined();
    expect(servicoAlunos.excluir).not.toHaveBeenCalled();
  });

  it('volta uma página quando exclui o último item', () => {
    const componente = fixture.componentInstance;
    componente.pagina = 2;
    componente.dados = {
      content: [alunoExemplo],
      totalElements: 21,
      totalPages: 3,
      number: 2,
      size: 10,
    };
    componente.alunoExcluir = alunoExemplo;
    componente.confirmarExclusao();

    expect(componente.pagina).toBe(1);
  });

  it('restaura os filtros e a página salvos', () => {
    sessionStorage.setItem('listaAlunos', JSON.stringify({
      nome: 'Ana',
      matricula: '',
      status: 'ATIVO',
      pagina: 2,
    }));

    const componente = fixture.componentInstance;
    componente.ngOnInit();

    expect(componente.nome).toBe('Ana');
    expect(componente.status).toBe('ATIVO');
    expect(componente.pagina).toBe(2);
  });

  it('exibe as ações de administrador', () => {
    const paginaHtml = fixture.nativeElement as HTMLElement;

    expect(paginaHtml.querySelector('.acao.editar')).not.toBeNull();
    expect(paginaHtml.querySelector('.acao.excluir')).not.toBeNull();
  });

  it('mostra o nome e a matrícula no modal de exclusão', () => {
    const botaoExcluir = fixture.nativeElement.querySelector('.acao.excluir') as HTMLButtonElement;
    botaoExcluir.click();
    fixture.detectChanges();

    const modal = fixture.nativeElement.querySelector('.modal') as HTMLElement;
    const textoModal = modal.textContent || '';

    expect(textoModal).toContain('Ana Souza');
    expect(textoModal).toContain('20260001');
  });
});
