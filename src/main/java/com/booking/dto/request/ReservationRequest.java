package com.booking.dto.request;

import com.booking.models.ReservationModel;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private Long id;
    private Date check_in;
    private Date check_out;
    private String status;
    @NotEmpty
    private String user_id;
    @NotEmpty
    private List<String> room_number;
    private String transport_id;
    private List<ReservationMealRequest> meals;
}
