import { Routes } from '@angular/router';
import { OwnerShellComponent } from './owner-shell.component';

export const ownerRoutes: Routes = [
  {
    path: '',
    component: OwnerShellComponent,
    children: [
      { path: '', loadComponent: () => import('./owner-home.component').then((m) => m.OwnerHomeComponent) },
      { path: 'staff', loadComponent: () => import('./owner-staff.component').then((m) => m.OwnerStaffComponent) },
      { path: 'shifts', loadComponent: () => import('./owner-shifts.component').then((m) => m.OwnerShiftsComponent) },
      { path: 'menu', loadComponent: () => import('./owner-menu.component').then((m) => m.OwnerMenuComponent) },
      { path: 'tables', loadComponent: () => import('./owner-tables.component').then((m) => m.OwnerTablesComponent) },
      { path: 'report', loadComponent: () => import('./owner-report.component').then((m) => m.OwnerReportComponent) },
    ],
  },
];