package com.bookmanagement.service.impl;

import com.bookmanagement.dto.CategoryDTO;
import com.bookmanagement.entity.Category;
import com.bookmanagement.exception.*;
import com.bookmanagement.mapper.CategoryMapper;
import com.bookmanagement.repository.CategoryRepository;
import com.bookmanagement.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
     public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Category with name " + categoryDTO.getName() + " already exists");
        }
        
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }
    @Override
     @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        if (categoryDTO.getName() != null && !categoryDTO.getName().equals(category.getName())) {
            if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
                throw new DuplicateResourceException("Category with name " + categoryDTO.getName() + " already exists");
            }
            category.setName(categoryDTO.getName());
        }
        
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
     @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        if (!category.getBooks().isEmpty()) {
            throw new InvalidOperationException("Cannot delete category with existing books");
        }
        
        categoryRepository.deleteById(id);
    }

   
    
}
