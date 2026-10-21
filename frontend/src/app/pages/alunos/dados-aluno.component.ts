import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Aluno } from '../../models/aluno.model';

/** Apresenta os dados de um aluno sem permitir edição. */
@Component({
  selector: 'app-dados-aluno',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dados-aluno.component.html',
})
export class DadosAlunoComponent {
  @Input({ required: true }) aluno!: Aluno;
}
