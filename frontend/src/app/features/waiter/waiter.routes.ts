import { Routes } from '@angular/router';
import { WaiterShellComponent } from './waiter-shell.component';

export const waiterRoutes: Routes = [
  {
    path: '',
    component: WaiterShellComponent,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./waiter-tables.component').then((m) => m.WaiterTablesComponent),
      },
      {
        path: 'table/:tableId',
        loadComponent: () =>
          import('./waiter-order.component').then((m) => m.WaiterOrderComponent),
      },
    ],
  },
];