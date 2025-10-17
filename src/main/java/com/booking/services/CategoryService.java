package com.booking.services;

import com.booking.dto.request.CategoryRequest;
import com.booking.dto.response.CategoryResponse;
import com.booking.models.CategoryModel;
import com.booking.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RoomService roomService;

    public CategoryResponse toResponse(CategoryModel category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .rooms(category.getRooms() == null ? null : roomService.toResponse(category.getRooms()))
                .build();
    }

    public List<CategoryResponse> toResponse(List<CategoryModel> category){
        return category.stream().map(this::toResponse).toList();
    }

    public CategoryModel toEntity(CategoryRequest request){
        return CategoryModel.builder()
                .id(request.getId())
                .name(request.getName())
                .build();
    }

    public CategoryResponse add(CategoryRequest request){
        var category = toEntity(request);
        categoryRepository.save(category);
        return toResponse(category);
    }

    public Page<CategoryResponse> get(int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepository.findAll(pageRequest).map(this::toResponse);
    }

    public List<CategoryResponse> getAll(){
        return toResponse(categoryRepository.findAll());
    }

    public CategoryResponse getById(String id){
        CategoryModel category = categoryRepository.findById(id).orElseThrow();
        return toResponse(category);
    }

    public CategoryResponse update(String id, CategoryRequest request){
        var category = categoryRepository.findById(id).orElseThrow();
        category.setName(request.getName());
        categoryRepository.save(category);
        return toResponse(category);
    }

    @Transactional
    public void deleteById(String id){
        var category = categoryRepository.findById(id).orElseThrow();
        roomService.deleteRoomOfCategory(Long.valueOf(id));
        categoryRepository.delete(category);
    }
}
