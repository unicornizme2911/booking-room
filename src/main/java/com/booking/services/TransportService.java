package com.booking.services;

import com.booking.dto.request.TransportRequest;
import com.booking.dto.response.TransportResponse;
import com.booking.models.TransportModel;
import com.booking.repository.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class TransportService {
    @Autowired
    private TransportRepository transportRepository;
    @Autowired
    private ReservationService reservationService;

    public TransportResponse toResponse(TransportModel transport){
        return TransportResponse.builder()
                .id(transport.getId())
                .vehicle(transport.getVehicle())
                .price(transport.getPrice())
                .reservations(transport.getReservations() == null ? null : reservationService.toResponse(transport.getReservations()))
                .build();
    }

    public List<TransportResponse> toResponse(List<TransportModel> transport){
        return transport.stream().map(this::toResponse).toList();
    }

    public TransportModel toEntity(TransportRequest request){
        return TransportModel.builder()
                .id(request.getId())
                .vehicle(request.getVehicle())
                .price(request.getPrice())
                .build();
    }

    public TransportResponse save(TransportRequest request){
        var transport = toEntity(request);
        transportRepository.save(transport);
        return toResponse(transport);
    }

    public Page<TransportResponse> get(int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return transportRepository.findAll(pageRequest).map(this::toResponse);
    }

    public List<TransportResponse> getAll(){
        return toResponse(transportRepository.findAll());
    }

    public TransportResponse getById(String id){
        TransportModel transport = transportRepository.findById(id).orElseThrow();
        return toResponse(transport);
    }

//    public TransportResponse update(String id, TransportRequest request){
//        var transport = transportRepository.findById(id).orElseThrow();
//        transport.setName(request.getName());
//        transportRepository.save(transport);
//        return toResponse(transport);
//    }

    public void deleteById(String id){
        transportRepository.deleteById(id);
    }
}
