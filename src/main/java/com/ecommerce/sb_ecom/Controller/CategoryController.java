package com.ecommerce.sb_ecom.Controller;

import com.ecommerce.sb_ecom.Model.Category;
import com.ecommerce.sb_ecom.Service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categories")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping("/api/public/categories")
    public String createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return "Category created";

    }


}
