import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { AlunoService } from '../../core/services/aluno.service';
import { DetalhesAlunoComponent } from './detalhes-aluno.component';

describe('DetalhesAlunoComponent', () => {
  it('busca os dados pelo identificador da rota', () => {
    const servico = jasmine.createSpyObj<AlunoService>('AlunoService', ['buscar']);
    servico.buscar.and.returnValue(of({
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
        { provide: AlunoService, useValue: servico },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '7' }) } },
        },
      ],
    });

    const componente = TestBed.createComponent(DetalhesAlunoComponent);
    componente.detectChanges();

    expect(servico.buscar).toHaveBeenCalledWith(7);
    expect(componente.nativeElement.textContent).toContain('Ana Souza');
  });
});
