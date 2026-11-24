import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TableApiService } from '../../core/api/table-api.service';
import { TableDto } from '../../core/api/api.models';

@Component({
  selector: 'app-owner-tables',
  standalone: true,
  imports: [FormsModule],
  template: `
    <main class="page">
      <h1>Floor plan</h1>
      <p class="meta">Drag tables with the mouse. Orange = busy.</p>
      <div class="floor-map wide" #mapEl>
        @for (t of tables; track t.id) {
          <button
            type="button"
            class="floor-dot"
            [class.occupied]="t.occupied"
            [class.dragging]="dragId === t.id"
            [style.left.px]="displayPos(t).x"
            [style.top.px]="displayPos(t).y"
            (pointerdown)="onDragStart($event, t)"
            (click)="select(t)"
          >
            {{ t.label }}
          </button>
        }
      </div>
      @if (selected) {
        <div class="card form-stack">
          <h2>Edit {{ selected.label }}</h2>
          <form (ngSubmit)="save()">
            <label class="field">
              Label
              <input [(ngModel)]="label" name="label" />
            </label>
            <button type="submit" class="btn btn-primary">Save label</button>
            <button type="button" class="btn btn-ghost" (click)="remove()">Remove</button>
          </form>
        </div>
      }
      <section class="card form-stack section-gap">
        <h2>Add table</h2>
        <form (ngSubmit)="add()">
          <input [(ngModel)]="newLabel" name="newLabel" placeholder="Label" required />
          <button type="submit" class="btn btn-primary">Add at center</button>
        </form>
      </section>
    </main>
  `,
})
export class OwnerTablesComponent implements OnInit {
  private api = inject(TableApiService);

  @ViewChild('mapEl') mapEl?: ElementRef<HTMLElement>;

  tables: TableDto[] = [];
  selected: TableDto | null = null;
  label = '';
  newLabel = '';
  dragId: number | null = null;
  dragPos = { x: 0, y: 0 };
  private dragMoved = false;

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.api.list(true).subscribe((rows) => (this.tables = rows.filter((t) => t.active)));
  }

  displayPos(t: TableDto) {
    if (this.dragId === t.id) {
      return this.dragPos;
    }
    return { x: t.posX, y: t.posY };
  }

  select(t: TableDto) {
    if (this.dragMoved) {
      return;
    }
    this.selected = t;
    this.label = t.label;
  }

  onDragStart(event: PointerEvent, t: TableDto) {
    if (!this.mapEl) {
      return;
    }
    event.preventDefault();
    this.dragId = t.id;
    this.dragMoved = false;
    this.dragPos = { x: t.posX, y: t.posY };
    const el = event.target as HTMLElement;
    el.setPointerCapture(event.pointerId);

    const onMove = (e: PointerEvent) => {
      if (e.pointerId !== event.pointerId) {
        return;
      }
      const rect = this.mapEl!.nativeElement.getBoundingClientRect();
      const x = Math.max(0, Math.min(rect.width - 56, e.clientX - rect.left - 26));
      const y = Math.max(0, Math.min(rect.height - 40, e.clientY - rect.top - 20));
      this.dragPos = { x: Math.round(x), y: Math.round(y) };
      this.dragMoved = true;
    };

    const onUp = (e: PointerEvent) => {
      if (e.pointerId !== event.pointerId) {
        return;
      }
      el.releasePointerCapture(event.pointerId);
      const id = this.dragId;
      const pos = { ...this.dragPos };
      this.dragId = null;
      window.removeEventListener('pointermove', onMove);
      window.removeEventListener('pointerup', onUp);
      if (id == null) {
        return;
      }
      if (this.dragMoved) {
        this.api.update(id, { posX: pos.x, posY: pos.y }).subscribe({ next: () => this.reload() });
      }
      setTimeout(() => (this.dragMoved = false), 0);
    };

    window.addEventListener('pointermove', onMove);
    window.addEventListener('pointerup', onUp);
  }

  save() {
    if (!this.selected) {
      return;
    }
    this.api.update(this.selected.id, { label: this.label }).subscribe({ next: () => this.reload() });
  }

  remove() {
    if (!this.selected) {
      return;
    }
    this.api.remove(this.selected.id).subscribe({
      next: () => {
        this.selected = null;
        this.reload();
      },
    });
  }

  add() {
    this.api.create(this.newLabel, 200, 200).subscribe({
      next: () => {
        this.newLabel = '';
        this.reload();
      },
    });
  }
}