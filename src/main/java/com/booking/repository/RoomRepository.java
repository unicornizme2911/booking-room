package com.booking.repository;

import com.booking.models.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomModel,String> {
    List<RoomModel> findAllByCategory_Id(Long categoryId);

    RoomModel findByRoomNumber(String roomNumber);
}
