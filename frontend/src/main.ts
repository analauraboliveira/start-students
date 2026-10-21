import { bootstrapApplication } from '@angular/platform-browser';

import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

// Inicializa a aplicação Angular com as rotas e serviços globais.
bootstrapApplication(AppComponent, appConfig).catch((erroInicializacao) => {
  console.error('Não foi possível iniciar a aplicação.', erroInicializacao);
});
