package com.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealRequest {
    private Long id;
    @NotEmpty(message = "Meal name is required")
    private String name;
    private Double price;
    private String description;
}
