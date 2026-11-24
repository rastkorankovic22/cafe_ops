import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-owner-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <header class="app-header">
      <span class="brand">Owner</span>
      <nav class="app-nav">
        <a routerLink="/owner">Home</a>
        <a routerLink="/owner/staff">Staff</a>
        <a routerLink="/owner/shifts">Shifts</a>
        <a routerLink="/owner/menu">Menu</a>
        <a routerLink="/owner/tables">Tables</a>
        <a routerLink="/owner/report">Report</a>
      </nav>
      <button type="button" class="btn btn-ghost btn-sm" (click)="logout()">Logout</button>
    </header>
    <router-outlet />
  `,
})
export class OwnerShellComponent {
  private auth = inject(AuthService);

  logout() {
    this.auth.logout();
  }
}