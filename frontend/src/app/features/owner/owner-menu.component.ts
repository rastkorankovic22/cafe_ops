import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MenuApiService } from '../../core/api/menu-api.service';
import { MenuCategoryDto } from '../../core/api/api.models';

@Component({
  selector: 'app-owner-menu',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  template: `
    <main class="page">
      <h1>Menu</h1>
      @for (cat of categories; track cat.id) {
        <section class="card cat-block">
          <h2>
            {{ cat.name }}
            <button type="button" class="btn btn-ghost btn-sm" (click)="toggleCat(cat)">
              {{ cat.active ? 'Hide' : 'Show' }}
            </button>
          </h2>
          <ul>
            @for (item of cat.items; track item.id) {
              <li>
                {{ item.name }} — {{ item.price | number: '1.2-2' }}
                <button type="button" class="btn btn-ghost btn-sm" (click)="toggleItem(item.id, !item.active)">
                  {{ item.active ? 'Off' : 'On' }}
                </button>
              </li>
            }
          </ul>
        </section>
      }
      <section class="card form-stack section-gap">
        <h2>New category</h2>
        <form (ngSubmit)="addCategory()">
          <input [(ngModel)]="newCat" name="newCat" placeholder="Name" required />
          <button type="submit" class="btn btn-primary">Add category</button>
        </form>
      </section>
      <section class="card form-stack section-gap">
        <h2>New drink</h2>
        <form (ngSubmit)="addItem()">
          <select [(ngModel)]="itemCatId" name="itemCatId" required>
            <option [ngValue]="null">Category</option>
            @for (cat of categories; track cat.id) {
              <option [ngValue]="cat.id">{{ cat.name }}</option>
            }
          </select>
          <input [(ngModel)]="itemName" name="itemName" placeholder="Name" required />
          <input [(ngModel)]="itemPrice" name="itemPrice" type="number" step="0.01" required />
          <button type="submit" class="btn btn-primary">Add drink</button>
        </form>
      </section>
    </main>
  `,
})
export class OwnerMenuComponent implements OnInit {
  private api = inject(MenuApiService);

  categories: MenuCategoryDto[] = [];
  newCat = '';
  itemCatId: number | null = null;
  itemName = '';
  itemPrice = 0;

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.api.list(true).subscribe((rows) => (this.categories = rows));
  }

  addCategory() {
    this.api.createCategory(this.newCat).subscribe({
      next: () => {
        this.newCat = '';
        this.reload();
      },
    });
  }

  addItem() {
    if (!this.itemCatId) {
      return;
    }
    this.api.createItem(this.itemCatId, this.itemName, this.itemPrice).subscribe({
      next: () => {
        this.itemName = '';
        this.itemPrice = 0;
        this.reload();
      },
    });
  }

  toggleCat(cat: MenuCategoryDto) {
    this.api.updateCategory(cat.id, { active: !cat.active }).subscribe({ next: () => this.reload() });
  }

  toggleItem(id: number, active: boolean) {
    this.api.updateItem(id, { active }).subscribe({ next: () => this.reload() });
  }
}