import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { AlunoService } from '../../core/services/aluno.service';
import { Aluno, Pagina, StatusAluno } from '../../models/aluno.model';

/**
 * Tela principal dos alunos.
 *
 * Responsabilidades:
 * 1. Buscar alunos na API com filtros e paginação.
 * 2. Manter a busca ao voltar da página de detalhes.
 * 3. Controlar a confirmação de exclusão.
 *
 * Cadastro, edição e detalhes têm seus próprios componentes.
 */
@Component({
  selector: 'app-lista-alunos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './lista-alunos.component.html',
})
export class ListaAlunosComponent implements OnInit {
  // Dados recebidos do backend.
  dados?: Pagina<Aluno>;

  // Filtros e opções da listagem.
  termo = '';
  nome = '';
  matricula = '';
  status: '' | StatusAluno = '';
  ordenacao = 'nomeCompleto,asc';
  pagina = 0;

  // Estados exibidos na tela.
  carregando = false;
  erro = '';
  feedback = '';
  admin = false;

  // Dados usados pelo modal de confirmação de exclusão.
  alunoExcluir?: Aluno;
  excluindo = false;
  acionador?: HTMLElement;

  constructor(
    private readonly alunos: AlunoService,
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    // As ações de cadastro, edição e exclusão só aparecem para administradores.
    this.admin = this.auth.administrador();

    // Restaura a pesquisa anterior, inclusive a página em que a pessoa estava.
    this.restaurar();
    this.termo = this.nome || this.matricula;
    this.carregar(this.pagina);
  }

  /** Busca por matrícula quando o texto tem só números; caso contrário, por nome. */
  buscarTermo(): void {
    const textoDigitado = this.termo.trim();
    const pesquisaPorMatricula = /^\d+$/.test(textoDigitado);

    this.nome = pesquisaPorMatricula ? '' : textoDigitado;
    this.matricula = pesquisaPorMatricula ? textoDigitado : '';

    this.buscar();
  }

  /** Uma nova pesquisa sempre começa pela primeira página. */
  buscar(): void {
    this.carregar(0);
  }

  /** Limpa todos os filtros, mas mantém a ordenação escolhida. */
  limpar(): void {
    this.termo = '';
    this.nome = '';
    this.matricula = '';
    this.status = '';

    this.carregar(0);
  }

  /** Consulta a API e atualiza os estados de carregamento e erro. */
  carregar(numeroPagina: number): void {
    this.pagina = numeroPagina;
    this.carregando = true;
    this.erro = '';

    this.salvarEstado();

    this.alunos.listar({
      nome: this.nome,
      matricula: this.matricula,
      status: this.status,
      page: numeroPagina,
      sort: this.ordenacao,
    }).subscribe({
      next: (paginaRecebida) => {
        this.dados = paginaRecebida;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Não foi possível carregar os alunos.';
        this.carregando = false;
      },
    });
  }

  /** Gera os números das páginas para os botões de paginação. */
  paginas(): number[] {
    const totalPaginas = this.dados?.totalPages ?? 0;

    return Array.from(
      { length: totalPaginas },
      (_, indicePagina) => indicePagina,
    );
  }

  /** Abre a confirmação antes de permitir a exclusão de um aluno. */
  abrirExclusao(alunoSelecionado: Aluno, evento: Event): void {
    this.alunoExcluir = alunoSelecionado;
    this.acionador = evento.currentTarget as HTMLElement;

    // Aguarda o Angular mostrar o modal antes de mover o foco.
    setTimeout(() => {
      document.querySelector<HTMLElement>('.modal .secundario')?.focus();
    });
  }

  /** Fecha o modal e devolve o foco ao botão que o abriu. */
  cancelarExclusao(): void {
    this.alunoExcluir = undefined;

    setTimeout(() => {
      this.acionador?.focus();
    });
  }

  /** Permite fechar com Escape e mantém a navegação por Tab dentro do modal. */
  @HostListener('document:keydown', ['$event'])
  teclaModal(evento: KeyboardEvent): void {
    if (!this.alunoExcluir) {
      return;
    }

    if (evento.key === 'Escape') {
      evento.preventDefault();
      this.cancelarExclusao();
      return;
    }

    if (evento.key !== 'Tab') {
      return;
    }

    const botoesModal = Array.from(
      document.querySelectorAll<HTMLElement>('.modal button:not([disabled])'),
    );

    if (botoesModal.length === 0) {
      return;
    }

    const primeiroBotao = botoesModal[0];
    const ultimoBotao = botoesModal[botoesModal.length - 1];

    if (evento.shiftKey && document.activeElement === primeiroBotao) {
      evento.preventDefault();
      ultimoBotao.focus();
    } else if (!evento.shiftKey && document.activeElement === ultimoBotao) {
      evento.preventDefault();
      primeiroBotao.focus();
    }
  }

  /** Envia a exclusão à API apenas após a confirmação no modal. */
  confirmarExclusao(): void {
    if (!this.alunoExcluir || this.excluindo) {
      return;
    }

    this.excluindo = true;
    const identificadorAluno = this.alunoExcluir.id;

    this.alunos.excluir(identificadorAluno).subscribe({
      next: () => {
        this.feedback = 'Aluno excluído com sucesso.';

        // Se a última linha da página foi removida, volta uma página.
        if ((this.dados?.content.length ?? 0) === 1 && this.pagina > 0) {
          this.pagina--;
        }

        this.alunoExcluir = undefined;
        this.excluindo = false;
        this.carregar(this.pagina);

        setTimeout(() => this.acionador?.focus());
      },
      error: (respostaErro) => {
        this.erro = respostaErro.status === 403
          ? 'Você não tem permissão para esta ação.'
          : 'Não foi possível excluir.';

        this.excluindo = false;
        this.alunoExcluir = undefined;

        setTimeout(() => this.acionador?.focus());
      },
    });
  }

  /** Guarda filtros e página no navegador para restaurar a lista depois. */
  private salvarEstado(): void {
    const estadoAtual = {
      nome: this.nome,
      matricula: this.matricula,
      status: this.status,
      ordenacao: this.ordenacao,
      pagina: this.pagina,
    };

    sessionStorage.setItem('listaAlunos', JSON.stringify(estadoAtual));
  }

  /** Lê o estado salvo; se estiver inválido, mantém os filtros iniciais. */
  private restaurar(): void {
    try {
      const estadoSalvo = JSON.parse(
        sessionStorage.getItem('listaAlunos') || 'null',
      );

      if (!estadoSalvo) {
        return;
      }

      this.nome = estadoSalvo.nome || '';
      this.matricula = estadoSalvo.matricula || '';
      this.status = estadoSalvo.status || '';
      this.ordenacao = estadoSalvo.ordenacao || 'nomeCompleto,asc';
      this.pagina = estadoSalvo.pagina || 0;
    } catch {
      // Não impede a abertura da página se o armazenamento estiver inválido.
    }
  }

  /** Limpa a sessão e retorna para a tela de login. */
  sair(): void {
    sessionStorage.removeItem('listaAlunos');
    this.auth.logout();

    void this.router.navigate(['/login']);
  }
}
