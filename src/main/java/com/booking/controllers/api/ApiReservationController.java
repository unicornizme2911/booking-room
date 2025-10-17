package com.booking.controllers.api;

import com.booking.dto.request.ReservationRequest;
import com.booking.dto.response.ReservationResponse;
import com.booking.services.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/resevations")
class ApiReservationController {
    @Autowired
    private ReservationService reservationService;

//    @GetMapping("/list")
//    public ResponseEntity<List<ReservationResponse>> getAllCategories() {
//        return ResponseEntity.ok().body();
//    }
//
//    @GetMapping("/id")
//    public ResponseEntity<ReservationResponse> getReservationById(@RequestParam String id) {
//        return ResponseEntity.ok().body(reservationService.getById(id));
//    }

    @PostMapping("/add")
    public ResponseEntity<ReservationResponse> addReservation(
            @ModelAttribute @Valid ReservationRequest reservationRequest
    ){
        return ResponseEntity.ok().body(reservationService.add(reservationRequest));
    }

    @PostMapping("/checking")
    public ResponseEntity<?>  checkReservation(
            @RequestBody Date fromDate,
            @RequestBody Date toDate
    ){
        return ResponseEntity.ok().body(reservationService.checkRoomInDay(fromDate,toDate));
    }
}
