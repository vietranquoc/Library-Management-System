package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.dto.response.CategorySimpleResponse;
import com.ngv.libraryManagementSystem.service.admin.AdminService;
import com.ngv.libraryManagementSystem.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AdminService adminService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Integer publicationYear
    ) {
        List<BookResponse> books = bookService.searchBooks(title, authorName, categoryName, publicationYear);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tìm kiếm thành công", books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin sách thành công", book));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getCategories() {
        List<CategorySimpleResponse> categories = adminService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thể loại thành công", categories));
    }
}

