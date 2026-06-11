package com.reservex.inventory.mapper;

import com.reservex.inventory.dto.response.ShowSeatResponse;
import com.reservex.inventory.entity.ShowSeat;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public ShowSeatResponse toResponse(ShowSeat seat) {

        return ShowSeatResponse.builder()
                .seatId(seat.getId().toString())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .price(seat.getPrice().toString())
                .status(seat.getStatus().name())
                .build();
    }
}