import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtUtil } from '../../../shared/utils/jwt.util';
import { StaffSidebar } from '../../../shared/components/staff-sidebar/staff-sidebar';
import { LoanService, LoanResponse } from '../../../admin/services/loan.service';

@Component({
  selector: 'app-staff-loans',
  standalone: true,
  imports: [CommonModule, RouterLink, StaffSidebar],
  templateUrl: './loans.html',
  styleUrl: './loans.scss',
})
export class StaffLoans implements OnInit {
  loans: LoanResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private readonly router: Router,
    private readonly loanService: LoanService
  ) {}

  ngOnInit(): void {
    this.loadAllLoans();
  }

  loadAllLoans(): void {
    this.loading = true;
    this.errorMessage = '';

    this.loanService.getAllLoans().subscribe({
      next: (res) => {
        this.loading = false;
        this.loans = res.data || [];
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Không thể tải danh sách mượn trả.';
        this.loans = [];
      },
    });
  }

  getStatusLabel(status?: string): string {
    if (!status) return 'N/A';
    const statusMap: { [key: string]: string } = {
      REQUESTED: 'Đang chờ',
      APPROVED: 'Đã duyệt',
      BORROWED: 'Đang mượn',
      RETURNED: 'Đã trả',
      OVERDUE: 'Quá hạn',
      CANCELLED: 'Đã hủy',
    };
    return statusMap[status] || status;
  }

  getStatusClass(status?: string): string {
    if (!status) return 'status-default';
    const statusLower = status.toLowerCase();
    return `status-${statusLower}`;
  }

  onLogout(): void {
    localStorage.removeItem('access_token');
    this.router.navigateByUrl('/auth/login');
  }

  getUsername(): string {
    const token = localStorage.getItem('access_token');
    if (!token) return '';
    const decoded = JwtUtil.decodeToken(token);
    return decoded?.sub || '';
  }
}

