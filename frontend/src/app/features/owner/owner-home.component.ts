import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ReportApiService } from '../../core/api/report-api.service';
import { ShiftApiService } from '../../core/api/shift-api.service';

@Component({
  selector: 'app-owner-home',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <main class="page">
      <h1>Dashboard</h1>
      @if (shiftLabel) {
        <p class="meta">Open shift: <strong>{{ shiftLabel }}</strong></p>
      } @else {
        <p class="alert-warn">No open shift — open one under Shifts.</p>
      }
      @if (report) {
        <section class="card stat-card">
          <h2>Today</h2>
          <p>Closed orders: <span class="stat-value">{{ report.closedOrders }}</span></p>
          <p>Revenue: <span class="stat-value">{{ report.revenue | number: '1.2-2' }}</span></p>
        </section>
      }
    </main>
  `,
})
export class OwnerHomeComponent implements OnInit {
  private reportApi = inject(ReportApiService);
  private shiftApi = inject(ShiftApiService);

  shiftLabel = '';
  report: { closedOrders: number; revenue: number } | null = null;

  ngOnInit() {
    this.shiftApi.current().subscribe({
      next: (s) => (this.shiftLabel = s.waiterFullName),
      error: () => (this.shiftLabel = ''),
    });
    this.reportApi.daily().subscribe({
      next: (r) => (this.report = r),
    });
  }
}