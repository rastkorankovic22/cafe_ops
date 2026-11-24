import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MenuCategoryDto, MenuItemDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class MenuApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list(includeInactive = false): Observable<MenuCategoryDto[]> {
    const q = includeInactive ? '?includeInactive=true' : '';
    return this.http.get<MenuCategoryDto[]>(`${this.base}/api/v1/menu${q}`);
  }

  createCategory(name: string, sortOrder?: number): Observable<MenuCategoryDto> {
    return this.http.post<MenuCategoryDto>(`${this.base}/api/v1/menu/categories`, {
      name,
      sortOrder,
    });
  }

  updateCategory(
    id: number,
    body: { name?: string; sortOrder?: number; active?: boolean }
  ): Observable<MenuCategoryDto> {
    return this.http.patch<MenuCategoryDto>(`${this.base}/api/v1/menu/categories/${id}`, body);
  }

  createItem(categoryId: number, name: string, price: number): Observable<MenuItemDto> {
    return this.http.post<MenuItemDto>(`${this.base}/api/v1/menu/items`, { categoryId, name, price });
  }

  updateItem(
    id: number,
    body: { name?: string; price?: number; active?: boolean; categoryId?: number }
  ): Observable<MenuItemDto> {
    return this.http.patch<MenuItemDto>(`${this.base}/api/v1/menu/items/${id}`, body);
  }
}