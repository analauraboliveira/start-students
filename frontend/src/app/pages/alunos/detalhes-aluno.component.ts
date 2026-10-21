import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AlunoService } from '../../core/services/aluno.service';
import { Aluno } from '../../models/aluno.model';
import { DadosAlunoComponent } from './dados-aluno.component';

/**
 * Exibe os dados do aluno em um modal somente de leitura.
 * Fluxo: recebe o ID da rota -> busca na API -> mostra os dados.
 */
@Component({
  selector: 'app-detalhes-aluno',
  standalone: true,
  imports: [CommonModule, DadosAlunoComponent],
  templateUrl: './detalhes-aluno.component.html',
})
export class DetalhesAlunoComponent implements OnInit {
  aluno?: Aluno;
  carregando = true;
  erro = '';

  constructor(
    private readonly alunoService: AlunoService,
    private readonly rotaAtiva: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const identificadorAluno = Number(this.rotaAtiva.snapshot.paramMap.get('id'));

    // Não faz a chamada se o ID da rota não for válido.
    if (!Number.isInteger(identificadorAluno) || identificadorAluno <= 0) {
      this.erro = 'Identificador do aluno inválido.';
      this.carregando = false;
      return;
    }

    this.alunoService.buscar(identificadorAluno).subscribe({
      next: (alunoEncontrado) => {
        this.aluno = alunoEncontrado;
        this.carregando = false;

        // O foco vai para o botão de voltar quando o modal terminar de abrir.
        setTimeout(() => {
          document.querySelector<HTMLElement>('.detalhe-modal .voltar-btn')?.focus();
        });
      },
      error: (respostaErro) => {
        this.erro = respostaErro.status === 404
          ? 'Aluno não encontrado.'
          : 'Não foi possível carregar os dados do aluno.';
        this.carregando = false;
      },
    });
  }

  /** Fecha o modal com a tecla Esc, além do botão Voltar. */
  @HostListener('document:keydown.escape')
  aoPressionarEscape(): void {
    this.voltar();
  }

  voltar(): void {
    void this.router.navigate(['/alunos']);
  }
}
