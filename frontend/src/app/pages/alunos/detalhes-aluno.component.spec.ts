import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AlunoService } from '../../core/services/aluno.service';
import { DetalhesAlunoComponent } from './detalhes-aluno.component';

describe('DetalhesAlunoComponent', () => {
  it('busca os dados pelo identificador da rota', () => {
    const servicoAlunos = jasmine.createSpyObj<AlunoService>('AlunoService', ['buscar']);
    servicoAlunos.buscar.and.returnValue(of({
      id: 7,
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
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '7' }) } },
        },
      ],
    });

    const paginaDetalhes = TestBed.createComponent(DetalhesAlunoComponent);
    paginaDetalhes.detectChanges();

    expect(servicoAlunos.buscar).toHaveBeenCalledWith(7);
    expect(paginaDetalhes.nativeElement.textContent).toContain('Ana Souza');
  });
});
