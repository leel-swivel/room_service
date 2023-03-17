package com.hilltop.service;

import com.hilltop.domain.request.RoomTypeCreateRequestDto;
import com.hilltop.exception.InvalidRoomTypeException;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.RoomType;
import com.hilltop.repository.RoomTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }


    /**
     * This method used save roomType.
     *
     * @param roomTypeCreateRequestDto roomTypeCreateRequestDto
     * @return RoomType
     */
    public RoomType saveRoomType(RoomTypeCreateRequestDto roomTypeCreateRequestDto) {
        try {
            var roomType = new RoomType(roomTypeCreateRequestDto);
            log.info("Saving room type by id: {}", roomType.getId());
            return roomTypeRepository.save(roomType);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Saving room type info into database was failed.", e);
        }
    }

    /**
     * This method used to get room type by id.
     *
     * @param id id
     * @return RoomType
     */
    public RoomType getRoomType(String id) {
        try {
            Optional<RoomType> roomTypeOptional = roomTypeRepository.findById(id);
            if (roomTypeOptional.isPresent()) {
                return roomTypeOptional.get();
            } else {
                throw new InvalidRoomTypeException("Invalid room type id: " + id);
            }
        } catch (DataAccessException e) {
            throw new RoomServiceException("Getting room type info into database was failed.", e);
        }
    }
}
