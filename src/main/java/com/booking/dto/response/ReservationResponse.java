package com.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {
    private Long id;
    private String full_name;
    private String email;
    private String phone;
    private Date check_in;
    private Date check_out;
    private String status;
    private UserResponse user;
    private LocalDateTime expiredAt;
    private List<RoomResponse> rooms;
    private TransportResponse transport;
    private List<ReservationMealResponse> meals;
}
