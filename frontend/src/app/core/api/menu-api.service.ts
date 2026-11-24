import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MenuCategoryDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class MenuApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list(): Observable<MenuCategoryDto[]> {
    return this.http.get<MenuCategoryDto[]>(`${this.base}/api/v1/menu`);
  }
}