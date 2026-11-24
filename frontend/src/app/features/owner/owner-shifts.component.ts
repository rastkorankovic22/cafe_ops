import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ShiftApiService } from '../../core/api/shift-api.service';
import { ShiftDto } from '../../core/api/api.models';
import { UserApiService, UserDto } from '../../core/api/user-api.service';

@Component({
  selector: 'app-owner-shifts',
  standalone: true,
  imports: [FormsModule],
  template: `
    <main class="page page-narrow">
      <h1>Shifts</h1>
      <div class="card">
        @if (current) {
          <p>Active: <strong>{{ current.waiterFullName }}</strong></p>
          <p class="meta">Started {{ current.startedAt }}</p>
          <button type="button" class="btn btn-primary" (click)="end()">End shift</button>
        } @else {
          <p class="meta">No open shift.</p>
          <label class="field">
            Waiter
            <select [(ngModel)]="waiterId" name="waiterId">
              <option [ngValue]="null">Select</option>
              @for (w of waiters; track w.id) {
                <option [ngValue]="w.id">{{ w.fullName }}</option>
              }
            </select>
          </label>
          <button type="button" class="btn btn-primary" (click)="start()" [disabled]="!waiterId">Start shift</button>
        }
      </div>
      <section class="section-gap">
        <h2>Recent</h2>
        <ul class="list-plain">
          @for (s of history; track s.id) {
            <li>{{ s.waiterFullName }} — {{ s.status }} — {{ s.startedAt }}</li>
          }
        </ul>
      </section>
    </main>
  `,
})
export class OwnerShiftsComponent implements OnInit {
  private shifts = inject(ShiftApiService);
  private users = inject(UserApiService);

  current: ShiftDto | null = null;
  history: ShiftDto[] = [];
  waiters: UserDto[] = [];
  waiterId: number | null = null;

  ngOnInit() {
    this.users.list().subscribe((rows) => {
      this.waiters = rows.filter((u) => u.role === 'WAITER' && u.enabled);
    });
    this.refresh();
  }

  refresh() {
    this.shifts.current().subscribe({
      next: (s) => (this.current = s),
      error: () => (this.current = null),
    });
    this.shifts.history().subscribe({
      next: (page) => (this.history = page.content ?? []),
      error: () => (this.history = []),
    });
  }

  start() {
    if (!this.waiterId) {
      return;
    }
    this.shifts.start(this.waiterId).subscribe({ next: () => this.refresh() });
  }

  end() {
    if (!this.current) {
      return;
    }
    this.shifts.end(this.current.id).subscribe({ next: () => this.refresh() });
  }
}