import { routes } from '../../app.routes';
import { FormularioAlunoComponent } from './formulario-aluno.component';
import { NovoAlunoComponent } from './novo-aluno.component';
import { alteracoesPendentesGuard } from '../../core/guards/alteracoes-pendentes.guard';

describe('Rotas de alunos', () => {
  it('separa cadastro e edição', () => {
    expect(routes.find((rota) => rota.path === 'alunos/novo')?.component).toBe(NovoAlunoComponent);
    expect(routes.find((rota) => rota.path === 'alunos/:id/editar')?.component).toBe(FormularioAlunoComponent);
  });

  it('protege os formulários contra perda de dados', () => {
    for (const caminho of ['alunos/novo', 'alunos/:id/editar']) {
      expect(routes.find((rota) => rota.path === caminho)?.canDeactivate)
        .toContain(alteracoesPendentesGuard);
    }
  });
});
