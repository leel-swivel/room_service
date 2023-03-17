package com.hilltop.model;

import com.hilltop.domain.request.RoomTypeCreateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * RoomType Entity
 */
@Entity
@Table(name = "room_type")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomType {

    @Transient
    private static final String ROOM_ID_PREFIX = "rtid-";

    @Id
    private String id;
    private String name;
    private BigDecimal pricePerNight;

    public RoomType(RoomTypeCreateRequestDto roomTypeCreateRequestDto) {
        this.id = ROOM_ID_PREFIX + UUID.randomUUID();
        this.name = roomTypeCreateRequestDto.getRoomType();
        this.pricePerNight = roomTypeCreateRequestDto.getPricePerNight();
    }
}
