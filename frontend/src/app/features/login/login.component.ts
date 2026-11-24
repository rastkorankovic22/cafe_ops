import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <main class="login-hero">
      <p class="logo">Cafe Ops</p>
      <p class="tagline">Lumen Cafe — shift &amp; floor</p>
      <div class="card login-card">
        <form [formGroup]="form" (ngSubmit)="submit()">
          <label class="field">
            Email
            <input type="email" formControlName="email" autocomplete="username" />
          </label>
          <label class="field">
            Password
            <input type="password" formControlName="password" autocomplete="current-password" />
          </label>
          @if (error) {
            <p class="alert-error">{{ error }}</p>
          }
          <button type="submit" class="btn btn-primary" [disabled]="form.invalid || loading">
            {{ loading ? 'Signing in…' : 'Sign in' }}
          </button>
        </form>
      </div>
    </main>
  `,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = false;
  error = '';

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  constructor() {
    if (this.auth.isLoggedIn()) {
      this.router.navigateByUrl(this.auth.homeRoute());
    }
  }

  submit() {
    if (this.form.invalid) {
      return;
    }
    this.loading = true;
    this.error = '';
    const { email, password } = this.form.getRawValue();
    this.auth.login(email!, password!).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.auth.homeRoute());
      },
      error: () => {
        this.loading = false;
        this.error = 'Invalid email or password';
      },
    });
  }
}