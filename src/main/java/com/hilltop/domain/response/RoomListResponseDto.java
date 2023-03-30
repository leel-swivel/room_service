package com.hilltop.domain.response;

import com.hilltop.model.Room;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoomListResponseDto extends ResponseDto {

    private transient List<RoomSearchResponseDto> list;

    public RoomListResponseDto(Map<String, List<Room>> dataMap, int days) {
        list = generateResponse(dataMap, days);
    }

    private List<RoomSearchResponseDto> generateResponse(Map<String, List<Room>> dataMap, int days) {
        List<RoomSearchResponseDto> roomSearchResponseDtoList = new ArrayList<>();

        for (Map.Entry<String, List<Room>> entry : dataMap.entrySet()) {
            String id = entry.getKey();
            List<RoomResponseDto> roomsList = entry.getValue()
                    .stream()
                    .map(RoomResponseDto::new)
                    .collect(Collectors.toList());
            RoomSearchResponseDto roomSearchResponseDto = new RoomSearchResponseDto(id, roomsList ,days);
            roomSearchResponseDtoList.add(roomSearchResponseDto);
        }
        return roomSearchResponseDtoList;
    }
}
