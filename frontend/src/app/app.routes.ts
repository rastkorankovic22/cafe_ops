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
    loadComponent: () => import('./features/waiter/waiter.component').then((m) => m.WaiterComponent),
    canActivate: [authGuard, roleGuard],
    data: { role: 'WAITER' },
  },
  {
    path: 'owner',
    loadComponent: () => import('./features/owner/owner.component').then((m) => m.OwnerComponent),
    canActivate: [authGuard, roleGuard],
    data: { role: 'OWNER' },
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];