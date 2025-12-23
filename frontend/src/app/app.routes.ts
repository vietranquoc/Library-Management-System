import { Routes } from '@angular/router';
import { Login } from './auth/pages/login/login';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'auth/login',
    component: Login,
  },
  {
    path: 'auth',
    children: [
      {
        path: 'register',
        loadComponent: () =>
          import('./auth/pages/register/register').then((m) => m.Register),
      },
    ],
  },
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/home/home').then((m) => m.Home),
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];
