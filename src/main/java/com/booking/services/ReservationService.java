package com.booking.services;

import com.booking.dto.request.ReservationMealRequest;
import com.booking.dto.request.ReservationRequest;
import com.booking.dto.response.ReservationMealResponse;
import com.booking.dto.response.ReservationResponse;
import com.booking.models.MealModel;
import com.booking.models.ReservationMeal;
import com.booking.models.ReservationModel;
import com.booking.models.RoomModel;
import com.booking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public ReservationModel toEntity(ReservationRequest request){
        return ReservationModel.builder()
                .id(request.getId())
                .check_in(request.getCheck_in())
                .check_out(request.getCheck_out())
                .status(request.getStatus())
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

//    public List<RoomModel> checkRoomInDay(Date fromDate, Date toDate){
//        if (fromDate.after(toDate) || fromDate.equals(toDate)) {
//            throw new IllegalArgumentException("Check in date must be before check out date");
//        }
//
//        List<ReservationModel> reservations = reservationRepository.findOverlappingReservations(fromDate, toDate);
//
//        Set<Long> room_ids = reservations.stream()
//                .flatMap(r -> r.getRooms().stream())
//                .map(RoomModel::getId)
//                .collect(Collectors.toSet());
//
//        List<RoomModel> availableRooms = room_ids.isEmpty()
//                ? roomRepository.findAll()
//                : roomRepository.findAll().stream().filter(r -> !room_ids.contains(r.getId())).toList();
//
//        return availableRooms;
//    }


}
