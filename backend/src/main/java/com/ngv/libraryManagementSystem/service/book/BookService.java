package com.ngv.libraryManagementSystem.service.book;

import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import java.util.List;

public interface BookService {
    List<BookResponse> searchBooks(String title, String authorName, String categoryName, Integer publicationYear);
    BookResponse getBookById(Long id);
}

