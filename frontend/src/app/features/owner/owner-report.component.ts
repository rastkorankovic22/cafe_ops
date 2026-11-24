import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DailyReportDto, ReportApiService } from '../../core/api/report-api.service';

@Component({
  selector: 'app-owner-report',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  template: `
    <main class="page page-narrow">
      <h1>Daily report</h1>
      <div class="card">
        <label class="field">
          Date
          <input type="date" [(ngModel)]="date" name="date" (change)="load()" />
        </label>
        @if (report) {
          <p>Closed orders: <strong>{{ report.closedOrders }}</strong></p>
          <p>Revenue: <strong>{{ report.revenue | number: '1.2-2' }}</strong></p>
          <h2>Top drinks</h2>
          <ul class="list-plain">
            @for (t of report.topItems; track t.menuItemId) {
              <li>{{ t.name }} — {{ t.quantitySold }} sold</li>
            }
          </ul>
        }
      </div>
    </main>
  `,
})
export class OwnerReportComponent implements OnInit {
  private api = inject(ReportApiService);

  date = '';
  report: DailyReportDto | null = null;

  ngOnInit() {
    this.load();
  }

  load() {
    this.api.daily(this.date || undefined).subscribe({
      next: (r) => {
        this.report = r;
        if (!this.date && r.date) {
          this.date = r.date;
        }
      },
    });
  }
}