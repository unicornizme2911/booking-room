package com.booking.services;

import com.booking.dto.request.MealRequest;
import com.booking.dto.response.MealResponse;
import com.booking.models.MealModel;
import com.booking.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;

    public MealResponse toResponse(MealModel meal){
        return MealResponse.builder()
                .id(meal.getId())
                .name(meal.getName())
                .price(meal.getPrice())
                .description(meal.getDescription())
                .build();
    }

    public List<MealResponse> toResponse(List<MealModel> meal){
        return meal.stream().map(this::toResponse).toList();
    }

    public MealModel toEntity(MealRequest request){
        return MealModel.builder()
                .id(request.getId())
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
    }

    public MealResponse add(MealRequest request){
        var meal = toEntity(request);
        mealRepository.save(meal);
        return toResponse(meal);
    }

    public Page<MealResponse> get(int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return mealRepository.findAll(pageRequest).map(this::toResponse);
    }

    public List<MealResponse> getAll(){
        return toResponse(mealRepository.findAll());
    }

    public MealResponse getById(String id){
        MealModel meal = mealRepository.findById(id).orElseThrow();
        return toResponse(meal);
    }

    public MealResponse update(String id, MealRequest request){
        var meal = mealRepository.findById(id).orElseThrow();
        meal.setName(request.getName());
        mealRepository.save(meal);
        return toResponse(meal);
    }

    public void deleteById(String id){
        mealRepository.deleteById(id);
    }
}
