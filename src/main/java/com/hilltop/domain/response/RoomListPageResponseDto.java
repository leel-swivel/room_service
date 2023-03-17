package com.hilltop.domain.response;

import com.hilltop.model.Room;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RoomListPageResponseDto extends PageResponseDto {

    private final List<RoomResponseDto> roomResponses;

    public RoomListPageResponseDto(Page<Room> roomPage) {
        super(roomPage);
        this.roomResponses = generateRoomResponseList(roomPage);
    }

    private List<RoomResponseDto> generateRoomResponseList(Page<Room> rooms) {
        return rooms.getContent().stream().map(RoomResponseDto::new).collect(Collectors.toList());
    }
}
