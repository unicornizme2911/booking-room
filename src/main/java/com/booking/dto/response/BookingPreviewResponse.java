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
public class BookingPreviewResponse {
    private Long id;
    private List<RoomResponse> rooms;
    private Date fromDate;
    private Date toDate;
    private LocalDateTime expiredAt;
}
