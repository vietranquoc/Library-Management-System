import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
  UrlTree,
} from '@angular/router';

export const authGuard: CanActivateFn = (): boolean | UrlTree => {
  const router = inject(Router);
  const token = localStorage.getItem('access_token');

  if (token) {
    return true;
  }

  return router.parseUrl('/auth/login');
};


