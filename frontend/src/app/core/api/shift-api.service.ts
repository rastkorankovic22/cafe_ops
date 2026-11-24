import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ShiftDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class ShiftApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  current(): Observable<ShiftDto> {
    return this.http.get<ShiftDto>(`${this.base}/api/v1/shifts/current`);
  }

  start(waiterId: number): Observable<ShiftDto> {
    return this.http.post<ShiftDto>(`${this.base}/api/v1/shifts/start`, { waiterId });
  }

  end(shiftId: number): Observable<ShiftDto> {
    return this.http.post<ShiftDto>(`${this.base}/api/v1/shifts/${shiftId}/end`, {});
  }

  history(): Observable<{ content: ShiftDto[] }> {
    return this.http.get<{ content: ShiftDto[] }>(`${this.base}/api/v1/shifts`);
  }
}