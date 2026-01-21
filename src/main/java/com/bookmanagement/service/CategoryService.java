package com.bookmanagement.service;

import com.bookmanagement.dto.CategoryDTO;
import com.bookmanagement.dto.NewCategoryDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO createCategory(NewCategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, NewCategoryDTO categoryDTO);

    void deleteCategory(Long id);
}
