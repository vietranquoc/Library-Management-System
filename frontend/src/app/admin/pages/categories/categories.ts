import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { CreateCategoryRequest } from '../../dto/create-category-request';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AdminSidebar],
  templateUrl: './categories.html',
  styleUrl: './categories.scss',
})
export class AdminCategories implements OnInit {
  loading = false;
  errorMessage = '';
  successMessage = '';

  form = new FormGroup({
    name: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    description: new FormControl<string>('', {
      nonNullable: true,
    }),
  });

  constructor(private readonly adminService: AdminService) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload: CreateCategoryRequest = this.form.getRawValue();

    this.adminService.createCategory(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Thêm thể loại thành công';
        this.form.reset();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Thêm thể loại thất bại. Vui lòng thử lại.';
      },
    });
  }
}

