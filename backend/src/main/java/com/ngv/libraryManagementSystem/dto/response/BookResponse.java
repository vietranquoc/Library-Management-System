package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String image;
    private String description;
    private CategoryInfo category;
    private Set<AuthorInfo> authors;
    private List<BookCopyInfo> copies;
    private Integer availableCopies;
    private Integer totalCopies;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCopyInfo {
        private Long id;
        private String barCode;
        private Boolean available;
    }
}

