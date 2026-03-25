package com.booking.controllers;

import com.booking.dto.request.BookingPreviewRequest;
import com.booking.dto.response.BookingPreviewResponse;
import com.booking.dto.response.CategoryResponse;
import com.booking.services.CategoryService;
import com.booking.services.ReservationService;
import com.booking.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class HomeController {
    private final RoomService roomService;
    private final CategoryService categoryService;
    private final ReservationService reservationService;

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/booking")
    public String bookingRooms(Model model){
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("step", 1);
        return "pages/booking";
    }

    @GetMapping("/booking/search")
    public String search(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam int rooms,
            @RequestParam int adults,
            @RequestParam int children,
            Model model
    ){
        var categories = categoryService.availableRooms(java.sql.Date.valueOf(fromDate), java.sql.Date.valueOf(toDate), rooms, adults, children);
        model.addAttribute("categories", categories);
        model.addAttribute("step", 1);
        return "fragments/user/category-available :: categoryList";
    }

    @PostMapping("/booking/review")
    public ResponseEntity<BookingPreviewResponse> reviewPost(
            @RequestBody BookingPreviewRequest request
    ){
        var reservation = reservationService.preview(request);
        return ResponseEntity.ok().body(reservation);
    }

    @GetMapping("/booking/review")
    public String reviewPage(@RequestParam Long reservationId, Model model){
        var reservation = reservationService.get(reservationId);
        LocalDate checkIn = reservation.getCheck_in().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate checkOut = reservation.getCheck_out().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        String checkInDay = checkIn.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String checkOutDay = checkOut.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = reservation.getRooms().stream().mapToDouble(r -> r.getCategory().getPrice()).sum();
        model.addAttribute("step", 2);
        model.addAttribute("checkIn", checkInDay);
        model.addAttribute("checkOut", checkOutDay);
        model.addAttribute("reservation", reservation);
        model.addAttribute("nights", nights);
        model.addAttribute("total", total*nights);
        return "pages/booking";
    }

    @GetMapping("/booking/payment")
    public String payment(Model model){
        model.addAttribute("step", 3);
        return "pages/booking-payment";
    }
}
