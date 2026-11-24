import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-waiter-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <header class="bar">
      <a routerLink="/waiter">Tables</a>
      <span>{{ user?.fullName }}</span>
      <button type="button" (click)="logout()">Logout</button>
    </header>
    <router-outlet />
  `,
  styles: [
    `
      .bar {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
        padding: 0.75rem 1rem;
        border-bottom: 1px solid #ddd;
      }
    `,
  ],
})
export class WaiterShellComponent {
  private auth = inject(AuthService);
  user = this.auth.getUser();

  logout() {
    this.auth.logout();
  }
}