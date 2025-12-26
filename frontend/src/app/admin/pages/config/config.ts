import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { UpdateConfigRequest } from '../../dto/update-config-request';
import { ConfigResponse } from '../../dto/config-response';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-config',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AdminSidebar],
  templateUrl: './config.html',
  styleUrl: './config.scss',
})
export class AdminConfig implements OnInit {
  loading = false;
  loadingData = false;
  errorMessage = '';
  successMessage = '';
  config: ConfigResponse | null = null;

  form = new FormGroup({
    loanPeriodDays: new FormControl<number>(7, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
    finePerDay: new FormControl<number>(10000, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(0)],
    }),
    maxBooksPerMember: new FormControl<number>(5, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
  });

  constructor(private readonly adminService: AdminService) {}

  ngOnInit(): void {
    this.loadConfig();
  }

  private loadConfig(): void {
    this.loadingData = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.adminService.getConfig().subscribe({
      next: (res) => {
        this.loadingData = false;
        this.config = res.data || null;
        if (this.config) {
          this.form.patchValue({
            loanPeriodDays: this.config.loanPeriodDays,
            finePerDay: this.config.finePerDay,
            maxBooksPerMember: this.config.maxBooksPerMember,
          });
        }
      },
      error: (err) => {
        this.loadingData = false;
        this.errorMessage = this.getErrorMessage(err, 'Không thể tải cấu hình hệ thống.');
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
    const payload: UpdateConfigRequest = {
      loanPeriodDays: this.form.value.loanPeriodDays!,
      finePerDay: this.form.value.finePerDay!,
      maxBooksPerMember: this.form.value.maxBooksPerMember!,
    };

    this.adminService.updateConfig(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Cập nhật cấu hình thành công';
        this.config = res.data || null;
        // Reload để đảm bảo data đồng bộ
        setTimeout(() => {
          this.loadConfig();
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Cập nhật cấu hình thất bại. Vui lòng thử lại.');
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

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  }
}

