package com.bookmanagement.service.impl;

import java.time.LocalDate;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.*;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.BookMapper;
import com.bookmanagement.repository.*;
import com.bookmanagement.service.BookService;
import com.bookmanagement.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl  implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookDTO> getAllBooks(
            Integer page, Integer size, Long authorId, Long categoryId,
            Double ratingMin, Double ratingMax, LocalDate publishedStart,
            LocalDate publishedEnd, String sortBy) {
        
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(sortBy != null ? sortBy : "id")
        );
        
        Specification<Book> spec = Specification.where(null);
        
        if (authorId != null) {
            spec = spec.and(BookSpecification.hasAuthorId(authorId));
        }
        if (categoryId != null) {
            spec = spec.and(BookSpecification.hasCategoryId(categoryId));
        }
        if (ratingMin != null) {
            spec = spec.and(BookSpecification.hasRatingGreaterThanOrEqual(ratingMin));
        }
        if (ratingMax != null) {
            spec = spec.and(BookSpecification.hasRatingLessThanOrEqual(ratingMax));
        }
        if (publishedStart != null) {
            spec = spec.and(BookSpecification.publishedAfter(publishedStart));
        }
        if (publishedEnd != null) {
            spec = spec.and(BookSpecification.publishedBefore(publishedEnd));
        }
        
        return bookRepository.findAll(spec, pageable).map(bookMapper::toDTO);
    }

    @Override
     public BookDetailDTO getBookById(Long id) {
        Book book = bookRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return bookMapper.toDetailDTO(book);
    }

    @Override
    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }
        
        Author author = authorRepository.findById(bookDTO.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDTO.getAuthorId()));
        
        Set<Category> categories = bookDTO.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)))
                .collect(Collectors.toSet());
        
        if (categories.isEmpty()) {
            throw new InvalidOperationException("Book must have at least one category");
        }
        
        Book book = bookMapper.toEntity(bookDTO);
        book.setAuthor(author);
        book.setCategories(categories);
        book.setRating(0.0);
        
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().equals(book.getIsbn())) {
            if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
                throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
            }
            book.setIsbn(bookDTO.getIsbn());
        }
        
        if (bookDTO.getTitle() != null) {
            book.setTitle(bookDTO.getTitle());
        }
        
        if (bookDTO.getPublishedDate() != null) {
            book.setPublishedDate(bookDTO.getPublishedDate());
        }
        
        if (bookDTO.getAuthorId() != null) {
            Author author = authorRepository.findById(bookDTO.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDTO.getAuthorId()));
            book.setAuthor(author);
        }
        
        if (bookDTO.getCategoryIds() != null && !bookDTO.getCategoryIds().isEmpty()) {
            Set<Category> categories = bookDTO.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId)))
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }
        
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toDTO(updatedBook);
    }

    @Override
     @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
    
}
