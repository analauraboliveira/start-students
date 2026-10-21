import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { AlunoService } from '../../core/services/aluno.service';
import { cpfReactiveValidator } from '../../core/validators/cpf-reactive.validator';
import { Aluno } from '../../models/aluno.model';
import { FotoAlunoComponent } from './foto-aluno.component';

/**
 * Fluxo de cadastro: preencher -> validar -> enviar para a API -> voltar à lista.
 * A edição fica em outro componente, pois tem regras diferentes.
 */
@Component({
  selector: 'app-novo-aluno',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, FotoAlunoComponent],
  templateUrl: './novo-aluno.component.html',
})
export class NovoAlunoComponent implements OnInit {
  // Os validadores prontos do Angular cuidam das regras mais comuns.
  readonly formulario = this.formBuilder.nonNullable.group({
    nomeCompleto: ['', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(120),
      Validators.pattern(/^[\p{L} .'-]+$/u),
    ]],
    cpf: ['', [Validators.required, cpfReactiveValidator]],
    email: ['', [Validators.required, Validators.email]],
    telefone: ['', [
      Validators.required,
      Validators.pattern(/^\D*(?:\d\D*){10,11}$/),
    ]],
    foto: [''],
  });

  salvando = false;
  salvo = false;
  tentouSalvar = false;
  semPermissao = false;
  erro = '';
  sucesso = '';
  errosCampos: Record<string, string> = {};

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly alunoService: AlunoService,
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.semPermissao = !this.authService.administrador();
  }

  /** Mostra erros após a interação com o campo ou tentativa de envio. */
  campoInvalido(nomeCampo: keyof typeof this.formulario.controls): boolean {
    const controle = this.formulario.controls[nomeCampo];
    return controle.invalid && (controle.touched || this.tentouSalvar);
  }

  /** Traduz as validações em mensagens que a pessoa consegue entender. */
  mensagemCampo(nomeCampo: keyof typeof this.formulario.controls): string {
    if (this.errosCampos[nomeCampo]) {
      return this.errosCampos[nomeCampo];
    }

    const erros = this.formulario.controls[nomeCampo].errors;
    if (erros?.['required']) return 'Este campo é obrigatório.';
    if (erros?.['email']) return 'Informe um e-mail válido.';
    if (erros?.['cpfInvalido']) return 'Informe um CPF válido.';
    if (nomeCampo === 'telefone') return 'Informe DDD e telefone com 10 ou 11 dígitos.';
    if (nomeCampo === 'nomeCompleto') {
      return 'Use de 3 a 120 caracteres, apenas letras e pontuação permitida.';
    }
    return '';
  }

  /** Confere formato e tamanho antes de converter a imagem em texto base64. */
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

  /** Só envia para o backend quando o formulário estiver válido. */
  salvar(): void {
    this.tentouSalvar = true;
    this.formulario.markAllAsTouched();

    if (this.semPermissao || this.salvando || this.formulario.invalid) {
      return;
    }

    this.salvando = true;
    this.erro = '';
    this.sucesso = '';
    this.errosCampos = {};

    // No cadastro, o backend gera a matrícula e o status começa ATIVO.
    const dadosAluno: Partial<Aluno> = {
      ...this.formulario.getRawValue(),
      status: 'ATIVO',
    };

    this.alunoService.cadastrar(dadosAluno).subscribe({
      next: (alunoCadastrado) => {
        this.salvo = true;
        this.salvando = false;
        this.sucesso = 'Aluno cadastrado. Matrícula: ' + alunoCadastrado.matricula;
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

  /** O guard pergunta antes de perder informações preenchidas. */
  podeSair(): boolean {
    return this.salvo
      || !this.formulario.dirty
      || confirm('Você tem alterações não salvas. Deseja sair mesmo?');
  }

  @HostListener('window:beforeunload', ['$event'])
  confirmarFechamento(evento: BeforeUnloadEvent): void {
    if (this.formulario.dirty && !this.salvo) {
      evento.preventDefault();
    }
  }
}
