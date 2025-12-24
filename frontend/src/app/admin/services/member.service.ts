import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../auth/dto/api-response';

export interface MemberResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  joinDate: string;
  status: string;
}

@Injectable({
  providedIn: 'root',
})
export class MemberService {
  private readonly baseUrl = 'http://localhost:8080/api/members';

  constructor(private http: HttpClient) {}

  getAllMembers(): Observable<ApiResponse<MemberResponse[]>> {
    return this.http.get<ApiResponse<MemberResponse[]>>(`${this.baseUrl}`);
  }

  getMemberById(id: number): Observable<ApiResponse<MemberResponse>> {
    return this.http.get<ApiResponse<MemberResponse>>(`${this.baseUrl}/${id}`);
  }

  getMyProfile(): Observable<ApiResponse<MemberResponse>> {
    return this.http.get<ApiResponse<MemberResponse>>(`${this.baseUrl}/profile`);
  }
}

