package com.booking.services;

import com.booking.dto.request.RoomRequest;
import com.booking.dto.response.RoomResponse;
import com.booking.models.RoomModel;
import com.booking.repository.CategoryRepository;
import com.booking.repository.RoomRepository;
import com.booking.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CategoryRepository categoryRepository;
//    @Autowired
//    private ReservationService reservationService;

    public RoomResponse toResponse(RoomModel room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomFloor(room.getRoomFloor())
                .price(room.getPrice())
                .status(room.getStatus())
                .imagePath(room.getImagePath())
                .categoryName(room.getCategory().getName())
                .build();
    }

    public List<RoomResponse> toResponse(List<RoomModel> rooms) {
        return rooms.stream().map(this::toResponse).toList();
    }

    public RoomModel toEntity(RoomRequest request) {
        return RoomModel.builder()
                .id(request.getId())
                .roomNumber(request.getRoomNumber())
                .roomFloor(request.getRoomFloor())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();
    }

    public RoomResponse add(RoomRequest request) {
        var room = roomRepository.findById(String.valueOf(request.getId())).orElse(null);
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if(room == null && category != null){
            var roomEntity = toEntity(request);
            var roomSaved = roomRepository.save(roomEntity);
            roomSaved.setCategory(category);
            Long id = roomSaved.getId();
            if(request.getImagePath() != null){
                try{
                    FileUploadUtil.saveFile("/rooms/",id +".jpg", request.getImagePath());
                    roomSaved.setImagePath("/uploads/rooms/" + id +".jpg");
                    category.getRooms().add(roomSaved);
                    categoryRepository.save(category);
                    roomRepository.save(roomSaved);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return toResponse(roomSaved);
        }
        return update(request, room);
    }

    public Page<RoomResponse> get(int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return roomRepository.findAll(pageRequest).map(this::toResponse);
    }

    public List<RoomResponse> getAll(){
        return toResponse(roomRepository.findAll());
    }

    public RoomResponse getByRoomNumber(String roomNumber){
        RoomModel room = roomRepository.findByRoomNumber(roomNumber);
        return toResponse(room);
    }

    public RoomResponse update(String id, RoomRequest request){
        var room = roomRepository.findById(id).orElseThrow();
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomFloor(request.getRoomFloor());
        room.setPrice(request.getPrice());
        room.setStatus(request.getStatus());
        if(category != null){
            room.setCategory(category);
        }
        if(request.getImagePath() != null){
            try{
                String filePath = room.getImagePath();
                if(filePath != null){
                    if(filePath.startsWith("/uploads/rooms/")){
                        filePath = filePath.replace("/uploads/rooms/", "");
                        FileUploadUtil.deleteFile(filePath);
                    }
                    FileUploadUtil.saveFile("/rooms/",id + ".jpg", request.getImagePath());
                    room.setImagePath("/uploads/rooms/" + id + ".jpg");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        roomRepository.save(room);
        return toResponse(room);
    }

    public RoomResponse update(RoomRequest request, RoomModel room) {
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomFloor(request.getRoomFloor());
        room.setPrice(request.getPrice());
        room.setStatus(request.getStatus());
        roomRepository.save(room);
        return toResponse(room);
    }

    public void deleteRoomOfCategory(Long categoryId){
        List<RoomModel> rooms = roomRepository.findAllByCategory_Id(categoryId);
        roomRepository.deleteAll(rooms);
    }
}
