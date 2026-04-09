package com.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private Long reservation_id;
    private Long category_id;
    private int rating;
    private String comment;
}
