import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { BookService, LoanRequest, ReservationRequest } from '../services/book.service';
import { BookResponse } from '../../admin/dto/book-response';

@Component({
  selector: 'app-search-books',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './search-books.html',
  styleUrl: './search-books.scss',
})
export class SearchBooks implements OnInit {
  loading = false;
  errorMessage = '';
  successMessage = '';
  books: BookResponse[] = [];
  categories: Array<{ id: number; name: string }> = [];
  selectedBook: BookResponse | null = null;
  showBookDetail = false;

  searchForm = new FormGroup({
    title: new FormControl<string>(''),
    authorName: new FormControl<string>(''),
    categoryId: new FormControl<number | null>(null),
    publicationYear: new FormControl<number | null>(null),
  });

  constructor(
    private readonly bookService: BookService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.searchBooks();
  }

  loadCategories(): void {
    this.bookService.getCategories().subscribe({
      next: (res) => {
        this.categories = res.data || [];
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        this.categories = [];
      },
    });
  }

  searchBooks(): void {
    this.loading = true;
    this.errorMessage = '';
    const formValue = this.searchForm.getRawValue();

    const params: {
      title?: string;
      authorName?: string;
      categoryName?: string;
      publicationYear?: number;
    } = {};

    if (formValue.title?.trim()) {
      params.title = formValue.title.trim();
    }
    if (formValue.authorName?.trim()) {
      params.authorName = formValue.authorName.trim();
    }
    // Xử lý categoryId - có thể là string hoặc number từ select
    const categoryIdValue = formValue.categoryId;
    if (categoryIdValue !== null && categoryIdValue !== undefined) {
      // Convert sang number nếu là string
      const categoryId = typeof categoryIdValue === 'string' 
        ? (categoryIdValue === '' ? null : Number(categoryIdValue))
        : categoryIdValue;
      
      if (categoryId !== null && categoryId !== undefined && !isNaN(categoryId) && categoryId > 0) {
        // Tìm category name từ id
        const selectedCategory = this.categories.find(cat => cat.id === categoryId);
        if (selectedCategory) {
          params.categoryName = selectedCategory.name;
        } else {
          console.warn('Category not found for id:', categoryId, 'Available categories:', this.categories);
        }
      }
    }
    if (formValue.publicationYear) {
      params.publicationYear = formValue.publicationYear;
    }

    console.log('Search params:', params);

    this.bookService.searchBooks(params).subscribe({
      next: (res) => {
        this.loading = false;
        this.books = res.data || [];
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Không thể tìm kiếm sách. Vui lòng thử lại.';
        this.books = [];
      },
    });
  }

  onSearch(): void {
    this.searchBooks();
  }

  onReset(): void {
    this.searchForm.reset();
    this.searchBooks();
  }

  viewBookDetail(book: BookResponse): void {
    this.selectedBook = book;
    this.showBookDetail = true;
  }

  closeBookDetail(): void {
    this.showBookDetail = false;
    this.selectedBook = null;
  }

  borrowBook(book: BookResponse): void {
    if (!confirm(`Bạn có chắc muốn mượn sách "${book.title}"?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: LoanRequest = {
      bookCopyId: book.id, // Trong code backend, bookCopyId được dùng như bookId
    };

    this.bookService.borrowBook(request).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Mượn sách thành công!';
        this.closeBookDetail();
        // Refresh danh sách để cập nhật availableCopies
        setTimeout(() => this.searchBooks(), 1000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message ||
          'Không thể mượn sách. Vui lòng thử lại.';
      },
    });
  }

  reserveBook(book: BookResponse): void {
    if (!confirm(`Bạn có chắc muốn đặt chỗ sách "${book.title}"?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: ReservationRequest = {
      bookId: book.id,
    };

    this.bookService.createReservation(request).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Đặt chỗ sách thành công!';
        this.closeBookDetail();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message ||
          'Không thể đặt chỗ sách. Vui lòng thử lại.';
      },
    });
  }

  formatAuthors(book: BookResponse): string {
    if (!book.authors || book.authors.length === 0) {
      return '-';
    }
    return book.authors.map((a) => a.name).join(', ');
  }

  canBorrow(book: BookResponse): boolean {
    return (book.availableCopies ?? 0) > 0;
  }

  canReserve(book: BookResponse): boolean {
    return (book.availableCopies ?? 0) === 0;
  }

  onLogout(): void {
    localStorage.removeItem('access_token');
    this.router.navigateByUrl('/auth/login');
  }
}

