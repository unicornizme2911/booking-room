package com.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {
    private Long id;
    private Date check_in;
    private Date check_out;
    private String status;
    private UserResponse user;
    private List<RoomResponse> rooms;
    private TransportResponse transport;
    private List<ReservationMealResponse> meals;
}
