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

    @GetMapping("/")
    public String home(){
        return "index";
    }
}
