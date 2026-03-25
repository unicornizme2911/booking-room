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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    public RoomResponse toResponse(RoomModel room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomFloor(room.getRoomFloor())
                .status(room.getStatus())
                .imagePath(room.getImagePath())
                .category(categoryService.toResponse(room.getCategory()))
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
                .status(request.getStatus())
                .build();
    }

    @Transactional
    public RoomResponse add(RoomRequest request) {
        var room = roomRepository.findByRoomNumber(request.getRoomNumber());
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (room != null) {
            return update(request, room);
        }

        var roomEntity = toEntity(request);
        roomEntity.setCategory(category);
        var roomSaved = roomRepository.save(roomEntity);
        Long id = roomSaved.getId();
        if(request.getImagePath() != null){
            try{
                FileUploadUtil.saveFile("/rooms/",id +".jpg", request.getImagePath());
                roomSaved.setImagePath("/uploads/rooms/" + id +".jpg");
                roomRepository.save(roomSaved);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return toResponse(roomSaved);
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

    public List<RoomResponse> searchAvailableRooms(Long categoryId, Date fromDate, Date toDate, int quantity){
        if(fromDate == null || toDate == null || fromDate.after(toDate)){
            throw new RuntimeException("Invalid date range");
        }
        return toResponse(roomRepository.findRandomAvailableRooms(categoryId, fromDate, toDate, quantity));
    }

    public RoomResponse update(String id, RoomRequest request){
        var room = roomRepository.findById(id).orElseThrow();
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomFloor(request.getRoomFloor());
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
        room.setStatus(request.getStatus());
        roomRepository.save(room);
        return toResponse(room);
    }

    public void deleteRoomOfCategory(Long categoryId){
        List<RoomModel> rooms = roomRepository.findAllByCategory_Id(categoryId);
        roomRepository.deleteAll(rooms);
    }
}
