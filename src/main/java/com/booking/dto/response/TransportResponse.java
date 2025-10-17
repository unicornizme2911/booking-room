package com.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportResponse {
    private Long id;
    @JsonProperty("name")
    private String vehicle;
    private Double price;
    private List<ReservationResponse> reservations;
}
