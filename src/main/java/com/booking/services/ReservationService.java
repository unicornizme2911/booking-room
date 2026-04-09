package com.booking.services;

import com.booking.dto.request.BookingPreviewRequest;
import com.booking.dto.request.CategoryBookingItem;
import com.booking.dto.request.ReservationMealRequest;
import com.booking.dto.request.ReservationRequest;
import com.booking.dto.response.BookingPreviewResponse;
import com.booking.dto.response.ReservationMealResponse;
import com.booking.dto.response.ReservationResponse;
import com.booking.dto.response.RoomResponse;
import com.booking.models.*;
import com.booking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private TransportService transportService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private TransportRepository transportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationRoomRepository reservationRoomRepository;
    @Autowired
    private CategoryStatsService categoryStatsService;


    public ReservationModel toEntity(ReservationRequest request){
        return ReservationModel.builder()
                .id(request.getId())
                .check_in(request.getCheck_in())
                .check_out(request.getCheck_out())
                .status(ReservationStatus.valueOf(request.getStatus()))
                .build();
    }

    public ReservationMeal toEntity(ReservationModel reservation, MealModel meal, int quantity){
        return ReservationMeal.builder()
                .reservation(reservation)
                .meal(meal)
                .quantity(quantity)
                .build();
    }

    public ReservationResponse toResponse(ReservationModel reservation){
        return ReservationResponse.builder()
                .id(reservation.getId())
                .check_in(reservation.getCheck_in())
                .check_out(reservation.getCheck_out())
                .expiredAt(reservation.getExpiredAt())
                .user(reservation.getUser() == null ? null : userService.toResponse(reservation.getUser()))
                .rooms(reservation.getReservation_rooms() == null ? null :
                        reservation.getReservation_rooms().stream().map(rm -> {
                            return roomService.toResponse(rm.getRoom());
                        }).toList())
                .transport(reservation.getTransport() == null ? null : transportService.toResponse(reservation.getTransport()))
                .meals(reservation.getReservation_meals() == null ? null :
                        reservation.getReservation_meals().stream().map(rm -> {
                            return mealResponse(String.valueOf(rm.getId()), rm.getQuantity());
                        }).toList())
                .build();
    }

    public List<ReservationResponse> toResponse(List<ReservationModel> reservations){
        return reservations.stream().map(this::toResponse).toList();
    }

    public ReservationMealResponse mealResponse(String id, int quantity){
        var meal = mealRepository.findById(id).orElseThrow();
        return ReservationMealResponse.builder()
                .id(Long.valueOf(id))
                .name(meal.getName())
                .quantity(quantity)
                .build();
    }

    public ReservationResponse add(ReservationRequest request){
        var transport = transportRepository.findById(request.getTransport_id()).orElse(null);

        var user = userRepository.findById(request.getUser_id()).orElseThrow();

        List<RoomModel> rooms = new ArrayList<>();
        for(String rm: request.getRoom_number()){
            rooms.add(roomRepository.findByRoomNumber(rm));
        }

        if(!request.getStatus().equalsIgnoreCase("pending")){
            return null;
        }

        var reservation = toEntity(request);
        reservation.setUser(user);
        reservation.setTransport(transport);
//        reservation.setRooms(rooms);

        var reservationSaved = reservationRepository.save(reservation);

        List<ReservationMeal> reservation_meals = new ArrayList<>();
        if(request.getMeals() != null){
            for(ReservationMealRequest mealRequest: request.getMeals()){
                var meal = mealRepository.findById(mealRequest.getId()).orElseThrow();
                reservation_meals.add(toEntity(reservationSaved, meal, mealRequest.getQuantity()));
            }
        }
        reservationSaved.setReservation_meals(reservation_meals);
        return toResponse(reservationSaved);
    }

    public ReservationResponse update(String id, String full_name, String phone, String email){
        var reservation = reservationRepository.findById(id).orElseThrow();
        reservation.setFull_name(full_name);
        reservation.setPhone(phone);
        reservation.setEmail(email);
        reservationRepository.save(reservation);
        return toResponse(reservation);
    }

    @Transactional
    public BookingPreviewResponse preview(BookingPreviewRequest request){
        List<RoomModel> selectedRooms = new ArrayList<>();
        for(CategoryBookingItem item : request.getCategories()){
            List<RoomModel> rooms = roomRepository.findRandomAvailableRooms(
                    item.getCategoryId(),
                    request.getFromDate(),
                    request.getToDate(),
                    item.getRooms()
            );
            if (rooms.size() < item.getRooms()) {
                throw new RuntimeException("Not enough rooms");
            }
            selectedRooms.addAll(rooms);
        }

        List<Long> roomIds = selectedRooms.stream().map(RoomModel::getId).toList();
        List<RoomModel> lockedRooms = roomRepository.lockRooms(roomIds);

        ReservationModel reservation = new ReservationModel();
        reservation.setCheck_in(request.getFromDate());
        reservation.setCheck_out(request.getToDate());
        reservation.setStatus(ReservationStatus.HOLD);
        reservation.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        reservationRepository.save(reservation);
        for (RoomModel room: lockedRooms) {
            if (reservationRepository.existsConflict(room.getId(),request.getFromDate(),request.getToDate())) {
                throw new RuntimeException("Room already booked");
            } else {
                ReservationRoom rr = new ReservationRoom();
                var price = room.getCategory().getPrice().multiply(BigDecimal.valueOf(request.getNights()));
                rr.setRoom(room);
                rr.setReservation(reservation);
                rr.setPrice_at_booking(price);
                reservationRoomRepository.save(rr);
            }
        }

        return BookingPreviewResponse.builder()
                .id(reservation.getId())
                .rooms(roomService.toResponse(lockedRooms))
                .fromDate(reservation.getCheck_in())
                .toDate(reservation.getCheck_out())
                .expiredAt(reservation.getExpiredAt())
                .build();
    }

    @Transactional
    public void confirm(ReservationModel reservation){
        if(reservation.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Reservation expired");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        for (ReservationRoom rr : reservation.getReservation_rooms()) {
            Long category_id = rr.getRoom().getCategory().getId();
            categoryStatsService.updateBooking(category_id, 1);
        }
    }

    public ReservationResponse get(Long id){
        var reservation = reservationRepository.findById(String.valueOf(id)).orElseThrow();
        return toResponse(reservation);
    }

    public ReservationResponse setStatus(Long id, ReservationStatus status){
        var reservation = reservationRepository.findById(String.valueOf(id)).orElseThrow();
        if (reservation.getStatus().isHold()){
            reservation.setStatus(status);
            reservationRepository.save(reservation);
        }
        return toResponse(reservation);
    }

    public BigDecimal getTotal(Long id) {
        var reservation = get(id);
        BigDecimal total = BigDecimal.ZERO;
        for(RoomResponse room : reservation.getRooms()) {
            total = total.add(room.getCategory().getPrice());
        }
        return total;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancel(){
        List<ReservationModel> reservations = reservationRepository
                .findByStatusAndExpiredAtBefore(ReservationStatus.HOLD,LocalDateTime.now());
        reservations.forEach(reservation -> reservation.setStatus(ReservationStatus.CANCELLED));
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredReservations() {
        List<ReservationModel> expired = reservationRepository
                .findByStatusAndExpiredAtBefore(ReservationStatus.HOLD,LocalDateTime.now());
        for (var r : expired) {
            r.setStatus(ReservationStatus.CANCELLED);
            reservationRoomRepository.deleteByReservation(r);
        }
        reservationRepository.saveAll(expired);
    }
}
