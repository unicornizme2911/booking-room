package com.booking.controllers;

import com.booking.configuration.VNPayConfig;
import com.booking.dto.request.BookingPreviewRequest;
import com.booking.dto.request.PaymentRequest;
import com.booking.dto.request.VNPayRequest;
import com.booking.dto.response.BookingPreviewResponse;
import com.booking.dto.response.CategoryResponse;
import com.booking.services.CategoryService;
import com.booking.services.PaymentService;
import com.booking.services.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            Model model
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> categories = categoryService.availableRooms(
                java.sql.Date.valueOf(fromDate),
                java.sql.Date.valueOf(toDate),
                rooms,
                adults,
                children,
                pageable);
        model.addAttribute("categories", categories);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", categories.getTotalElements());

        model.addAttribute("fromDate",  fromDate);
        model.addAttribute("toDate",    toDate);
        model.addAttribute("rooms",     rooms);
        model.addAttribute("adults",    adults);
        model.addAttribute("children",  children);
        model.addAttribute("step",      1);
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
        var nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal total = reservation.getRooms().stream()
                .map(r -> r.getCategory().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("step", 2);
        model.addAttribute("checkInDay", checkInDay);
        model.addAttribute("checkOutDay", checkOutDay);
        model.addAttribute("reservation", reservation);
        model.addAttribute("nights", nights);
        model.addAttribute("total", total.multiply(BigDecimal.valueOf(nights)));
        return "pages/booking";
    }

    @GetMapping("/payment")
    public String payment(@RequestParam Long reservationId, Model model){
        var reservation = reservationService.get(reservationId);
        BigDecimal total = reservationService.getTotal(reservationId);
        LocalDate checkIn = reservation.getCheck_in().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate checkOut = reservation.getCheck_out().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        var nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        model.addAttribute("total", total);
        model.addAttribute("nights", nights);
        model.addAttribute("reservation", reservation);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("step", 3);
        return "pages/booking";
    }

    @PostMapping("/payment/vnpay")
    public ResponseEntity<String> createPayment(@RequestParam Long reservationId) {
        try {
            BigDecimal total = reservationService.getTotal(reservationId);
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
        model.addAttribute("orderId", request.getParameter("vnp_OrderInfo"));
        model.addAttribute("totalPrice", request.getParameter("vnp_Amount"));
        model.addAttribute("paymentTime", request.getParameter("vnp_PayDate"));
        model.addAttribute("transactionId", request.getParameter("vnp_TransactionNo"));
        model.addAttribute("step",4);
        return paymentStatus == 1 ? "pages/booking" : "redirect:/";
    }
}
