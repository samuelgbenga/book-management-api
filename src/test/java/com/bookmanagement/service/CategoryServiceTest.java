package com.bookmanagement.service;

import com.bookmanagement.dto.CategoryDTO;
import com.bookmanagement.dto.NewCategoryDTO;
import com.bookmanagement.entity.Book;
import com.bookmanagement.entity.Category;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.CategoryMapper;
import com.bookmanagement.repository.CategoryRepository;
import com.bookmanagement.service.impl.CategoryServiceImpl;

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
@DisplayName("CategoryService Tests")
class CategoryServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private CategoryMapper categoryMapper;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    
    private Category category;
    private CategoryDTO categoryDTO;
    private NewCategoryDTO newCategoryDTO;
    
    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Programming")
                .books(new HashSet<>())
                .build();
        
        categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Programming")
                .build();

        newCategoryDTO = NewCategoryDTO.builder()
                .name("Programming")
                .build();
    }
    
    @Test
    @DisplayName("Should get all categories successfully")
    void testGetAllCategories_Success() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);
        
        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Programming");
        
        verify(categoryRepository).findAll();
        verify(categoryMapper).toDTO(category);
    }
    
    @Test
    @DisplayName("Should create category successfully")
    void testCreateCategory_Success() {
        // Arrange
        when(categoryRepository.findByName(newCategoryDTO.getName())).thenReturn(Optional.empty());
       // when(categoryMapper.toEntity(newCategoryDTO)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        
        // Act
        CategoryDTO result = categoryService.createCategory(newCategoryDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Programming");
        
        verify(categoryRepository).findByName("Programming");
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toDTO(category);
    }
    
    @Test
    @DisplayName("Should throw DuplicateResourceException when name exists")
    void testCreateCategory_DuplicateName() {
        // Arrange
        // ✅ FIX: Use newCategoryDTO, not categoryDTO
        when(categoryRepository.findByName(newCategoryDTO.getName()))
                .thenReturn(Optional.of(category));
        
        // Act & Assert
        // ✅ FIX: Pass newCategoryDTO, not categoryDTO
        assertThatThrownBy(() -> categoryService.createCategory(newCategoryDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Category with name " + newCategoryDTO.getName() + " already exists");
        
        verify(categoryRepository).findByName("Programming");
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should update category successfully")
    void testUpdateCategory_Success() {
        // Arrange
        // ✅ FIX: Create NewCategoryDTO for update
        NewCategoryDTO updateDTO = NewCategoryDTO.builder()
                .name("Updated Category")
                .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);
        
        // Act
        // ✅ FIX: Pass newCategoryDTO, not categoryDTO
        CategoryDTO result = categoryService.updateCategory(1L, updateDTO);
        
        // Assert
        assertThat(result).isNotNull();
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toDTO(category);
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void testUpdateCategory_NotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(999L, newCategoryDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");
        
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should delete category successfully")
    void testDeleteCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);
        
        // Act
        assertThatCode(() -> categoryService.deleteCategory(1L))
                .doesNotThrowAnyException();
        
        // Assert
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should throw exception when deleting category with books")
    void testDeleteCategory_HasBooks() {
        // Arrange
        Book book = Book.builder().id(1L).build();
        category.getBooks().add(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot delete category with existing books");
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("Should return empty list when no categories exist")
    void testGetAllCategories_Empty() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(categoryRepository).findAll();
        verify(categoryMapper, never()).toDTO(any());
    }
    
    
}