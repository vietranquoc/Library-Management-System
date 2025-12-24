import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MemberService, MemberResponse } from '../../admin/services/member.service';
import { LoanService, LoanResponse } from '../../admin/services/loan.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  member: MemberResponse | null = null;
  loans: LoanResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private readonly router: Router,
    private readonly memberService: MemberService,
    private readonly loanService: LoanService
  ) {}

  ngOnInit(): void {
    this.loadMemberData();
  }

  loadMemberData(): void {
    this.loading = true;
    this.errorMessage = '';

    // Load member profile
    this.memberService.getMyProfile().subscribe({
      next: (res) => {
        this.member = res.data || null;
        this.loadMyLoans();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Không thể tải thông tin thành viên.';
      },
    });
  }

  loadMyLoans(): void {
    this.loanService.getMyLoans().subscribe({
      next: (res) => {
        this.loading = false;
        this.loans = res.data || [];
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Không thể tải danh sách mượn sách.';
      },
    });
  }

  getActiveLoans(): LoanResponse[] {
    return this.loans.filter(loan => loan.status === 'BORROWED' || loan.status === 'ACTIVE');
  }

  getOverdueLoans(): LoanResponse[] {
    const today = new Date();
    return this.loans.filter(loan => {
      if (loan.status !== 'BORROWED' && loan.status !== 'ACTIVE') return false;
      const dueDate = new Date(loan.dueDate);
      return dueDate < today;
    });
  }

  getReturnedLoans(): LoanResponse[] {
    return this.loans.filter(loan => loan.status === 'RETURNED');
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'BORROWED': 'Đang mượn',
      'ACTIVE': 'Đang mượn',
      'RETURNED': 'Đã trả',
      'OVERDUE': 'Quá hạn',
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const classMap: { [key: string]: string } = {
      'BORROWED': 'status-active',
      'ACTIVE': 'status-active',
      'RETURNED': 'status-returned',
      'OVERDUE': 'status-overdue',
    };
    return classMap[status] || '';
  }

  onLogout(): void {
    localStorage.removeItem('access_token');
    this.router.navigateByUrl('/auth/login');
  }
}


