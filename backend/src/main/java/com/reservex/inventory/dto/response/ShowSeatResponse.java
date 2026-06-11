package com.reservex.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShowSeatResponse {

    private String seatId;

    private String rowNumber;

    private String seatNumber;

    private String seatType;

    private String price;

    private String status;
}