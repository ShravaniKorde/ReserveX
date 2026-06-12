package com.reservex.inventory.mapper;

import com.reservex.inventory.dto.response.ShowSeatResponse;
import com.reservex.inventory.entity.ShowSeat;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public ShowSeatResponse toResponse(ShowSeat seat) {

        return ShowSeatResponse.builder()
                .seatId(
                        seat.getSeat()
                                .getId()
                                .toString()
                )
                .rowNumber(seat.getSeat().getRowNumber())
                .seatNumber(seat.getSeat().getSeatNumber())
                .seatType(seat.getSeat().getSeatType())
                .price(seat.getPrice().toString())
                .status(seat.getStatus().name())
                .build();
    }
}