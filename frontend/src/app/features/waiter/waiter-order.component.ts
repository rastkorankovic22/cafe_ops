import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MenuApiService } from '../../core/api/menu-api.service';
import { OrderApiService } from '../../core/api/order-api.service';
import { MenuCategoryDto, MenuItemDto, OrderDto } from '../../core/api/api.models';

@Component({
  selector: 'app-waiter-order',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe],
  template: `
    <main class="page">
      <a class="back-link" routerLink="/waiter">← Floor plan</a>
      <h1>Table {{ tableId }}</h1>

      @if (error) {
        <p class="alert-error">{{ error }}</p>
      }

      @if (!order) {
        <div class="card">
          <p class="meta">No open order on this table.</p>
          <button type="button" class="btn btn-primary" (click)="startOrder()">Start order</button>
        </div>
      } @else {
        <div class="card">
          <ul class="order-lines">
            @for (line of order.lines; track line.id) {
              <li>
                <span>{{ line.menuItemName }} × {{ line.quantity }}</span>
                <span class="price">{{ line.unitPrice * line.quantity | number: '1.2-2' }}</span>
                <button type="button" class="btn btn-ghost btn-sm" (click)="removeLine(line.id)">Remove</button>
              </li>
            }
          </ul>
          <p class="total">Total: {{ orderTotal | number: '1.2-2' }}</p>

          <div class="category-chips">
            @for (cat of categories; track cat.id) {
              <button
                type="button"
                [class.active]="activeCategoryId === cat.id"
                (click)="activeCategoryId = cat.id; pickedItem = null"
              >
                {{ cat.name }}
              </button>
            }
          </div>

          @if (activeCategory) {
            <div class="drink-grid">
              @for (item of activeCategory.items; track item.id) {
                <button
                  type="button"
                  class="drink-card"
                  [class.selected]="pickedItem?.id === item.id"
                  (click)="pickItem(item)"
                >
                  <img [src]="itemImage(item)" [alt]="item.name" />
                  <span class="name">{{ item.name }}</span>
                  <span class="price">{{ item.price | number: '1.2-2' }}</span>
                </button>
              }
            </div>
          }

          @if (pickedItem) {
            <div class="qty-panel">
              <p>
                <strong>{{ pickedItem.name }}</strong>
              </p>
              <div class="row">
                <label class="field">
                  Quantity
                  <input type="number" min="1" [(ngModel)]="qty" />
                </label>
                <button type="button" class="btn btn-primary" (click)="addLine()">Add to order</button>
              </div>
            </div>
          }

          <button type="button" class="btn btn-primary" (click)="closeOrder()">Close order</button>
        </div>
      }
    </main>
  `,
})
export class WaiterOrderComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private orders = inject(OrderApiService);
  private menu = inject(MenuApiService);

  tableId = 0;
  order: OrderDto | null = null;
  categories: MenuCategoryDto[] = [];
  activeCategoryId: number | null = null;
  pickedItem: MenuItemDto | null = null;
  qty = 1;
  error = '';

  ngOnInit() {
    this.tableId = Number(this.route.snapshot.paramMap.get('tableId'));
    this.menu.list().subscribe({
      next: (cats) => {
        this.categories = cats;
        if (cats.length) {
          this.activeCategoryId = cats[0].id;
        }
      },
    });
    this.loadOrder();
  }

  get activeCategory(): MenuCategoryDto | undefined {
    return this.categories.find((c) => c.id === this.activeCategoryId);
  }

  itemImage(item: MenuItemDto): string {
    return item.imageUrl || '/drinks/default.svg';
  }

  get orderTotal(): number {
    if (!this.order) {
      return 0;
    }
    return this.order.lines.reduce((sum, l) => sum + l.unitPrice * l.quantity, 0);
  }

  pickItem(item: MenuItemDto) {
    this.pickedItem = item;
    this.qty = 1;
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
    if (!this.order || !this.pickedItem) {
      return;
    }
    this.orders.addLine(this.order.id, this.pickedItem.id, this.qty).subscribe({
      next: (o) => {
        this.order = o;
        this.pickedItem = null;
        this.qty = 1;
      },
      error: (e) => (this.error = this.msg(e)),
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