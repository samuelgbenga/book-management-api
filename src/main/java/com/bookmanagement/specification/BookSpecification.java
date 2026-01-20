package com.bookmanagement.specification;

import com.bookmanagement.entity.Book;
import com.bookmanagement.entity.Category;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class BookSpecification {
    
    public static Specification<Book> hasAuthorId(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }
    
    public static Specification<Book> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            Join<Book, Category> categories = root.join("categories");
            return cb.equal(categories.get("id"), categoryId);
        };
    }
    
    public static Specification<Book> hasRatingGreaterThanOrEqual(Double rating) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("rating"), rating);
    }
    
    public static Specification<Book> hasRatingLessThanOrEqual(Double rating) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("rating"), rating);
    }
    
    public static Specification<Book> publishedAfter(LocalDate date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("publishedDate"), date);
    }
    
    public static Specification<Book> publishedBefore(LocalDate date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("publishedDate"), date);
    }
}