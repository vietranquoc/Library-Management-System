import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
  UrlTree,
} from '@angular/router';
import { JwtUtil } from '../../shared/utils/jwt.util';

export const adminGuard: CanActivateFn = (): boolean | UrlTree => {
  const router = inject(Router);
  const token = localStorage.getItem('access_token');

  if (!token) {
    return router.parseUrl('/auth/login');
  }

  if (!JwtUtil.isAdmin(token)) {
    // Nếu không phải admin, redirect về home
    return router.parseUrl('/home');
  }

  return true;
};

