package com.ecommerce.sb_ecom.Service;
import com.ecommerce.sb_ecom.Model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

        List<Category> getCategories();
        Category getCategoryById(long categoryId);
        void createCategory(Category category);
        String updateCategory(long categoryId, Category category);
        String deleteCategory(long categoryId);
}
