import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../auth/dto/api-response';
import { CreateCategoryRequest } from '../dto/create-category-request';
import { CreateBookRequest } from '../dto/create-book-request';
import { CreateStaffRequest } from '../dto/create-staff-request';
import { BookResponse } from '../dto/book-response';
import { HttpParams } from '@angular/common/http';
import { StaffResponse } from '../dto/staff-response';
import { ConfigResponse } from '../dto/config-response';
import { UpdateConfigRequest } from '../dto/update-config-request';

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

  getCategories(): Observable<ApiResponse<Array<{ id: number; name: string; description?: string }>>> {
    return this.http.get<ApiResponse<Array<{ id: number; name: string; description?: string }>>>(
      `${this.baseUrl}/categories`,
    );
  }

  getAuthors(): Observable<ApiResponse<Array<{ id: number; name: string }>>> {
    return this.http.get<ApiResponse<Array<{ id: number; name: string }>>>(
      `${this.baseUrl}/authors`,
    );
  }

  getStaffs(): Observable<ApiResponse<StaffResponse[]>> {
    return this.http.get<ApiResponse<StaffResponse[]>>(`${this.baseUrl}/staff`);
  }

  getBooks(params?: {
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
    // API public /api/books/search
    return this.http.get<ApiResponse<BookResponse[]>>(
      `http://localhost:8080/api/books/search`,
      { params: httpParams },
    );
  }

  getDashboardStatistics(): Observable<ApiResponse<import('../dto/dashboard-statistics').DashboardStatistics>> {
    return this.http.get<ApiResponse<import('../dto/dashboard-statistics').DashboardStatistics>>(
      `${this.baseUrl}/dashboard/statistics`
    );
  }

  getConfig(): Observable<ApiResponse<ConfigResponse>> {
    return this.http.get<ApiResponse<ConfigResponse>>(`${this.baseUrl}/config`);
  }

  updateConfig(request: UpdateConfigRequest): Observable<ApiResponse<ConfigResponse>> {
    return this.http.put<ApiResponse<ConfigResponse>>(`${this.baseUrl}/config`, request);
  }
}

