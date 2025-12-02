package com.ngv.libraryManagementSystem.service.book;

import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

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
        int totalCopies = book.getCopies() != null ? book.getCopies().size() : 0;
        int availableCopies = book.getCopies() != null ? 
                (int) book.getCopies().stream().filter(copy -> Boolean.TRUE.equals(copy.getAvailable())).count() : 0;

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
                .copies(book.getCopies() != null ? 
                        book.getCopies().stream()
                                .map(copy -> BookResponse.BookCopyInfo.builder()
                                        .id(copy.getId())
                                        .barCode(copy.getBarCode())
                                        .available(copy.getAvailable())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}

