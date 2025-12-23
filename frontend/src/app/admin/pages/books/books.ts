import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { CreateBookRequest } from '../../dto/create-book-request';

@Component({
  selector: 'app-admin-books',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './books.html',
  styleUrl: './books.scss',
})
export class AdminBooks {
  loading = false;
  errorMessage = '';
  successMessage = '';

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
    categoryId: new FormControl<number | null>(null, {
      validators: [Validators.required],
    }),
    authorIds: new FormControl<string>('', {
      nonNullable: true,
    }),
  });

  constructor(private readonly adminService: AdminService) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.form.getRawValue();
    
    // Parse authorIds from comma-separated string
    const authorIds = formValue.authorIds
      ? formValue.authorIds
          .split(',')
          .map((id) => parseInt(id.trim()))
          .filter((id) => !isNaN(id))
      : [];

    const payload: CreateBookRequest = {
      title: formValue.title,
      publicationYear: formValue.publicationYear!,
      isbn: formValue.isbn,
      categoryId: formValue.categoryId!,
      authorIds: authorIds.length > 0 ? authorIds : undefined,
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
}

