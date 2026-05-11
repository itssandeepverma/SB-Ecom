package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<>();

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {

        categories.add(category);

    }
}
