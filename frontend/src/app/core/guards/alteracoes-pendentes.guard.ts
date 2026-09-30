import {CanDeactivateFn} from '@angular/router';

export interface FormularioComAlteracoes {
  podeSair():boolean;
}

export const alteracoesPendentesGuard:CanDeactivateFn<FormularioComAlteracoes>=(component)=>component.podeSair();
