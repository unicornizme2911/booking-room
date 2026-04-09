package com.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportRequest {
    private Long id;
    @NotEmpty(message = "Transport name is required")
    private String vehicle;
    private BigDecimal price;
}
