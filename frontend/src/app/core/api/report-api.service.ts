import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DailyReportDto {
  date: string;
  closedOrders: number;
  revenue: number;
  topItems: { menuItemId: number; name: string; quantitySold: number }[];
}

@Injectable({ providedIn: 'root' })
export class ReportApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  daily(date?: string): Observable<DailyReportDto> {
    const q = date ? `?date=${date}` : '';
    return this.http.get<DailyReportDto>(`${this.base}/api/v1/reports/daily${q}`);
  }
}