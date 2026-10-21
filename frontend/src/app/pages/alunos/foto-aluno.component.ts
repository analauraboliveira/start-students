import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

/** Foto reutilizável nos formulários de cadastro e edição. */
@Component({
  selector: 'app-foto-aluno',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './foto-aluno.component.html',
})
export class FotoAlunoComponent {
  @Input() foto = '';
  @Output() selecionar = new EventEmitter<Event>();
  @Output() remover = new EventEmitter<void>();
}
