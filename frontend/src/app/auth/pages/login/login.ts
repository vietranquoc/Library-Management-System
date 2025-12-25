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
        // Lấy token từ response hoặc localStorage (đảm bảo đã được lưu)
        const token = res.data?.token || localStorage.getItem('access_token');
        if (token) {
          // Debug: log roles để kiểm tra
          const roles = JwtUtil.getRoles(token);
          console.log('User roles:', roles);
          
          if (JwtUtil.isAdmin(token)) {
            console.log('Redirecting to admin dashboard');
            this.router.navigateByUrl('/admin/dashboard');
          } else if (JwtUtil.isStaff(token)) {
            // Staff (không phải admin)
            console.log('Redirecting to staff dashboard');
            this.router.navigateByUrl('/staff/dashboard');
          } else {
            // Member
            console.log('Redirecting to home (member)');
            this.router.navigateByUrl('/home');
          }
        } else {
          console.error('No token found');
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
