import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { UserRole } from './auth.models';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const expected = route.data['role'] as UserRole;
  if (auth.isLoggedIn() && auth.hasRole(expected)) {
    return true;
  }
  return router.createUrlTree([auth.isLoggedIn() ? auth.homeRoute() : '/login']);
};