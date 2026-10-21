import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

/**
 * Tela de autenticação.
 * O HTML fica separado para facilitar leitura e manutenção.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  usuario = '';
  senha = '';
  erro = '';
  carregando = false;

  constructor(
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    // Mostra avisos sobre sessão expirada, quando existirem.
    const avisoSessao = sessionStorage.getItem('avisoSessao');

    if (avisoSessao) {
      this.erro = avisoSessao;
      sessionStorage.removeItem('avisoSessao');
    }
  }

  /** Envia as credenciais ao serviço de autenticação. */
  entrar(): void {
    if (this.carregando) return;

    this.erro = '';
    this.carregando = true;

    this.auth.login(this.usuario, this.senha).subscribe({
      next: () => {
        void this.router.navigate(['/alunos']);
      },
      error: (respostaErro) => {
        this.erro = respostaErro.error?.mensagem ?? 'Usuário ou senha inválidos.';
        this.carregando = false;
      },
    });
  }
}
