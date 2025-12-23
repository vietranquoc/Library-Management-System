import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtUtil } from '../../../shared/utils/jwt.util';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSidebar],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class AdminDashboard {
  constructor(private readonly router: Router) {}

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

