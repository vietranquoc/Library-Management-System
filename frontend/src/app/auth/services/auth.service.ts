import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../dto/login-request';
import { RegisterRequest } from '../dto/register-request';
import { ApiResponse } from '../dto/api-response';
import { AuthResponse } from '../dto/auth-response';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(payload: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, payload)
      .pipe(
        tap((res) => {
          if (res.data?.token) {
            localStorage.setItem('access_token', res.data.token);
          }
        }),
      );
  }

  register(payload: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(
      `${this.baseUrl}/register`,
      payload,
    );
  }
}


