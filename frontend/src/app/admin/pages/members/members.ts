import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MemberService } from '../../services/member.service';
import { MemberResponse } from '../../services/member.service';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-members',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSidebar],
  templateUrl: './members.html',
  styleUrl: './members.scss',
})
export class AdminMembers implements OnInit {
  members: MemberResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(private readonly memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  loadMembers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.memberService.getAllMembers().subscribe({
      next: (res) => {
        this.loading = false;
        this.members = res.data || [];
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Không thể tải danh sách thành viên.';
      },
    });
  }
}

