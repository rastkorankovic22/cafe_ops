import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-waiter-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <header class="app-header">
      <a routerLink="/waiter" class="brand">Waiter</a>
      <span class="user">{{ user?.fullName }}</span>
      <button type="button" class="btn btn-ghost btn-sm" (click)="logout()">Logout</button>
    </header>
    <router-outlet />
  `,
})
export class WaiterShellComponent {
  private auth = inject(AuthService);
  user = this.auth.getUser();

  logout() {
    this.auth.logout();
  }
}