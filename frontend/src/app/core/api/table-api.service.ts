import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TableDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class TableApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list(): Observable<TableDto[]> {
    return this.http.get<TableDto[]>(`${this.base}/api/v1/tables`);
  }
}