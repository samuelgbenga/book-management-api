package com.bookmanagement.service;

import com.bookmanagement.dto.*;
import com.bookmanagement.entity.*;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.BookMapper;
import com.bookmanagement.repository.*;
import com.bookmanagement.service.impl.BookServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private AuthorRepository authorRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private BookMapper bookMapper;
    
    @InjectMocks
    private BookServiceImpl bookService;
    
    private Book book;
    private Author author;
    private Category category;
    private BookDTO bookDTO;
    private NewBookDTO newBookDTO;
    private BookDetailDTO bookDetailDTO;
    private BookPaginationDTO bookPaginationDTO;
    
    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(1L)
                .name("Robert C. Martin")
                .email("uncle.bob@example.com")
                .bio("Software engineer and author")
                .build();
        
        category = Category.builder()
                .id(1L)
                .name("Programming")
                .build();
        
        book = Book.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("978-0132350884")
                .publishedDate(LocalDate.of(2008, 8, 1))
                .author(author)
                .categories(new HashSet<>(Set.of(category)))
                .reviews(new HashSet<>())
                .rating(0.0)
                .build();
        
        AuthorSummaryDTO authorSummary = AuthorSummaryDTO.builder()
                .id(1L)
                .name("Robert C. Martin")
                .build();
        
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Programming")
                .build();
        
        bookDTO = BookDTO.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("978-0132350884")
                .publishedDate(LocalDate.of(2008, 8, 1))
                .authorId(1L)
                .categoryIds(Set.of(1L))
                .author(authorSummary)
                .categories(Set.of(categoryDTO))
                .rating(0.0)
                .build();
        
        bookDetailDTO = BookDetailDTO.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("978-0132350884")
                .rating(0.0)
                .build();

         // ✅ NewBookDTO initialization
        newBookDTO = NewBookDTO.builder()
                .title("Clean Code")
                .isbn("978-0132350884")
                .publishedDate(LocalDate.of(2008, 8, 1))
                .authorId(1L)
                .categoryIds(Set.of(1L))
                .build();
        
        // ✅ BookPaginationDTO initialization
        bookPaginationDTO = BookPaginationDTO.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("978-0132350884")
                .publishedDate(LocalDate.of(2008, 8, 1))
                .build();
    }
    
    // ==================== GET ALL BOOKS TESTS ====================
    
