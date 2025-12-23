import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { CreateBookRequest } from '../../dto/create-book-request';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-books',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AdminSidebar],
  templateUrl: './books.html',
  styleUrl: './books.scss',
})
export class AdminBooks {
  loading = false;
  errorMessage = '';
  successMessage = '';

  categories: Array<{ id: number; name: string }> = [];


  form = new FormGroup({
    title: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    publicationYear: new FormControl<number | null>(null, {
      validators: [Validators.required, Validators.min(0)],
    }),
    isbn: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    quantity: new FormControl<number | null>(null, {
      validators: [Validators.required, Validators.min(0)],
    }),
    description: new FormControl<string>('', {
      nonNullable: true,
    }),
    image: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    categoryId: new FormControl<number | null>(null, {
      validators: [Validators.required],
    }),
    authorNames: new FormControl<string>('', {
      nonNullable: true,
    }),
  });

  constructor(
    private readonly adminService: AdminService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  private loadCategories(): void {
    this.adminService.getCategories().subscribe({
      next: (res) => {
        this.categories = res.data || [];
      },
      error: () => {
        // Không chặn form nếu load category fail
        this.categories = [];
      },
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.form.getRawValue();

    // authorNames nhập dạng chuỗi, phân tách bằng dấu phẩy
    const rawAuthorNames = formValue.authorNames ?? '';
    const authorNames =
      rawAuthorNames.length > 0
        ? rawAuthorNames
            .split(',')
            .map((name) => name.trim())
            .filter((name) => !!name)
        : [];

    const payload: CreateBookRequest = {
      title: formValue.title,
      publicationYear: formValue.publicationYear!,
      isbn: formValue.isbn,
      quantity: formValue.quantity ?? 0,
      description: formValue.description ?? '',
      image: formValue.image ?? '',
      categoryId: formValue.categoryId!,
      authorNames: authorNames.length > 0 ? authorNames : undefined,
    };

    this.adminService.createBook(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Thêm sách thành công';
        this.form.reset();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Thêm sách thất bại. Vui lòng thử lại.';
      },
    });
  }

  onCategoryChange(): void {
    const value = this.form.get('categoryId')?.value;
    if (value === -1) {
      // reset lại để tránh gửi -1 lên backend
      this.form.get('categoryId')?.setValue(null);
      this.router.navigate(['/admin/categories']);
    }
  }
}

