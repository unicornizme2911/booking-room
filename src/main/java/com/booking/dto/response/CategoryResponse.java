package com.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    @JsonProperty("name")
    private String name;
    private String description;
    private int capacity;
    @NotEmpty(message = "Price is required")
    private BigDecimal price;
    private String bed_type;
    private int room_size;
    private List<String> images;
    private List<FeatureResponse> features;
    private Long available_rooms;
}
