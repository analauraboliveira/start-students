import {ApplicationConfig} from '@angular/core';import {provideRouter} from '@angular/router';import {provideHttpClient,withInterceptors} from '@angular/common/http';import {routes} from './app.routes';import {authInterceptor} from './core/auth/auth.interceptor';

// liga as rotas e o interceptor nas requisicoes HTTP
export const appConfig:ApplicationConfig={providers:[provideRouter(routes),provideHttpClient(withInterceptors([authInterceptor]))]};