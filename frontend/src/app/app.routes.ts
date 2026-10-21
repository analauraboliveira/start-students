import {alteracoesPendentesGuard} from './core/guards/alteracoes-pendentes.guard';
import {Routes,CanDeactivateFn} from '@angular/router';import {authGuard} from './core/auth/auth.guard';import {LoginComponent} from './pages/login/login.component';import {ListaAlunosComponent} from './pages/alunos/lista-alunos.component';import {FormularioAlunoComponent} from './pages/alunos/formulario-aluno.component';import {DetalhesAlunoComponent} from './pages/alunos/detalhes-aluno.component';

const confirmarSaidaFormulario:CanDeactivateFn<FormularioAlunoComponent>=component=>component.podeSair();

// todas as telas de alunos precisam de login
export const routes:Routes=[{path:'login',component:LoginComponent},{path:'alunos',component:ListaAlunosComponent,canActivate:[authGuard]},{path:'alunos/novo',component:FormularioAlunoComponent,canActivate:[authGuard],canDeactivate:[alteracoesPendentesGuard]},{path:'alunos/:id',component:DetalhesAlunoComponent,canActivate:[authGuard]},{path:'alunos/:id/editar',component:FormularioAlunoComponent,canActivate:[authGuard],canDeactivate:[alteracoesPendentesGuard]},{path:'',pathMatch:'full',redirectTo:'alunos'},{path:'**',redirectTo:'alunos'}];