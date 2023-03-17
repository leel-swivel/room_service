package com.hilltop.domain.response;

import com.hilltop.model.Room;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchRoomListResponseDto extends ResponseDto {

    private List<SearchRoomResponseDto> searchRoomList;

    public SearchRoomListResponseDto(List<Room> searchRoomList, int days) {
        var searchRoomResponseDtoList = searchRoomList
                .stream()
                .map(room -> new SearchRoomResponseDto(room, days))
                .collect(Collectors.toList());
        this.searchRoomList = searchRoomResponseDtoList;
    }

}
