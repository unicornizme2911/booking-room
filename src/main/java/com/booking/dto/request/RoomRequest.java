package com.booking.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    private Long id;
    @Column(nullable = false, unique = true)
    private String roomNumber;
    @Column(nullable = false, unique = true)
    private int roomFloor;
    private Double price;
    private String status;
    private MultipartFile imagePath;
    private String categoryId;
}
