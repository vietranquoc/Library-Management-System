import { Routes } from '@angular/router';
import { Login } from './auth/pages/login/login';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { staffGuard } from './core/guards/staff.guard';

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
    path: 'books',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/search-books/search-books').then((m) => m.SearchBooks),
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./admin/pages/dashboard/dashboard').then((m) => m.AdminDashboard),
      },
      {
        path: 'categories',
        loadComponent: () =>
          import('./admin/pages/categories/categories').then((m) => m.AdminCategories),
      },
      {
        path: 'books',
        loadComponent: () =>
          import('./admin/pages/books/books').then((m) => m.AdminBooks),
      },
      {
        path: 'staff',
        loadComponent: () =>
          import('./admin/pages/staff/staff').then((m) => m.AdminStaff),
      },
      {
        path: 'members',
        loadComponent: () =>
          import('./admin/pages/members/members').then((m) => m.AdminMembers),
      },
      {
        path: 'loans',
        loadComponent: () =>
          import('./admin/pages/loans/loans').then((m) => m.AdminLoans),
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: 'staff',
    canActivate: [staffGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./staff/pages/dashboard/dashboard').then((m) => m.StaffDashboard),
      },
      {
        path: 'loan-requests',
        loadComponent: () =>
          import('./staff/pages/loan-requests/loan-requests').then((m) => m.StaffLoanRequests),
      },
      {
        path: 'loan-requests/:loanId',
        loadComponent: () =>
          import('./staff/pages/loan-requests/loan-requests').then((m) => m.StaffLoanRequests),
      },
      {
        path: 'loans',
        loadComponent: () =>
          import('./staff/pages/loans/loans').then((m) => m.StaffLoans),
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];
