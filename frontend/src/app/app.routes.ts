import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth.guard';
import { alteracoesPendentesGuard } from './core/guards/alteracoes-pendentes.guard';
import { DetalhesAlunoComponent } from './pages/alunos/detalhes-aluno.component';
import { FormularioAlunoComponent } from './pages/alunos/formulario-aluno.component';
import { ListaAlunosComponent } from './pages/alunos/lista-alunos.component';
import { NovoAlunoComponent } from './pages/alunos/novo-aluno.component';
import { LoginComponent } from './pages/login/login.component';

// Cadastro e edição são rotas separadas, cada uma com seu próprio componente.
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'alunos', component: ListaAlunosComponent, canActivate: [authGuard] },
  {
    path: 'alunos/novo',
    component: NovoAlunoComponent,
    canActivate: [authGuard],
    canDeactivate: [alteracoesPendentesGuard],
  },
  {
    path: 'alunos/:id/editar',
    component: FormularioAlunoComponent,
    canActivate: [authGuard],
    canDeactivate: [alteracoesPendentesGuard],
  },
  {
    path: 'alunos/:id',
    component: DetalhesAlunoComponent,
    canActivate: [authGuard],
  },
  { path: '', pathMatch: 'full', redirectTo: 'alunos' },
  { path: '**', redirectTo: 'alunos' },
];
