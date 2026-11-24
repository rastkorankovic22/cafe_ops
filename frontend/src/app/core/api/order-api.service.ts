import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrderDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class OrderApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  open(tableId: number): Observable<OrderDto> {
    return this.http.post<OrderDto>(`${this.base}/api/v1/tables/${tableId}/orders`, {});
  }

  currentForTable(tableId: number): Observable<OrderDto> {
    return this.http.get<OrderDto>(`${this.base}/api/v1/tables/${tableId}/orders/current`);
  }

  addLine(orderId: number, menuItemId: number, quantity: number): Observable<OrderDto> {
    return this.http.post<OrderDto>(`${this.base}/api/v1/orders/${orderId}/lines`, {
      menuItemId,
      quantity,
    });
  }

  updateLine(orderId: number, lineId: number, quantity: number): Observable<OrderDto> {
    return this.http.patch<OrderDto>(`${this.base}/api/v1/orders/${orderId}/lines/${lineId}`, {
      quantity,
    });
  }

  removeLine(orderId: number, lineId: number): Observable<OrderDto> {
    return this.http.delete<OrderDto>(`${this.base}/api/v1/orders/${orderId}/lines/${lineId}`);
  }

  close(orderId: number): Observable<OrderDto> {
    return this.http.post<OrderDto>(`${this.base}/api/v1/orders/${orderId}/close`, {});
  }
}