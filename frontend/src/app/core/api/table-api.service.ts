import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TableDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class TableApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list(includeInactive = false): Observable<TableDto[]> {
    const q = includeInactive ? '?includeInactive=true' : '';
    return this.http.get<TableDto[]>(`${this.base}/api/v1/tables${q}`);
  }

  create(label: string, posX: number, posY: number): Observable<TableDto> {
    return this.http.post<TableDto>(`${this.base}/api/v1/tables`, { label, posX, posY });
  }

  update(
    id: number,
    body: { label?: string; posX?: number; posY?: number; active?: boolean }
  ): Observable<TableDto> {
    return this.http.patch<TableDto>(`${this.base}/api/v1/tables/${id}`, body);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/v1/tables/${id}`);
  }
}