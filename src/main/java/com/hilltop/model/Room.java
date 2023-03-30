package com.hilltop.model;

import com.hilltop.domain.request.RoomCreateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Room Entity
 */
@Entity
@Table(name = "room")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Room {

    @Transient
    private static final String ROOM_ID_PREFIX = "rid-";

    @Id
    private String id;
    private int roomNumber;
    private String hotelId;
    private int paxCount;
    @ManyToOne
    private RoomType roomType;
    @ElementCollection
    private List<String> imageUrls;
    private BigDecimal cost;
    private BigDecimal pricePerNight;
    private long createdAt;
    private long updatedAt;


    public Room(RoomCreateRequestDto roomCreateRequestDto, RoomType roomType) {
        this.id = ROOM_ID_PREFIX + UUID.randomUUID();
        this.roomNumber = roomCreateRequestDto.getRoomNumber();
        this.paxCount = roomCreateRequestDto.getPaxCount();
        this.hotelId = roomCreateRequestDto.getHotelId();
        this.roomType = roomType;
        this.imageUrls = roomCreateRequestDto.getImageUrls();
        this.updatedAt = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.cost = calculateRoomCost(roomCreateRequestDto.getPricePerNight(), roomType);
    }

    public void update(RoomCreateRequestDto roomCreateRequestDto, RoomType roomType) {
        this.roomNumber = roomCreateRequestDto.getRoomNumber();
        this.paxCount = roomCreateRequestDto.getPaxCount();
        this.hotelId = roomCreateRequestDto.getHotelId();
        this.roomType = roomType;
        this.imageUrls = roomCreateRequestDto.getImageUrls();
        this.updatedAt = System.currentTimeMillis();
        this.cost = calculateRoomCost(roomCreateRequestDto.getPricePerNight(), roomType);
    }

    private BigDecimal calculateRoomCost(BigDecimal pricePerNight, RoomType roomType) {
        BigDecimal markupValue = pricePerNight
                .multiply(BigDecimal.valueOf(roomType.getMarkupPercentage() / 100));
        return pricePerNight.add(markupValue);
    }
}
