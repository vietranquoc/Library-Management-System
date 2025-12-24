import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../auth/dto/api-response';
import { BookResponse } from '../../admin/dto/book-response';

export interface LoanRequest {
  bookCopyId: number;
}

export interface ReservationRequest {
  bookId: number;
}

export interface ReservationResponse {
  id: number;
  reservationDate: string;
  notified: boolean;
  book?: {
    id: number;
    title: string;
    isbn: string;
  };
  member?: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  searchBooks(params?: {
    title?: string;
    authorName?: string;
    categoryName?: string;
    publicationYear?: number;
  }): Observable<ApiResponse<BookResponse[]>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          httpParams = httpParams.set(key, String(value));
        }
      });
    }
    return this.http.get<ApiResponse<BookResponse[]>>(
      `${this.baseUrl}/books/search`,
      { params: httpParams }
    );
  }

  getBookById(id: number): Observable<ApiResponse<BookResponse>> {
    return this.http.get<ApiResponse<BookResponse>>(
      `${this.baseUrl}/books/${id}`
    );
  }

  borrowBook(request: LoanRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      `${this.baseUrl}/loans/borrow`,
      request
    );
  }

  createReservation(request: ReservationRequest): Observable<ApiResponse<ReservationResponse>> {
    return this.http.post<ApiResponse<ReservationResponse>>(
      `${this.baseUrl}/reservations`,
      request
    );
  }

  getMyReservations(): Observable<ApiResponse<ReservationResponse[]>> {
    return this.http.get<ApiResponse<ReservationResponse[]>>(
      `${this.baseUrl}/reservations/my-reservations`
    );
  }

  cancelReservation(reservationId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.baseUrl}/reservations/${reservationId}`
    );
  }

  getCategories(): Observable<ApiResponse<Array<{ id: number; name: string; description?: string }>>> {
    return this.http.get<ApiResponse<Array<{ id: number; name: string; description?: string }>>>(
      `${this.baseUrl}/books/categories`
    );
  }
}

