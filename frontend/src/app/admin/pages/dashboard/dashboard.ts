import { CommonModule } from '@angular/common';
import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtUtil } from '../../../shared/utils/jwt.util';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';
import { AdminService } from '../../services/admin.service';
import { DashboardStatistics } from '../../dto/dashboard-statistics';

declare var Chart: any;

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSidebar],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class AdminDashboard implements OnInit, AfterViewInit, OnDestroy {
  statistics: DashboardStatistics | null = null;
  loading = false;
  errorMessage = '';
  
  private loanChart: any = null;
  private categoryChart: any = null;

  constructor(
    private readonly router: Router,
    private readonly adminService: AdminService
  ) {}

  ngOnInit(): void {
    this.loadStatistics();
  }

  ngAfterViewInit(): void {
    // Charts sẽ được tạo sau khi data load xong
  }

  ngOnDestroy(): void {
    if (this.loanChart) {
      this.loanChart.destroy();
    }
    if (this.categoryChart) {
      this.categoryChart.destroy();
    }
  }

  loadStatistics(): void {
    this.loading = true;
    this.errorMessage = '';

    this.adminService.getDashboardStatistics().subscribe({
      next: (res) => {
        this.loading = false;
        this.statistics = res.data || null;
        if (this.statistics) {
          setTimeout(() => {
            this.createCharts();
          }, 100);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Không thể tải thống kê dashboard.';
      },
    });
  }

  createCharts(): void {
    if (!this.statistics || typeof Chart === 'undefined') {
      return;
    }

    // Biểu đồ xu hướng mượn sách theo tháng
    this.createLoanTrendChart();
    
    // Biểu đồ phân bổ sách theo thể loại
    this.createCategoryChart();
  }

  createLoanTrendChart(): void {
    const ctx = document.getElementById('loanTrendChart') as HTMLCanvasElement;
    if (!ctx || !this.statistics) return;

    if (this.loanChart) {
      this.loanChart.destroy();
    }

    const months = this.statistics.monthlyLoanData.map(d => d.month);
    const borrowedData = this.statistics.monthlyLoanData.map(d => d.borrowedCount);
    const returnedData = this.statistics.monthlyLoanData.map(d => d.returnedCount);

    this.loanChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: months,
        datasets: [
          {
            label: 'Số sách mượn',
            data: borrowedData,
            borderColor: 'rgb(59, 130, 246)',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            tension: 0.4,
            fill: true,
          },
          {
            label: 'Số sách trả',
            data: returnedData,
            borderColor: 'rgb(34, 197, 94)',
            backgroundColor: 'rgba(34, 197, 94, 0.1)',
            tension: 0.4,
            fill: true,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
          },
          title: {
            display: true,
            text: 'Xu hướng mượn sách theo tháng',
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1,
            },
          },
        },
      },
    });
  }

  createCategoryChart(): void {
    const ctx = document.getElementById('categoryChart') as HTMLCanvasElement;
    if (!ctx || !this.statistics) return;

    if (this.categoryChart) {
      this.categoryChart.destroy();
    }

    const categories = this.statistics.categoryDistribution.map(d => d.categoryName);
    const counts = this.statistics.categoryDistribution.map(d => d.bookCount);

    // Màu sắc cho các phần
    const colors = [
      'rgba(59, 130, 246, 0.8)',
      'rgba(34, 197, 94, 0.8)',
      'rgba(251, 191, 36, 0.8)',
      'rgba(239, 68, 68, 0.8)',
      'rgba(168, 85, 247, 0.8)',
      'rgba(236, 72, 153, 0.8)',
      'rgba(20, 184, 166, 0.8)',
      'rgba(249, 115, 22, 0.8)',
    ];

    this.categoryChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: categories,
        datasets: [
          {
            data: counts,
            backgroundColor: colors.slice(0, categories.length),
            borderWidth: 2,
            borderColor: '#ffffff',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
          },
          title: {
            display: true,
            text: 'Phân bổ sách theo thể loại',
          },
        },
      },
    });
  }

  formatActivityTime(timestamp: string): string {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Vừa xong';
    if (diffMins < 60) return `${diffMins} phút trước`;
    if (diffHours < 24) return `${diffHours} giờ trước`;
    if (diffDays < 7) return `${diffDays} ngày trước`;
    return date.toLocaleDateString('vi-VN');
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'LOAN_REQUEST':
        return '📋';
      case 'LOAN_BORROWED':
        return '📖';
      case 'LOAN_RETURNED':
        return '✅';
      case 'LOAN_OVERDUE':
        return '⚠️';
      default:
        return '📚';
    }
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
