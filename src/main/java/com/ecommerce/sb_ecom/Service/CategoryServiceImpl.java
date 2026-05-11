package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public Category getCategoryById(long categoryId) {
        return categories.stream()
                .filter(category -> Objects.equals(category.getCategoryId(), categoryId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createCategory(Category category) {

        category.setCategoryId(nextId++);
        categories.add(category);

    }

    @Override
    public String updateCategory(long categoryId, Category category) {
        Category existingCategory = getCategoryById(categoryId);

        if (existingCategory == null) {
            return "Category not found";
        }

        existingCategory.setCategoryName(category.getCategoryName());
        return "Category with id: " + categoryId + " has been updated";
    }

    @Override
    public String deleteCategory(long categoryId) {
        boolean categoryRemoved = categories.removeIf(category -> Objects.equals(category.getCategoryId(), categoryId));

        if (!categoryRemoved) {
            return "Category not found";
        }

        return "Category with id: " + categoryId + " has been deleted";

    }


}
