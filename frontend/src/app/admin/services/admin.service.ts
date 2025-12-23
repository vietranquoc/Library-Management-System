import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../auth/dto/api-response';
import { CreateCategoryRequest } from '../dto/create-category-request';
import { CreateBookRequest } from '../dto/create-book-request';
import { CreateStaffRequest } from '../dto/create-staff-request';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  createCategory(request: CreateCategoryRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.baseUrl}/categories`, request);
  }

  createBook(request: CreateBookRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/books`, request);
  }

  createStaff(request: CreateStaffRequest): Observable<ApiResponse<number>> {
    return this.http.post<ApiResponse<number>>(`${this.baseUrl}/staff`, request);
  }

  getCategories(): Observable<ApiResponse<Array<{ id: number; name: string }>>> {
    return this.http.get<ApiResponse<Array<{ id: number; name: string }>>>(
      `${this.baseUrl}/categories`,
    );
  }

  getAuthors(): Observable<ApiResponse<Array<{ id: number; name: string }>>> {
    return this.http.get<ApiResponse<Array<{ id: number; name: string }>>>(
      `${this.baseUrl}/authors`,
    );
  }
}

