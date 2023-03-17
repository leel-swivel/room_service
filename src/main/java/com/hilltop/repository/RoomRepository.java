package com.hilltop.repository;

import com.hilltop.model.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * RoomRepository
 */
public interface RoomRepository extends JpaRepository<Room, String> {

    Page<Room> findByHotelId(Pageable pageable, String hotelId);


    List<Room> getAllByHotelIdInAndPaxCountGreaterThanEqual(List<String> hotelIds,int paxCount);


}
