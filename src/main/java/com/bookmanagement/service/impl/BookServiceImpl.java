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
    public Page<BookPaginationDTO> getAllBooks(GetAllBookParamsDTO params) {
        
        Pageable pageable = PageRequest.of(
                params.page() != null ? params.page() : 0,
                params.size() != null ? params.size() : 20,
                createSort(params.sortBy())
        );
        
        Specification<Book> spec = Specification.where(null);

        if (params.authorId() != null) {
            spec = spec.and(BookSpecification.hasAuthorId(params.authorId()));
        }
        if (params.categoryId() != null) {
            spec = spec.and(BookSpecification.hasCategoryId(params.categoryId()));
        }
        if (params.ratingMin() != null) {
            spec = spec.and(BookSpecification.hasRatingGreaterThanOrEqual(params.ratingMin()));
        }
        if (params.ratingMax() != null) {
            spec = spec.and(BookSpecification.hasRatingLessThanOrEqual(params.ratingMax()));
        }
        if (params.publishedStart() != null) {
            spec = spec.and(BookSpecification.publishedAfter(params.publishedStart()));
        }
        if (params.publishedEnd() != null) {
            spec = spec.and(BookSpecification.publishedBefore(params.publishedEnd()));
        }
        
        return bookRepository.findAll(spec, pageable).map(bookMapper::toPaginationDTO);
    }


        private Sort createSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id"); // default sort
        }
        
        // Handle format: "title,desc" or "title,asc" or just "title"
        String[] parts = sortBy.split(",");
        String property = parts[0].trim();
        
        // Validate the property exists in Book entity
        if (!isValidSortProperty(property)) {
            throw new IllegalArgumentException("Invalid sort property: " + property);
        }
        
        Sort.Direction direction = Sort.Direction.ASC; // default
        if (parts.length > 1) {
            String directionStr = parts[1].trim().toUpperCase();
            if (directionStr.equals("DESC")) {
                direction = Sort.Direction.DESC;
            }
        }
        
        return Sort.by(direction, property);
    }

    /**
     * Validates if the property is a valid sortable field in Book entity
     */
    private boolean isValidSortProperty(String property) {
        Set<String> validProperties = Set.of(
            "id", "title", "isbn", "publishedDate", "createdAt", "updatedAt"
        );
        return validProperties.contains(property);
    }

    @Override
     public BookDetailDTO getBookById(Long id) {
        Book book = bookRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return bookMapper.toDetailDTO(book);
    }

    @Override
    @Transactional
    public BookDTO createBook(NewBookDTO bookDTO) {
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
    public BookDetailDTO updateBook(Long id, NewBookDTO bookDTO) {
        Book book = findBookById(id);
        
        updateIsbnIfChanged(book, bookDTO.getIsbn());
        updateBasicFields(book, bookDTO);
        updateAuthorIfProvided(book, bookDTO.getAuthorId());
        updateCategoriesIfProvided(book, bookDTO.getCategoryIds());
        
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toDetailDTO(updatedBook);
    }

    /**
     * Finds book by ID or throws exception
     */
    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    /**
     * Updates ISBN if changed and validates uniqueness
     */
    private void updateIsbnIfChanged(Book book, String newIsbn) {
        if (newIsbn == null || newIsbn.equals(book.getIsbn())) {
            return;
        }
        
        if (bookRepository.findByIsbn(newIsbn).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + newIsbn + " already exists");
        }
        
        book.setIsbn(newIsbn);
    }

    /**
     * Updates basic book fields (title, publishedDate)
     */
    private void updateBasicFields(Book book, NewBookDTO bookDTO) {
        if (bookDTO.getTitle() != null) {
            book.setTitle(bookDTO.getTitle());
        }
        
        if (bookDTO.getPublishedDate() != null) {
            book.setPublishedDate(bookDTO.getPublishedDate());
        }
    }

    /**
     * Updates book's author if authorId is provided
     */
    private void updateAuthorIfProvided(Book book, Long authorId) {
        if (authorId == null) {
            return;
        }
        
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        
        book.setAuthor(author);
    }

    /**
     * Updates book's categories if categoryIds are provided
     */
    private void updateCategoriesIfProvided(Book book, Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        
        Set<Category> categories = fetchCategoriesByIds(categoryIds);
        book.setCategories(categories);
    }

    /**
     * Fetches all categories by their IDs
     */
    private Set<Category> fetchCategoriesByIds(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(this::findCategoryById)
                .collect(Collectors.toSet());
    }

    /**
     * Finds category by ID or throws exception
     */
    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
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
