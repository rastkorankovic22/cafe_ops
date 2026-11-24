import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UserDto {
  id: number;
  email: string;
  role: string;
  fullName: string;
  enabled: boolean;
}

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.base}/api/v1/users`);
  }

  create(email: string, password: string, fullName: string): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.base}/api/v1/users`, { email, password, fullName });
  }

  update(id: number, body: { enabled?: boolean; password?: string }): Observable<UserDto> {
    return this.http.patch<UserDto>(`${this.base}/api/v1/users/${id}`, body);
  }
}