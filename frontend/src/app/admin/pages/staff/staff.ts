import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { CreateStaffRequest } from '../../dto/create-staff-request';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-staff',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AdminSidebar],
  templateUrl: './staff.html',
  styleUrl: './staff.scss',
})
export class AdminStaff {
  loading = false;
  errorMessage = '';
  successMessage = '';

  form = new FormGroup({
    username: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(3)],
    }),
    password: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6)],
    }),
    firstName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    lastName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    phone: new FormControl<string>('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/^0(3|5|7|8|9)[0-9]{8}$/),
      ],
    }),
  });

  constructor(private readonly adminService: AdminService) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload: CreateStaffRequest = this.form.getRawValue();

    this.adminService.createStaff(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Thêm nhân viên thành công';
        this.form.reset();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Thêm nhân viên thất bại. Vui lòng thử lại.';
      },
    });
  }
}

