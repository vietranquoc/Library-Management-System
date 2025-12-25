import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
  UrlTree,
} from '@angular/router';
import { JwtUtil } from '../../shared/utils/jwt.util';

export const staffGuard: CanActivateFn = (): boolean | UrlTree => {
  const router = inject(Router);
  const token = localStorage.getItem('access_token');

  if (!token) {
    return router.parseUrl('/auth/login');
  }

  if (!JwtUtil.isAdminOrStaff(token)) {
    // Nếu không phải admin hoặc staff, redirect về home
    return router.parseUrl('/home');
  }

  return true;
};

