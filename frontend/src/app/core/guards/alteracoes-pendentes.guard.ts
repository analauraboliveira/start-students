import { CanDeactivateFn } from '@angular/router';

/** Contrato usado pelas telas que precisam proteger alterações não salvas. */
export interface FormularioComAlteracoes {
  podeSair(): boolean;
}

/**
 * Antes de mudar de rota, pergunta ao componente se a navegação é permitida.
 * Cada formulário decide se há alterações pendentes.
 */
export const alteracoesPendentesGuard: CanDeactivateFn<FormularioComAlteracoes> = (
  componente,
) => componente.podeSair();
