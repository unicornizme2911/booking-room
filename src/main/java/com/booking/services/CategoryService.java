package com.booking.services;

import com.booking.dto.request.CategoryRequest;
import com.booking.dto.response.CategoryAvailableResponse;
import com.booking.dto.response.CategoryResponse;
import com.booking.models.CategoryImage;
import com.booking.models.CategoryModel;
import com.booking.models.FeatureModel;
import com.booking.repository.CategoryImageRepository;
import com.booking.repository.CategoryRepository;
import com.booking.repository.FeatureRepository;
import com.booking.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private CategoryImageRepository categoryImageRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private FeatureService featureService;

    public CategoryResponse toResponse(CategoryModel category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .capacity(category.getCapacity())
                .price(category.getPrice())
                .bed_type(category.getBed_type())
                .room_size(category.getRoom_size())
                .images(category.getImages() == null ? List.of() :
                        category.getImages().stream().map(CategoryImage::getImage).toList())
                .features(category.getFeatures() == null ? null : featureService.toResponse(category.getFeatures()))
                .build();
    }

    public CategoryResponse toResponse(CategoryModel category, Long room_available){
        CategoryResponse categoryResponse = toResponse(category);
        categoryResponse.setAvailable_rooms(room_available);
        return categoryResponse;
    }

    public List<CategoryResponse> toResponse(List<CategoryModel> category){
        return category.stream().map(this::toResponse).toList();
    }

    public CategoryModel toEntity(CategoryRequest request){
        return CategoryModel.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .bed_type(request.getBed_type())
                .room_size(request.getRoom_size())
                .build();
    }

    public CategoryResponse add(CategoryRequest request){
        if(categoryRepository.findByName(request.getName()).isPresent()){
            throw new IllegalArgumentException("Category already exists");
        }
        CategoryModel category = toEntity(request);
        List<FeatureModel> features = featureRepository.findAllById(request.getFeature_ids());
        category.setFeatures(features);
        CategoryModel categorySaved = categoryRepository.save(category);
        if(request.getImages() != null){
            for(MultipartFile image : request.getImages()){
                try {
                    String filename = UUID.randomUUID() + ".jpg";
                    FileUploadUtil.saveFile("/categories/", filename, image);

                    CategoryImage categoryImageEntity = new CategoryImage();
                    categoryImageEntity.setCategory(categorySaved);
                    categoryImageEntity.setImage("/uploads/categories/" + filename);

                    categoryImageRepository.save(categoryImageEntity);
                    categorySaved.getImages().add(categoryImageEntity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        categoryRepository.save(categorySaved);
        return toResponse(categorySaved);
    }

    public List<CategoryResponse> availableRooms(Date fromDate, Date toDate, int rooms, int adults, int children){
        var guests = children + adults;
        if(fromDate == null || toDate == null || fromDate.after(toDate) || guests <= 0 || rooms <= 0){
            throw new RuntimeException("Invalid date range");
        }
        List<CategoryAvailableResponse> categories = categoryRepository.countRoomAvailable(fromDate, toDate, guests);
        List<CategoryResponse> responses = new ArrayList<>();
        categories.forEach(c -> {
            CategoryModel category = categoryRepository.findById(String.valueOf(c.getId())).orElseThrow();
            if (c.getAvailable_rooms() >= rooms) {
                responses.add(toResponse(category,c.getAvailable_rooms()));
            }
        });
        return responses;
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
