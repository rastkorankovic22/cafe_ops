import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MenuApiService } from '../../core/api/menu-api.service';
import { OrderApiService } from '../../core/api/order-api.service';
import { MenuCategoryDto, MenuItemDto, OrderDto } from '../../core/api/api.models';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-waiter-order',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe],
  template: `
    <main class="page">
      <a routerLink="/waiter">← Back</a>
      <h1>Table {{ tableId }}</h1>

      @if (error) {
        <p class="warn">{{ error }}</p>
      }

      @if (!order) {
        <button type="button" (click)="startOrder()">Start order</button>
      } @else {
        <ul class="lines">
          @for (line of order.lines; track line.id) {
            <li>
              <span>{{ line.menuItemName }} × {{ line.quantity }}</span>
              <span>{{ line.unitPrice * line.quantity | number: '1.2-2' }}</span>
              <button type="button" (click)="changeQty(line.id, line.quantity + 1)">+</button>
              <button type="button" (click)="removeLine(line.id)" [disabled]="order.lines.length === 1">×</button>
            </li>
          }
        </ul>

        <div class="add">
          <select [(ngModel)]="selectedItemId">
            <option [ngValue]="null">Select drink</option>
            @for (item of menuItems; track item.id) {
              <option [ngValue]="item.id">{{ item.name }} ({{ item.price }})</option>
            }
          </select>
          <input type="number" min="1" [(ngModel)]="qty" />
          <button type="button" (click)="addLine()" [disabled]="!selectedItemId">Add</button>
        </div>

        <p class="total">Total: {{ orderTotal | number: '1.2-2' }}</p>
        <button type="button" class="close" (click)="closeOrder()">Close order</button>
      }
    </main>
  `,
  styles: [
    `
      .page {
        padding: 1rem;
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
      }
      .warn {
        color: #b00020;
      }
      .lines {
        list-style: none;
        padding: 0;
        margin: 0;
      }
      .lines li {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        align-items: center;
        padding: 0.5rem 0;
        border-bottom: 1px solid #eee;
      }
      .add {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        align-items: center;
      }
      select,
      input {
        padding: 0.35rem;
      }
      .close {
        padding: 0.75rem;
      }
    `,
  ],
})
export class WaiterOrderComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private orders = inject(OrderApiService);
  private menu = inject(MenuApiService);

  tableId = 0;
  order: OrderDto | null = null;
  menuItems: MenuItemDto[] = [];
  selectedItemId: number | null = null;
  qty = 1;
  error = '';

  ngOnInit() {
    this.tableId = Number(this.route.snapshot.paramMap.get('tableId'));
    this.menu.list().subscribe({
      next: (cats) => {
        this.menuItems = cats.flatMap((c: MenuCategoryDto) => c.items);
      },
    });
    this.loadOrder();
  }

  get orderTotal(): number {
    if (!this.order) {
      return 0;
    }
    return this.order.lines.reduce((sum, l) => sum + l.unitPrice * l.quantity, 0);
  }

  startOrder() {
    this.error = '';
    this.orders.open(this.tableId).subscribe({
      next: (o) => (this.order = o),
      error: (e) => (this.error = this.msg(e)),
    });
  }

  loadOrder() {
    this.error = '';
    this.orders.currentForTable(this.tableId).subscribe({
      next: (o) => (this.order = o),
      error: () => (this.order = null),
    });
  }

  addLine() {
    if (!this.order || !this.selectedItemId) {
      return;
    }
    this.orders.addLine(this.order.id, this.selectedItemId, this.qty).subscribe({
      next: (o) => {
        this.order = o;
        this.qty = 1;
      },
      error: (e) => (this.error = this.msg(e)),
    });
  }

  changeQty(lineId: number, quantity: number) {
    if (!this.order) {
      return;
    }
    this.orders.updateLine(this.order.id, lineId, quantity).subscribe({
      next: (o) => (this.order = o),
    });
  }

  removeLine(lineId: number) {
    if (!this.order) {
      return;
    }
    this.orders.removeLine(this.order.id, lineId).subscribe({
      next: (o) => (this.order = o),
    });
  }

  closeOrder() {
    if (!this.order) {
      return;
    }
    this.orders.close(this.order.id).subscribe({
      next: () => (this.order = null),
      error: (e) => (this.error = this.msg(e)),
    });
  }

  private msg(err: unknown): string {
    return err && typeof err === 'object' && 'status' in err && (err as { status: number }).status === 409
      ? 'No open shift or table busy'
      : 'Request failed';
  }
}