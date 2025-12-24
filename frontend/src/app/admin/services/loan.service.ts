import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../auth/dto/api-response';

export interface LoanResponse {
  id: number;
  loanDate?: string; // null khi REQUESTED
  returnedDate?: string; // Backend trả về returnedDate
  dueDate?: string; // null khi REQUESTED
  status?: string; // REQUESTED, BORROWED, RETURNED, OVERDUE, CANCELLED
  book: {
    id: number;
    title: string;
    isbn: string;
  };
  member: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
  bookCopyBarCode?: string;
}

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  private readonly baseUrl = 'http://localhost:8080/api/loans';

  constructor(private http: HttpClient) {}

  getAllLoans(): Observable<ApiResponse<LoanResponse[]>> {
    return this.http.get<ApiResponse<LoanResponse[]>>(this.baseUrl);
  }

  getLoansByBookId(bookId: number): Observable<ApiResponse<LoanResponse[]>> {
    return this.http.get<ApiResponse<LoanResponse[]>>(
      `${this.baseUrl}/book/${bookId}`
    );
  }

  getMemberLoans(memberId: number): Observable<ApiResponse<LoanResponse[]>> {
    return this.http.get<ApiResponse<LoanResponse[]>>(
      `${this.baseUrl}/member/${memberId}`
    );
  }

  getMyLoans(): Observable<ApiResponse<LoanResponse[]>> {
    return this.http.get<ApiResponse<LoanResponse[]>>(
      `${this.baseUrl}/my-loans`
    );
  }
}

