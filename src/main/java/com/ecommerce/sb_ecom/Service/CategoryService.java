package com.ecommerce.sb_ecom.Service;
import com.ecommerce.sb_ecom.Model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

        List<Category> getCategories();
        void createCategory(Category category);
}
