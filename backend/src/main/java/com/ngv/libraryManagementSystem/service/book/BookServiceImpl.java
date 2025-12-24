package com.ngv.libraryManagementSystem.service.book;

import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.repository.BookRepository;
import com.ngv.libraryManagementSystem.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title, String authorName, String categoryName, Integer publicationYear) {
        List<BookEntity> books = bookRepository.searchBooks(title, authorName, categoryName, publicationYear);
        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return mapToBookResponse(book);
    }

    private BookResponse mapToBookResponse(BookEntity book) {
        int totalCopies = book.getQuantity() != null ? book.getQuantity() : 0;
        // Đếm số lượng sách đang được mượn (chưa trả)
        long activeLoans = loanRepository.countByBookIdAndReturnedDateIsNull(book.getId());
        // Số sách còn sẵn = tổng số - số đang mượn
        int availableCopies = Math.max(0, totalCopies - (int) activeLoans);
        
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .category(book.getCategory() != null ? 
                        BookResponse.CategoryInfo.builder()
                                .id(book.getCategory().getId())
                                .name(book.getCategory().getName())
                                .build() : null)
                .authors(book.getAuthors() != null ?
                        book.getAuthors().stream()
                                .map(author -> BookResponse.AuthorInfo.builder()
                                        .id(author.getId())
                                        .name(author.getName())
                                        .build())
                                .collect(Collectors.toSet()) : null)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}

