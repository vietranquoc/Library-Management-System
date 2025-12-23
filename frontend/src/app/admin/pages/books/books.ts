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
import { BookResponse } from '../../dto/book-response';

@Component({
  selector: 'app-admin-books',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AdminSidebar],
  templateUrl: './books.html',
  styleUrl: './books.scss',
})
export class AdminBooks {
  loading = false;
  loadingList = false;
  errorMessage = '';
  successMessage = '';
  imageError = '';

  categories: Array<{ id: number; name: string }> = [];
  books: BookResponse[] = [];
  filteredBooks: BookResponse[] = [];
  searchTerm = '';


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

  imagePreview: string | null = null;
  imageFileName: string | null = null;

  constructor(
    private readonly adminService: AdminService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadBooks();
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

  private loadBooks(): void {
    this.loadingList = true;
    this.adminService.getBooks().subscribe({
      next: (res) => {
        this.loadingList = false;
        this.books = res.data || [];
        this.applyFilter();
      },
      error: (err) => {
        this.loadingList = false;
        this.errorMessage = this.getErrorMessage(err, 'Không thể tải danh sách sách.');
        this.books = [];
        this.filteredBooks = [];
      },
    });
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.applyFilter();
  }

  formatAuthors(book: BookResponse): string {
    if (!book.authors || book.authors.length === 0) {
      return '-';
    }
    return book.authors.map((a) => a.name).join(', ');
  }

  private applyFilter(): void {
    const keyword = this.searchTerm.trim().toLowerCase();
    if (!keyword) {
      this.filteredBooks = [...this.books];
      return;
    }
    this.filteredBooks = this.books.filter((b) =>
      [b.title, b.isbn, b.category?.name]
        .filter(Boolean)
        .some((field) => field!.toLowerCase().includes(keyword)),
    );
  }

  get totalBooks(): number {
    return this.books.length;
  }

  get availableSum(): number {
    return this.books.reduce((sum, b) => sum + (b.availableCopies ?? 0), 0);
  }

  get borrowedSum(): number {
    return this.books.reduce(
      (sum, b) => sum + Math.max((b.totalCopies ?? 0) - (b.availableCopies ?? 0), 0),
      0,
    );
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.imageError = '';

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
        this.imagePreview = null;
        this.imageFileName = null;
        this.loadBooks();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Thêm sách thất bại. Vui lòng thử lại.');
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

  private getErrorMessage(err: any, fallback: string): string {
    return (
      err?.error?.message ||
      err?.error?.errors?.[0]?.defaultMessage ||
      err?.message ||
      fallback
    );
  }

  onImageSelected(event: Event): void {
    this.imageError = '';
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    const file = input.files[0];

    // Giới hạn kích thước (ví dụ 5MB)
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      this.imageError = 'Ảnh bìa vượt quá 5MB. Vui lòng chọn ảnh nhỏ hơn.';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      this.form.get('image')?.setValue(result);
      this.imagePreview = result;
      this.imageFileName = file.name;
    };
    reader.onerror = () => {
      this.imageError = 'Không thể đọc file ảnh. Vui lòng thử lại.';
    };

    reader.readAsDataURL(file);
  }
}

