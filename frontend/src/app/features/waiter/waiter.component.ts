import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-waiter',
  standalone: true,
  template: `
    <header>
      <h1>Waiter</h1>
      <p>{{ user?.fullName }}</p>
      <button type="button" (click)="logout()">Logout</button>
    </header>
    <p>Table board and orders — next step.</p>
  `,
})
export class WaiterComponent {
  private auth = inject(AuthService);
  user = this.auth.getUser();

  logout() {
    this.auth.logout();
  }
}