import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { MemberService, MemberResponse } from '../../admin/services/member.service';
import { LoanService, LoanResponse } from '../../admin/services/loan.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit, OnDestroy {
  member: MemberResponse | null = null;
  loans: LoanResponse[] = [];
  loading = false;
  errorMessage = '';
  private routerSubscription?: Subscription;

  constructor(
    private readonly router: Router,
    private readonly memberService: MemberService,
    private readonly loanService: LoanService
  ) {}

  ngOnInit(): void {
    this.loadMemberData();
    
    // Tự động reload khi quay lại trang home từ các trang khác
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        if (event.url === '/home' || event.urlAfterRedirects === '/home') {
          this.loadMemberData();
        }
      });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
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
    // Loan đang mượn là loan chưa có returnedDate
    return this.loans.filter(loan => !loan.returnedDate);
  }

  getOverdueLoans(): LoanResponse[] {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return this.loans.filter(loan => {
      // Chỉ tính loan chưa trả
      if (loan.returnedDate) return false;
      const dueDate = new Date(loan.dueDate);
      dueDate.setHours(0, 0, 0, 0);
      return dueDate < today;
    });
  }

  getReturnedLoans(): LoanResponse[] {
    // Loan đã trả là loan có returnedDate
    return this.loans.filter(loan => !!loan.returnedDate);
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
  }

  getStatusLabel(loan: LoanResponse): string {
    if (loan.returnedDate) {
      return 'Đã trả';
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const dueDate = new Date(loan.dueDate);
    dueDate.setHours(0, 0, 0, 0);
    if (dueDate < today) {
      return 'Quá hạn';
    }
    return 'Đang mượn';
  }

  getStatusClass(loan: LoanResponse): string {
    if (loan.returnedDate) {
      return 'status-returned';
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const dueDate = new Date(loan.dueDate);
    dueDate.setHours(0, 0, 0, 0);
    if (dueDate < today) {
      return 'status-overdue';
    }
    return 'status-active';
  }

  onLogout(): void {
    localStorage.removeItem('access_token');
    this.router.navigateByUrl('/auth/login');
  }
}


