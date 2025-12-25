import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JwtUtil } from '../../../shared/utils/jwt.util';
import { StaffSidebar } from '../../../shared/components/staff-sidebar/staff-sidebar';
import { LoanService } from '../../../admin/services/loan.service';
import { LoanResponse } from '../../../admin/services/loan.service';
import { BookService } from '../../../pages/services/book.service';
import { BookResponse } from '../../../admin/dto/book-response';

@Component({
  selector: 'app-staff-loan-requests',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, StaffSidebar],
  templateUrl: './loan-requests.html',
  styleUrl: './loan-requests.scss',
})
export class StaffLoanRequests implements OnInit {
  loans: LoanResponse[] = [];
  selectedLoan: LoanResponse | null = null;
  barCode = '';
  availableCopies: Array<{ id: number; barCode: string; available: boolean }> = [];
  loading = false;
  assigning = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly loanService: LoanService,
    private readonly bookService: BookService
  ) {}

  ngOnInit(): void {
    this.loadRequestedLoans();
  }

  loadRequestedLoans(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.loanService.getRequestedLoans().subscribe({
      next: (res) => {
        this.loading = false;
        this.loans = res.data || [];
        
        // Nếu có loanId trong route, tự động chọn sau khi đã có danh sách loans
        const loanId = this.route.snapshot.params['loanId'];
        if (loanId) {
          const loan = this.loans.find(l => l.id === Number(loanId));
          if (loan) {
            this.selectLoan(loan);
          }
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Không thể tải danh sách phiếu mượn chờ xử lý.';
      },
    });
  }

  selectLoan(loan: LoanResponse | number): void {
    if (typeof loan === 'number') {
      const foundLoan = this.loans.find(l => l.id === loan);
      if (foundLoan) {
        this.selectedLoan = foundLoan;
        this.barCode = '';
        this.errorMessage = '';
        this.successMessage = '';
        this.loadAvailableCopies(foundLoan.book.id);
      }
    } else {
      this.selectedLoan = loan;
      this.barCode = '';
      this.errorMessage = '';
      this.successMessage = '';
      this.loadAvailableCopies(loan.book.id);
    }
  }

  private loadAvailableCopies(bookId: number): void {
    this.availableCopies = [];
    this.bookService.getBookById(bookId).subscribe({
      next: (res) => {
        const book: BookResponse | undefined = res.data;
        if (book && book.copies) {
          this.availableCopies = book.copies.filter((c) => c.available);
        } else {
          this.availableCopies = [];
        }
      },
      error: () => {
        this.availableCopies = [];
      },
    });
  }

  assignBookCopy(): void {
    if (!this.selectedLoan) {
      this.errorMessage = 'Vui lòng chọn phiếu mượn';
      return;
    }

    if (!this.barCode.trim()) {
      this.errorMessage = 'Vui lòng nhập mã vạch của bản sao sách';
      return;
    }

    this.assigning = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.loanService.assignBookCopy(this.selectedLoan.id, this.barCode.trim()).subscribe({
      next: (res) => {
        this.assigning = false;
        this.successMessage = 'Gán bản sao sách thành công!';
        this.barCode = '';
        // Reload danh sách sau 1 giây
        setTimeout(() => {
          this.loadRequestedLoans();
          this.selectedLoan = null;
        }, 1500);
      },
      error: (err) => {
        this.assigning = false;
        this.errorMessage = err?.error?.message || 'Không thể gán bản sao sách. Vui lòng thử lại.';
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

