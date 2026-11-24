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
      @if (shiftError) {
        <p class="warn">No open shift — ask the owner to start one.</p>
      } @else if (shiftName) {
        <p class="meta">Shift: {{ shiftName }}</p>
      }

      @if (tables.length === 0) {
        <p>No tables.</p>
      } @else {
        <ul class="grid">
          @for (t of tables; track t.id) {
            <li>
              <a [routerLink]="['/waiter', 'table', t.id]" [class.occupied]="t.occupied">
                {{ t.label }}
                <small>{{ t.occupied ? 'Busy' : 'Free' }}</small>
              </a>
            </li>
          }
        </ul>
      }
    </main>
  `,
  styles: [
    `
      .page {
        padding: 1rem;
      }
      .warn {
        color: #b00020;
      }
      .meta {
        margin-bottom: 1rem;
      }
      .grid {
        list-style: none;
        padding: 0;
        margin: 0;
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 0.75rem;
      }
      a {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 1rem;
        border: 1px solid #ccc;
        border-radius: 8px;
        text-decoration: none;
        color: inherit;
      }
      a.occupied {
        border-color: #f59e0b;
        background: #fff8eb;
      }
      small {
        color: #555;
      }
    `,
  ],
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