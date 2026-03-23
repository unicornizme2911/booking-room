package com.booking.controllers;

import com.booking.dto.response.CategoryResponse;
import com.booking.services.CategoryService;
import com.booking.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class HomeController {
    private final RoomService roomService;
    private final CategoryService categoryService;

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

    @GetMapping("/booking/review")
    public String review(Model model){
        model.addAttribute("step", 2);
        return  "pages/booking";
    }
}
