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

  constructor(private readonly loanService: LoanService) {}

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
        this.errorMessage =
          err?.error?.message || 'Không thể tải danh sách mượn trả.';
        this.loans = [];
      },
    });
  }
}

