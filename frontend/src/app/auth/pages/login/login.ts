import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../dto/login-request';
import { JwtUtil } from '../../../shared/utils/jwt.util';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  loading = false;
  errorMessage = '';
  successMessage = '';

  form = new FormGroup({
    username: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) { }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload: LoginRequest = this.form.getRawValue() as LoginRequest;

    this.authService.login(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Đăng nhập thành công';
        
        // Kiểm tra role và redirect
        const token = localStorage.getItem('access_token');
        if (token && JwtUtil.isAdmin(token)) {
          this.router.navigateByUrl('/admin/dashboard');
        } else {
          this.router.navigateByUrl('/home');
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
      },
    });
  }
}
