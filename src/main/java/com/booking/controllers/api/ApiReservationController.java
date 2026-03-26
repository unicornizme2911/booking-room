package com.booking.controllers.api;

import com.booking.dto.request.ReservationRequest;
import com.booking.dto.response.ReservationResponse;
import com.booking.services.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reservations")
class ApiReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/add")
    public ResponseEntity<ReservationResponse> addReservation(
            @ModelAttribute @Valid ReservationRequest reservationRequest
    ){
        return ResponseEntity.ok().body(reservationService.add(reservationRequest));
    }

    @PostMapping("/cancel/{id}")
    @Transactional
    public ResponseEntity<ReservationResponse> cancel(@PathVariable Long id){
        var reservation = reservationService.setStatus(id, "CANCELLED");
        return ResponseEntity.ok().body(reservation);
    }
}
