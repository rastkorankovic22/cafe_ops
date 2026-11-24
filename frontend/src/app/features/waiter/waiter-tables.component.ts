import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ShiftApiService } from '../../core/api/shift-api.service';
import { TableApiService } from '../../core/api/table-api.service';
import { TableDto } from '../../core/api/api.models';

@Component({
  selector: 'app-waiter-tables',
  standalone: true,
  imports: [RouterLink],
  template: `
    <main class="page">
      <h1>Tables</h1>
      @if (shiftError) {
        <p class="alert-warn">No open shift — ask the owner to start one.</p>
      } @else if (shiftName) {
        <p class="meta">On shift: <strong>{{ shiftName }}</strong></p>
      }

      @if (tables.length === 0) {
        <p class="meta">No tables.</p>
      } @else {
        <div class="floor-map wide">
          @for (t of tables; track t.id) {
            <a
              class="floor-dot link-dot"
              [class.occupied]="t.occupied"
              [style.left.px]="t.posX"
              [style.top.px]="t.posY"
              [routerLink]="['/waiter', 'table', t.id]"
            >
              {{ t.label }}
              <small>{{ t.occupied ? 'Busy' : 'Free' }}</small>
            </a>
          }
        </div>
        <p class="meta">Layout matches owner floor plan. Tap a table to open the order.</p>
      }
    </main>
  `,
})
export class WaiterTablesComponent implements OnInit {
  private tablesApi = inject(TableApiService);
  private shiftApi = inject(ShiftApiService);

  tables: TableDto[] = [];
  shiftName = '';
  shiftError = false;

  ngOnInit() {
    this.shiftApi.current().subscribe({
      next: (s) => {
        this.shiftName = s.waiterFullName;
        this.loadTables();
      },
      error: () => {
        this.shiftError = true;
        this.loadTables();
      },
    });
  }

  private loadTables() {
    this.tablesApi.list().subscribe({
      next: (rows) => (this.tables = rows),
    });
  }
}