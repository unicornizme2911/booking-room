package com.booking.controllers;

import com.booking.configuration.VNPayConfig;
import com.booking.dto.request.BookingPreviewRequest;
import com.booking.dto.request.PaymentRequest;
import com.booking.dto.request.VNPayRequest;
import com.booking.dto.response.BookingPreviewResponse;
import com.booking.models.PaymentModel;
import com.booking.models.ReservationModel;
import com.booking.services.CategoryService;
import com.booking.services.PaymentService;
import com.booking.services.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    private final CategoryService categoryService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @GetMapping("")
    public String bookingRooms(Model model){
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("step", 1);
        return "pages/booking";
    }

    @GetMapping("/search")
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

    @PostMapping("/review")
    public ResponseEntity<BookingPreviewResponse> reviewPost(
            @RequestBody BookingPreviewRequest request
    ){
        var reservation = reservationService.preview(request);
        return ResponseEntity.ok().body(reservation);
    }

    @GetMapping("/review")
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

    @GetMapping("/payment")
    public String payment(@RequestParam Long reservationId, Model model){
        var reservation = reservationService.get(reservationId);
        double total = reservationService.getTotal(reservationId);
        model.addAttribute("total", total);
        model.addAttribute("reservation", reservation);
        model.addAttribute("step", 3);
        return "pages/booking";
    }

    @PostMapping("/payment/vnpay")
    public ResponseEntity<String> createPayment(@RequestParam Long reservationId) {
        try {
            long total = reservationService.getTotal(reservationId);
            String txnRef = VNPayConfig.getRandomNumber(8);

            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setTxnRef(txnRef);
            paymentRequest.setReservation_id(reservationId);
            paymentRequest.setTotal(total);
            paymentRequest.setProvider("VNPAY");
            var payment = paymentService.add(paymentRequest);
            VNPayRequest request = new VNPayRequest(payment.getTxnRef(), payment.getTotal());
            String paymentUrl = paymentService.createVNPay(request);
            return ResponseEntity.ok().body(paymentUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi tạo thanh toán!");
        }
    }

    @GetMapping("payment/vnpay-return")
    public String returnPayment(HttpServletRequest request, Model model){
        int paymentStatus = paymentService.VNPayReturn(request);
        System.out.println("paymentStatus = " + paymentStatus);
        model.addAttribute("step",4);
        return paymentStatus == 1 ? "pages/booking" : "redirect:/";
    }
}
