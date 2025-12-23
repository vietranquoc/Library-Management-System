package com.ngv.libraryManagementSystem.service.admin;

import com.ngv.libraryManagementSystem.dto.request.admin.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.AuthorSimpleResponse;
import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.dto.response.CategorySimpleResponse;

import java.util.List;

public interface AdminService {

    void createCategory(CreateCategoryRequest request);

    BookResponse createBook(CreateBookRequest request);

    Long createStaff(CreateStaffRequest request);

    List<CategorySimpleResponse> getAllCategories();

    List<AuthorSimpleResponse> getAllAuthors();
}


