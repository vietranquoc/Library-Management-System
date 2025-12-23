package com.ngv.libraryManagementSystem.service.admin;

import com.ngv.libraryManagementSystem.dto.request.admin.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.BookResponse;

public interface AdminService {

    void createCategory(CreateCategoryRequest request);

    BookResponse createBook(CreateBookRequest request);

    Long createStaff(CreateStaffRequest request);
}


