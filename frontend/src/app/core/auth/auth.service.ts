import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginResponse, SessionUser, UserRole } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'cafe_ops_token';
  private readonly userKey = 'cafe_ops_user';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(email: string, password: string) {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/api/v1/auth/login`, { email, password })
      .pipe(
        tap((res) => {
          sessionStorage.setItem(this.tokenKey, res.accessToken);
          const user: SessionUser = {
            userId: res.userId,
            email: res.email,
            role: res.role,
            fullName: res.fullName,
          };
          sessionStorage.setItem(this.userKey, JSON.stringify(user));
        })
      );
  }

  logout() {
    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.userKey);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }

  getUser(): SessionUser | null {
    const raw = sessionStorage.getItem(this.userKey);
    return raw ? (JSON.parse(raw) as SessionUser) : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  hasRole(role: UserRole): boolean {
    return this.getUser()?.role === role;
  }

  homeRoute(): string {
    return this.getUser()?.role === 'OWNER' ? '/owner' : '/waiter';
  }
}