@Test
@DisplayName("Should get all books successfully")
void testGetAllBooks_Success() {
    // Arrange
    Page<Book> bookPage = new PageImpl<>(List.of(book));

    // Create a BookPaginationDTO to match the expected return type
    BookPaginationDTO bookPaginationDTO = BookPaginationDTO.builder()
            .id(1L)
            .title("Clean Code")
            .isbn("978-0132350884")
            .publishedDate(LocalDate.of(2008, 8, 1))
            //.author(AuthorSummaryDTO.builder().id(1L).name("Robert C. Martin").build())
            .build();

    when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(bookPage);

    // Mock the correct mapper method (toPaginationDTO, not toDTO)
    when(bookMapper.toPaginationDTO(any(Book.class)))
            .thenReturn(bookPaginationDTO);

    GetAllBookParamsDTO getAllBookParamsDTO = new GetAllBookParamsDTO(
            0, 20, null, null, null, null, null, null, null
    );

    // Act
    Page<BookPaginationDTO> result = bookService.getAllBooks(getAllBookParamsDTO);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0)).isNotNull();
    assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    assertThat(result.getContent().get(0).getIsbn()).isEqualTo("978-0132350884");
    //assertThat(result.getContent().get(0).getAuthor()).isEqualTo(AuthorSummaryDTO.builder().id(1L).name("Robert C. Martin").build());

    verify(bookRepository, times(1))
            .findAll(any(Specification.class), any(Pageable.class));

    verify(bookMapper, times(1))
            .toPaginationDTO(any(Book.class));
}
    
    @Test
    @DisplayName("Should get books with author filter")
    void testGetAllBooks_WithAuthorFilter() {
        // Arrange
        // Page<Book> bookPage = new PageImpl<>(List.of(book));
        // when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
        //         .thenReturn(bookPage);
        // when(bookMapper.toDTO(book)).thenReturn(bookDTO);
        Page<Book> bookPage = new PageImpl<>(List.of(book));
         BookPaginationDTO bookPaginationDTO = BookPaginationDTO.builder()
            .id(1L)
            .title("Clean Code")
            .isbn("978-0132350884")
            .publishedDate(LocalDate.of(2008, 8, 1))
            //.author(AuthorSummaryDTO.builder().id(1L).name("Robert C. Martin").build())
            .build();

    when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(bookPage);

    // Mock the correct mapper method (toPaginationDTO, not toDTO)
    when(bookMapper.toPaginationDTO(any(Book.class)))
            .thenReturn(bookPaginationDTO);

            GetAllBookParamsDTO getAllBookParamsDTO = new GetAllBookParamsDTO(
                0, 20, 1L, null, null, null, null, null, null
        );
        // Act
        Page<BookPaginationDTO> result = bookService.getAllBooks(getAllBookParamsDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(bookRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Should return empty page when no books found")
    void testGetAllBooks_EmptyResult() {
        // Arrange
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);
        
        GetAllBookParamsDTO getAllBookParamsDTO = new GetAllBookParamsDTO(
                0, 20, null, null, null, null, null, null, null
        );
        // Act
        Page<BookPaginationDTO> result = bookService.getAllBooks(getAllBookParamsDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }
    
    // ==================== GET BOOK BY ID TESTS ====================
    
    @Test
    @DisplayName("Should get book by ID successfully")
    void testGetBookById_Success() {
        // Arrange
        when(bookRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDetailDTO(book)).thenReturn(bookDetailDTO);
        
        // Act
        BookDetailDTO result = bookService.getBookById(1L);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        
        verify(bookRepository, times(1)).findByIdWithDetails(1L);
        verify(bookMapper, times(1)).toDetailDTO(book);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when book not found")
    void testGetBookById_NotFound() {
        // Arrange
        when(bookRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 999");
        
        verify(bookRepository, times(1)).findByIdWithDetails(999L);
        verify(bookMapper, never()).toDetailDTO(any());
    }
    
    @Test
    @DisplayName("Should handle null ID gracefully")
    void testGetBookById_NullId() {
        // Act & Assert
        assertThatThrownBy(() -> bookService.getBookById(null))
                .isInstanceOf(Exception.class);
    }
    
    // ==================== CREATE BOOK TESTS ====================
    
    @Test
    @DisplayName("Should create book successfully")
    void testCreateBook_Success() {
        // Arrange
        when(bookRepository.findByIsbn(newBookDTO.getIsbn())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookMapper.toEntity(newBookDTO)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDTO(book)).thenReturn(bookDTO);
        
        // Act
        BookDTO result = bookService.createBook(newBookDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getIsbn()).isEqualTo("978-0132350884");
        
        verify(bookRepository, times(1)).findByIsbn(bookDTO.getIsbn());
        verify(authorRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should throw DuplicateResourceException when ISBN already exists")
    void testCreateBook_DuplicateISBN() {
        // Arrange
        when(bookRepository.findByIsbn(bookDTO.getIsbn())).thenReturn(Optional.of(book));
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(newBookDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        
        verify(bookRepository, times(1)).findByIsbn(bookDTO.getIsbn());
        verify(bookRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when author not found")
    void testCreateBook_AuthorNotFound() {
        // Arrange
        when(bookRepository.findByIsbn(bookDTO.getIsbn())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(newBookDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 1");
        
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found")
    void testCreateBook_CategoryNotFound() {
        // Arrange
        when(bookRepository.findByIsbn(bookDTO.getIsbn())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(newBookDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 1");
        
        verify(categoryRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw InvalidOperationException when no categories provided")
    void testCreateBook_NoCategoriesProvided() {
        // Arrange
        NewBookDTO invalidDTO = NewBookDTO.builder()
                .title("Test Book")
                .isbn("978-0132350884")
                .authorId(1L)
                .categoryIds(Set.of())
                .build();
        
        when(bookRepository.findByIsbn(invalidDTO.getIsbn())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(invalidDTO))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Book must have at least one category");
        
        verify(bookRepository, never()).save(any());
    }
    
    // ==================== UPDATE BOOK TESTS ====================
    
    @Test
    @DisplayName("Should update book successfully")
    void testUpdateBook_Success() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDetailDTO(book)).thenReturn(bookDetailDTO);
        
        NewBookDTO updateDTO = NewBookDTO.builder()
                .title("Updated Title")
                .build();
        
        // Act
        BookDetailDTO result = bookService.updateBook(1L, updateDTO);
        
        // Assert
        assertThat(result).isNotNull();
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should update book ISBN when different")
    void testUpdateBook_UpdateISBN() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbn("978-0596007126")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDetailDTO(book)).thenReturn(bookDetailDTO);
        
        NewBookDTO updateDTO = NewBookDTO.builder()
                .isbn("978-0596007126")
                .build();
        
        // Act
        BookDetailDTO result = bookService.updateBook(1L, updateDTO);
        
        // Assert
        assertThat(result).isNotNull();
        verify(bookRepository).findByIsbn("978-0596007126");
    }
    
    @Test
    @DisplayName("Should throw exception when updating to existing ISBN")
    void testUpdateBook_DuplicateISBN() {
        // Arrange
        Book anotherBook = Book.builder().id(2L).isbn("978-0596007126").build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbn("978-0596007126")).thenReturn(Optional.of(anotherBook));
        
        NewBookDTO updateDTO = NewBookDTO.builder()
                .isbn("978-0596007126")
                .build();
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.updateBook(1L, updateDTO))
                .isInstanceOf(DuplicateResourceException.class);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent book")
    void testUpdateBook_BookNotFound() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        NewBookDTO updateDTO = NewBookDTO.builder().title("New Title").build();
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.updateBook(999L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 999");
    }
    
    @Test
    @DisplayName("Should update author when authorId provided")
    void testUpdateBook_UpdateAuthor() {
        // Arrange
        Author newAuthor = Author.builder().id(2L).name("Martin Fowler").build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDetailDTO(book)).thenReturn(bookDetailDTO);
        
        NewBookDTO updateDTO = NewBookDTO.builder().authorId(2L).build();
        
        // Act
        BookDetailDTO result = bookService.updateBook(1L, updateDTO);
        
        // Assert
        assertThat(result).isNotNull();
        verify(authorRepository).findById(2L);
    }
    
    @Test
    @DisplayName("Should update categories when categoryIds provided")
    void testUpdateBook_UpdateCategories() {
        // Arrange
        Category newCategory = Category.builder().id(2L).name("Software Engineering").build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDetailDTO(book)).thenReturn(bookDetailDTO);
        
        NewBookDTO updateDTO = NewBookDTO.builder().categoryIds(Set.of(2L)).build();
        
        // Act
        BookDetailDTO result = bookService.updateBook(1L, updateDTO);
        
        // Assert
        assertThat(result).isNotNull();
        verify(categoryRepository).findById(2L);
    }
    
    // ==================== DELETE BOOK TESTS ====================
    
    @Test
    @DisplayName("Should delete book successfully")
    void testDeleteBook_Success() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);
        
        // Act
        bookService.deleteBook(1L);
        
        // Assert
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent book")
    void testDeleteBook_BookNotFound() {
        // Arrange
        when(bookRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 999");
        
        verify(bookRepository, times(1)).existsById(999L);
        verify(bookRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("Should handle null ID when deleting")
    void testDeleteBook_NullId() {
        // Act & Assert
        assertThatThrownBy(() -> bookService.deleteBook(null))
                .isInstanceOf(Exception.class);
    }
}