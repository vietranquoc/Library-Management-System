import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtUtil } from '../../../shared/utils/jwt.util';
import { StaffSidebar } from '../../../shared/components/staff-sidebar/staff-sidebar';
import { LoanService } from '../../../admin/services/loan.service';
import { LoanResponse } from '../../../admin/services/loan.service';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, StaffSidebar],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class StaffDashboard implements OnInit {
  requestedLoans: LoanResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private readonly router: Router,
    private readonly loanService: LoanService
  ) {}

  ngOnInit(): void {
    this.loadRequestedLoans();
  }

  loadRequestedLoans(): void {
    this.loading = true;
    this.errorMessage = '';

    this.loanService.getRequestedLoans().subscribe({
      next: (res) => {
        this.loading = false;
        this.requestedLoans = res.data || [];
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Không thể tải danh sách phiếu mượn chờ xử lý.';
      },
    });
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

