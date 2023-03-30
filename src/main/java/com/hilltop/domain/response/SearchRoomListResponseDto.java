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

    public SearchRoomListResponseDto(List<Room> searchRoomList) {
        var searchRoomResponseDtoList = searchRoomList
                .stream()
                .map(SearchRoomResponseDto::new)
                .collect(Collectors.toList());
        this.searchRoomList = searchRoomResponseDtoList;
    }

}
