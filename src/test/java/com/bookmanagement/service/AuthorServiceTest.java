package com.bookmanagement.service;

import com.bookmanagement.dto.AllAuthorDTO;
import com.bookmanagement.dto.AuthorDTO;
import com.bookmanagement.dto.NewAuthorDTO;
import com.bookmanagement.entity.Author;
import com.bookmanagement.entity.Book;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.AuthorMapper;
import com.bookmanagement.repository.AuthorRepository;
import com.bookmanagement.service.impl.AuthorServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.*;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService Tests")
class AuthorServiceTest {
  
   @Mock
   private AuthorRepository authorRepository;
  
   @Mock
   private AuthorMapper authorMapper;
  
   @InjectMocks
   private AuthorServiceImpl authorService;
  
   private Author author;
   private AuthorDTO authorDTO;
   private NewAuthorDTO newAuthorDTO;
   private AllAuthorDTO allAuthorDTO;
  
   @BeforeEach
   void setUp() {
       author = Author.builder()
               .id(1L)
               .name("Robert C. Martin")
               .email("uncle.bob@example.com")
               .bio("Software engineer")
               .books(new HashSet<>())
               .build();
      
       authorDTO = AuthorDTO.builder()
               .id(1L)
               .name("Robert C. Martin")
               .email("uncle.bob@example.com")
               .bio("Software engineer")
               .build();


       allAuthorDTO = AllAuthorDTO.builder()
       .id(1L)
       .name("Robert C. Martin")
       .email("uncle.bob@example.com")
       .build();


       newAuthorDTO = NewAuthorDTO.builder()
           .name("Robert C. Martin")
           .email("uncle.bob@example.com")
           .bio("Software engineer")
           .build();
   }
  
   @Test
   @DisplayName("Should get all authors successfully")
   void testGetAllAuthors_Success() {
       // Arrange
       when(authorRepository.findAll()).thenReturn(List.of(author));
       when(authorMapper.toAllDTO(author)).thenReturn(allAuthorDTO);
      
       // Act
       List<AllAuthorDTO> result = authorService.getAllAuthors();
      
       // Assert
       assertThat(result).hasSize(1);
       assertThat(result.get(0).getName()).isEqualTo("Robert C. Martin");
       verify(authorRepository, times(1)).findAll();
   }
  
   @Test
   @DisplayName("Should return empty list when no authors exist")
   void testGetAllAuthors_EmptyList() {
       // Arrange
       when(authorRepository.findAll()).thenReturn(Collections.emptyList());
      
       // Act
       List<AllAuthorDTO> result = authorService.getAllAuthors();
      
       // Assert
       assertThat(result).isEmpty();
   }
  
   @Test
   @DisplayName("Should get author by ID successfully")
   void testGetAuthorById_Success() {
       // Arrange
       when(authorRepository.findByIdWithBooks(1L)).thenReturn(Optional.of(author));
       when(authorMapper.toDTO(author)).thenReturn(authorDTO);
      
       // Act
       AuthorDTO result = authorService.getAuthorById(1L);
      
       // Assert
       assertThat(result).isNotNull();
       assertThat(result.getName()).isEqualTo("Robert C. Martin");
   }
  
   @Test
   @DisplayName("Should throw ResourceNotFoundException when author not found")
   void testGetAuthorById_NotFound() {
       // Arrange
       when(authorRepository.findByIdWithBooks(999L)).thenReturn(Optional.empty());
      
       // Act & Assert
       assertThatThrownBy(() -> authorService.getAuthorById(999L))
               .isInstanceOf(ResourceNotFoundException.class)
               .hasMessageContaining("Author not found with id: 999");
   }
  
   @Test
   @DisplayName("Should create author successfully")
   void testCreateAuthor_Success() {
       // Arrange
       when(authorRepository.findByEmail(authorDTO.getEmail())).thenReturn(Optional.empty());
       when(authorMapper.toEntity(newAuthorDTO)).thenReturn(author);
       when(authorRepository.save(any(Author.class))).thenReturn(author);
       when(authorMapper.toDTO(author)).thenReturn(authorDTO);
      
       // Act
       AuthorDTO result = authorService.createAuthor(newAuthorDTO);
      
       // Assert
       assertThat(result).isNotNull();
       assertThat(result.getEmail()).isEqualTo("uncle.bob@example.com");
       verify(authorRepository, times(1)).save(any(Author.class));
   }
  
   @Test
   @DisplayName("Should throw DuplicateResourceException when email exists")
   void testCreateAuthor_DuplicateEmail() {
       // Arrange
       when(authorRepository.findByEmail(authorDTO.getEmail())).thenReturn(Optional.of(author));
      
       // Act & Assert
       assertThatThrownBy(() -> authorService.createAuthor(newAuthorDTO))
               .isInstanceOf(DuplicateResourceException.class)
               .hasMessageContaining("Author with email " + authorDTO.getEmail() + " already exists");
   }
  
   @Test
   @DisplayName("Should update author successfully")
   void testUpdateAuthor_Success() {
       // Arrange
       when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
       when(authorRepository.save(any(Author.class))).thenReturn(author);
       when(authorMapper.toDTO(author)).thenReturn(authorDTO);
      
       NewAuthorDTO updateDTO = NewAuthorDTO.builder()
               .name("Updated Name")
               .build();
      
       // Act
       AuthorDTO result = authorService.updateAuthor(1L, updateDTO);
      
       // Assert
       assertThat(result).isNotNull();
       verify(authorRepository, times(1)).save(any(Author.class));
   }
  
   @Test
   @DisplayName("Should throw exception when updating email to existing one")
   void testUpdateAuthor_DuplicateEmail() {
       // Arrange
       Author anotherAuthor = Author.builder().id(2L).email("new@email.com").build();
       when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
       when(authorRepository.findByEmail("new@email.com")).thenReturn(Optional.of(anotherAuthor));
      
       NewAuthorDTO updateDTO = NewAuthorDTO.builder().email("new@email.com").build();
      
       // Act & Assert
       assertThatThrownBy(() -> authorService.updateAuthor(1L, updateDTO))
               .isInstanceOf(DuplicateResourceException.class);
   }
  
   @Test
   @DisplayName("Should delete author successfully")
   void testDeleteAuthor_Success() {
       // Arrange
       when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
       doNothing().when(authorRepository).deleteById(1L);
      
       // Act
       authorService.deleteAuthor(1L);
      
       // Assert
       verify(authorRepository, times(1)).deleteById(1L);
   }
  
   @Test
   @DisplayName("Should throw exception when deleting author with books")
   void testDeleteAuthor_HasBooks() {
       // Arrange
       Book book = Book.builder().id(1L).title("Test Book").build();
       author.getBooks().add(book);
       when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
      
       // Act & Assert
       assertThatThrownBy(() -> authorService.deleteAuthor(1L))
               .isInstanceOf(InvalidOperationException.class)
               .hasMessageContaining("Cannot delete author with existing books");
   }
}


