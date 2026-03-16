package com.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    private Long id;
    @NotEmpty(message = "Category name is required")
    private String name;
    private String description;
    private int capacity;
    @NotNull(message = "Price is required")
    private double price;
    private String bed_type;
    private int room_size;
    private List<MultipartFile> images;
    private List<Long> feature_ids;
}
