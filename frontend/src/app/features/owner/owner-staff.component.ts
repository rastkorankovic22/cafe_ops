import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserApiService, UserDto } from '../../core/api/user-api.service';

@Component({
  selector: 'app-owner-staff',
  standalone: true,
  imports: [FormsModule],
  template: `
    <main class="page">
      <h1>Staff</h1>
      <div class="card">
        <ul class="list-plain">
          @for (u of users; track u.id) {
            <li>
              <span
                ><strong>{{ u.fullName }}</strong> — {{ u.email }}
                <span class="meta">({{ u.role }})</span></span
              >
              @if (u.role === 'WAITER') {
                <button type="button" class="btn btn-ghost btn-sm" (click)="toggle(u)">
                  {{ u.enabled ? 'Disable' : 'Enable' }}
                </button>
              }
            </li>
          }
        </ul>
      </div>
      <section class="section-gap card form-stack">
        <h2>New waiter</h2>
        <form (ngSubmit)="create()">
          <label class="field">
            Full name
            <input [(ngModel)]="fullName" name="fullName" required />
          </label>
          <label class="field">
            Email
            <input type="email" [(ngModel)]="email" name="email" required />
          </label>
          <label class="field">
            Password
            <input type="password" [(ngModel)]="password" name="password" required />
          </label>
          <button type="submit" class="btn btn-primary">Create</button>
        </form>
      </section>
    </main>
  `,
})
export class OwnerStaffComponent implements OnInit {
  private api = inject(UserApiService);

  users: UserDto[] = [];
  fullName = '';
  email = '';
  password = '';

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.api.list().subscribe((rows) => (this.users = rows));
  }

  create() {
    this.api.create(this.email, this.password, this.fullName).subscribe({
      next: () => {
        this.fullName = '';
        this.email = '';
        this.password = '';
        this.reload();
      },
    });
  }

  toggle(u: UserDto) {
    this.api.update(u.id, { enabled: !u.enabled }).subscribe({ next: () => this.reload() });
  }
}