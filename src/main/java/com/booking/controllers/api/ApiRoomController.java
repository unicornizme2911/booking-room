package com.booking.controllers.api;

import com.booking.dto.request.RoomRequest;
import com.booking.dto.response.RoomResponse;
import com.booking.services.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class ApiRoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/list")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok().body(roomService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<RoomResponse> addRoom(@Valid @ModelAttribute RoomRequest roomRequest) {
        return ResponseEntity.ok().body(roomService.add(roomRequest));
    }

    @PostMapping("/available")
    public ResponseEntity<List<RoomResponse>> available(
            @RequestParam String fromDate,
            @RequestParam String toDate
    ){
        return ResponseEntity.ok().body(roomService.searchAvailableRooms(new Date(fromDate), new Date(toDate)));
    }
}
