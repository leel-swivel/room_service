package com.hilltop.domain.response;

import com.hilltop.model.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * RoomCreateResponseDto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateResponseDto extends ResponseDto {
    private String id;
    private int roomNumber;
    private String hotelId;
    private int paxCount;
    private List<String> imageUrls;
    private RoomTypeResponse roomType;

    public RoomCreateResponseDto(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.hotelId = room.getHotelId();
        this.paxCount = room.getPaxCount();
        this.imageUrls = room.getImageUrls();
        this.roomType = new RoomTypeResponse(room.getRoomType().getId(), room.getRoomType().toString());
    }
}
