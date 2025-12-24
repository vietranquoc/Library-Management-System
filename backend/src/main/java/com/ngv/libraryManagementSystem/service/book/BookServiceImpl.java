package com.ngv.libraryManagementSystem.service.book;

import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.entity.BookCopyEntity;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.enums.BookCopyStatusEnum;
import com.ngv.libraryManagementSystem.repository.BookCopyRepository;
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
    private final BookCopyRepository bookCopyRepository;

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
        // Lấy danh sách BookCopy của sách này
        List<BookCopyEntity> copies = bookCopyRepository.findByBook(book);
        int totalCopies = copies.size();
        
        // Đếm số BookCopy có status AVAILABLE
        long availableCount = copies.stream()
                .filter(copy -> copy.getStatus() == BookCopyStatusEnum.AVAILABLE)
                .count();
        int availableCopies = (int) availableCount;
        
        // Map BookCopyInfo
        List<BookResponse.BookCopyInfo> copyInfos = copies.stream()
                .map(copy -> BookResponse.BookCopyInfo.builder()
                        .id(copy.getId())
                        .barCode(copy.getBarCode())
                        .available(copy.getStatus() == BookCopyStatusEnum.AVAILABLE)
                        .build())
                .collect(Collectors.toList());
        
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .image(book.getImage())
                .description(book.getDescription())
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
                .copies(copyInfos)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}

