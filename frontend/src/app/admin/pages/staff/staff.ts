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
import { StaffResponse } from '../../dto/staff-response';

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
  staffs: StaffResponse[] = [];

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

  ngOnInit(): void {
    this.loadStaffs();
  }

  private loadStaffs(): void {
    this.adminService.getStaffs().subscribe({
      next: (res) => {
        this.staffs = res.data || [];
      },
      error: () => {
        this.staffs = [];
      },
    });
  }

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
        this.loadStaffs();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Thêm nhân viên thất bại. Vui lòng thử lại.');
      },
    });
  }

  private getErrorMessage(err: any, fallback: string): string {
    return (
      err?.error?.message ||
      err?.error?.errors?.[0]?.defaultMessage ||
      err?.message ||
      fallback
    );
  }
}

