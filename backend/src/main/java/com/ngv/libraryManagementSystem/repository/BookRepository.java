package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    boolean existsByIsbn(String isbn);

    @Query("SELECT DISTINCT b FROM BookEntity b " +
           "LEFT JOIN b.authors a " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:authorName IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))) " +
           "AND (:categoryName IS NULL OR LOWER(b.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) " +
           "AND (:publicationYear IS NULL OR b.publicationYear = :publicationYear)")
    List<BookEntity> searchBooks(
            @Param("title") String title,
            @Param("authorName") String authorName,
            @Param("categoryName") String categoryName,
            @Param("publicationYear") Integer publicationYear
    );
}

