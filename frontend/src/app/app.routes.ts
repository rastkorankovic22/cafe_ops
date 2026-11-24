import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'waiter',
    loadChildren: () => import('./features/waiter/waiter.routes').then((m) => m.waiterRoutes),
    canActivate: [authGuard, roleGuard],
    data: { role: 'WAITER' },
  },
  {
    path: 'owner',
    loadChildren: () => import('./features/owner/owner.routes').then((m) => m.ownerRoutes),
    canActivate: [authGuard, roleGuard],
    data: { role: 'OWNER' },
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];