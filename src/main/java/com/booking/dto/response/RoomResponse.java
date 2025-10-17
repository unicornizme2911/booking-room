package com.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private int roomFloor;
    private Double price;
    private String status;
    private String imagePath;
    private String categoryName;
    private List<ReservationResponse> reservations;
}