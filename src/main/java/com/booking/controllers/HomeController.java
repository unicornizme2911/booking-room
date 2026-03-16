package com.booking.controllers;

import com.booking.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class HomeController {
    private final RoomService roomService;

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/booking-rooms")
    public String bookingRooms(Model model){
//        model.addAttribute("rooms", roomService.getAll());
        return "pages/booking";
    }
}
