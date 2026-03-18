package com.booking.controllers.api;

import com.booking.dto.request.CategoryRequest;
import com.booking.dto.response.CategoryResponse;
import com.booking.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
class ApiCategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok().body(categoryService.getAll());
    }

    @GetMapping("/id")
    public ResponseEntity<CategoryResponse> getCategoryById(@RequestParam String id) {
        return ResponseEntity.ok().body(categoryService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> addCategory(
            @ModelAttribute @Valid CategoryRequest categoryRequest
    ){
        return ResponseEntity.ok().body(categoryService.add(categoryRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<CategoryResponse> updateCategory(
            @RequestParam String id,
            @ModelAttribute @Valid CategoryRequest categoryRequest
    ){
           return  ResponseEntity.ok().body(categoryService.update(id,categoryRequest));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<List<CategoryResponse>> deleteCategory(@RequestParam String id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok().body(categoryService.getAll());
    }

    @PostMapping("/available")
    public ResponseEntity<List<CategoryResponse>> searchAvailableCategories(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam int rooms,
            @RequestParam int adults,
            @RequestParam int children
    ){
        var categories = categoryService.availableRooms(new Date(fromDate), new Date(toDate), rooms, adults, children);
        return ResponseEntity.ok().body(categories);
    }
}
