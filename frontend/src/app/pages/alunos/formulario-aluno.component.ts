import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { AlunoService } from '../../core/services/aluno.service';
import { Aluno, StatusAluno } from '../../models/aluno.model';
import { FotoAlunoComponent } from './foto-aluno.component';

/**
 * Esta tela é só de edição. O cadastro fica no NovoAlunoComponent.
 * Fluxo: ID da rota -> busca aluno -> preenche formulário -> salva alterações.
 */
@Component({
  selector: 'app-formulario-aluno',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, FotoAlunoComponent],
  templateUrl: './formulario-aluno.component.html',
})
export class FormularioAlunoComponent implements OnInit {
  readonly formulario = this.formBuilder.nonNullable.group({
    nomeCompleto: ['', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(120),
      Validators.pattern(/^[\p{L} .'-]+$/u),
    ]],
    email: ['', [Validators.required, Validators.email]],
    telefone: ['', [
      Validators.required,
      Validators.pattern(/^\D*(?:\d\D*){10,11}$/),
    ]],
    foto: [''],
    status: this.formBuilder.nonNullable.control<StatusAluno>('ATIVO', Validators.required),
  });

  aluno?: Aluno;
  carregando = true;
  salvando = false;
  salvo = false;
  semPermissao = false;
  tentouSalvar = false;
  erro = '';
  sucesso = '';
  errosCampos: Record<string, string> = {};

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly alunoService: AlunoService,
    private readonly authService: AuthService,
    private readonly rotaAtiva: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.authService.administrador()) {
      this.semPermissao = true;
      this.carregando = false;
      return;
    }

    const identificadorAluno = Number(this.rotaAtiva.snapshot.paramMap.get('id'));
    if (!Number.isInteger(identificadorAluno) || identificadorAluno <= 0) {
      this.erro = 'Identificador do aluno inválido.';
      this.carregando = false;
      return;
    }

    // Primeiro busca os dados; só depois libera o formulário.
    this.alunoService.buscar(identificadorAluno).subscribe({
      next: (alunoEncontrado) => {
        this.aluno = alunoEncontrado;
        this.formulario.reset({
          nomeCompleto: alunoEncontrado.nomeCompleto,
          email: alunoEncontrado.email,
          telefone: alunoEncontrado.telefone,
          foto: alunoEncontrado.foto ?? '',
          status: alunoEncontrado.status,
        });
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Não foi possível carregar o aluno.';
        this.carregando = false;
      },
    });
  }

  campoInvalido(nomeCampo: keyof typeof this.formulario.controls): boolean {
    const controle = this.formulario.controls[nomeCampo];
    return controle.invalid && (controle.touched || this.tentouSalvar);
  }

  mensagemCampo(nomeCampo: keyof typeof this.formulario.controls): string {
    if (this.errosCampos[nomeCampo]) return this.errosCampos[nomeCampo];

    const erros = this.formulario.controls[nomeCampo].errors;
    if (erros?.['required']) return 'Este campo é obrigatório.';
    if (erros?.['email']) return 'Informe um e-mail válido.';
    if (nomeCampo === 'telefone') return 'Informe DDD e telefone com 10 ou 11 dígitos.';
    if (nomeCampo === 'nomeCompleto') {
      return 'Use de 3 a 120 caracteres, apenas letras e pontuação permitida.';
    }
    return '';
  }

  fotoSelecionada(evento: Event): void {
    const campoArquivo = evento.target as HTMLInputElement;
    const arquivo = campoArquivo.files?.[0];
    if (!arquivo) return;

    if (!['image/jpeg', 'image/png'].includes(arquivo.type)) {
      this.erro = 'A foto deve ser JPEG ou PNG.';
      campoArquivo.value = '';
      return;
    }
    if (arquivo.size > 5 * 1024 * 1024) {
      this.erro = 'A foto deve ter no máximo 5 MB.';
      campoArquivo.value = '';
      return;
    }

    const leitorArquivo = new FileReader();
    leitorArquivo.onload = () => {
      this.formulario.controls.foto.setValue(String(leitorArquivo.result));
      this.formulario.controls.foto.markAsDirty();
      this.erro = '';
    };
    leitorArquivo.onerror = () => {
      this.erro = 'Não foi possível ler a foto.';
    };
    leitorArquivo.readAsDataURL(arquivo);
  }

  removerFoto(): void {
    this.formulario.controls.foto.setValue('');
    this.formulario.controls.foto.markAsDirty();
  }

  /** Só salva se houver alterações válidas e nenhum envio em andamento. */
  salvar(): void {
    this.tentouSalvar = true;
    this.formulario.markAllAsTouched();

    if (!this.aluno || this.salvando || this.formulario.invalid || !this.formulario.dirty) {
      return;
    }

    this.salvando = true;
    this.erro = '';
    this.sucesso = '';
    this.errosCampos = {};

    this.alunoService.editar(this.aluno.id, {
      ...this.formulario.getRawValue(),
      cpf: this.aluno.cpf,
      version: this.aluno.version,
    }).subscribe({
      next: () => {
        this.salvo = true;
        this.salvando = false;
        this.sucesso = 'Alterações salvas.';
        void this.router.navigate(['/alunos']);
      },
      error: (respostaErro) => {
        this.errosCampos = respostaErro.error?.campos ?? {};
        this.erro = respostaErro.error?.mensagem
          ?? 'Confira os dados e tente novamente.';
        this.salvando = false;
      },
    });
  }

  cancelar(): void {
    void this.router.navigate(['/alunos']);
  }

  podeSair(): boolean {
    return this.salvo
      || !this.formulario.dirty
      || confirm('Você tem alterações não salvas. Deseja sair mesmo?');
  }

  @HostListener('window:beforeunload', ['$event'])
  confirmarFechamento(evento: BeforeUnloadEvent): void {
    if (this.formulario.dirty && !this.salvo) evento.preventDefault();
  }
}
