package com.booking.controllers.api;

import com.booking.dto.request.ReservationRequest;
import com.booking.dto.response.ReservationResponse;
import com.booking.models.ReservationStatus;
import com.booking.services.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reservations")
class ApiReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/update/{id}")
    public ResponseEntity<ReservationResponse> update(@PathVariable Long id,
                                                      @RequestParam String full_name,
                                                      @RequestParam String phone,
                                                      @RequestParam @Email String email) {
        var reservation = reservationService.update(id, full_name, phone, email);
        return ResponseEntity.ok().body(reservation);
    }

    @PostMapping("/cancel/{id}")
    @Transactional
    public ResponseEntity<ReservationResponse> cancel(@PathVariable Long id){
        var reservation = reservationService.setStatus(id, ReservationStatus.CANCELLED);
        return ResponseEntity.ok().body(reservation);
    }
}
