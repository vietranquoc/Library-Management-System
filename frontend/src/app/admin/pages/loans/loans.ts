import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LoanService } from '../../services/loan.service';
import { LoanResponse } from '../../services/loan.service';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-loans',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, AdminSidebar],
  templateUrl: './loans.html',
  styleUrl: './loans.scss',
})
export class AdminLoans implements OnInit {
  loans: LoanResponse[] = [];
  loading = false;
  errorMessage = '';

  searchForm = new FormGroup({
    searchType: new FormControl<'book' | 'member'>('book', {
      nonNullable: true,
    }),
    searchId: new FormControl<number | null>(null),
  });

  constructor(private readonly loanService: LoanService) {}

  ngOnInit(): void {}

  onSearch(): void {
    const searchId = this.searchForm.get('searchId')?.value;
    const searchType = this.searchForm.get('searchType')?.value;

    if (!searchId) {
      this.errorMessage = 'Vui lòng nhập ID để tìm kiếm';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request =
      searchType === 'book'
        ? this.loanService.getLoansByBookId(searchId)
        : this.loanService.getMemberLoans(searchId);

    request.subscribe({
      next: (res) => {
        this.loading = false;
        this.loans = res.data || [];
        if (this.loans.length === 0) {
          this.errorMessage = 'Không tìm thấy kết quả nào.';
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Không thể tải danh sách mượn trả.';
        this.loans = [];
      },
    });
  }
}

