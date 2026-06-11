package com.reservex.inventory.dto.response;

import com.reservex.inventory.enums.SeatType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShowSeatResponse {

    private String seatId;

    private String rowNumber;

    private String seatNumber;

    private SeatType seatType;

    private String price;

    private String status;
}