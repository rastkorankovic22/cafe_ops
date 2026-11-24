import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-owner',
  standalone: true,
  template: `
    <header>
      <h1>Owner</h1>
      <p>{{ user?.fullName }}</p>
      <button type="button" (click)="logout()">Logout</button>
    </header>
    <p>Dashboard, menu, tables, shifts — next step.</p>
  `,
})
export class OwnerComponent {
  private auth = inject(AuthService);
  user = this.auth.getUser();

  logout() {
    this.auth.logout();
  }
}