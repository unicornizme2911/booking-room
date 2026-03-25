package com.booking.dto.request;

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
public class BookingPreviewRequest {
    private Date fromDate;
    private Date toDate;
    private int nights;
    private List<CategoryBookingItem> categories;
}
