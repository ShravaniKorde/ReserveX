package com.reservex.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingSeatResponse {

    private String seatId;

    private String rowNumber;

    private String seatNumber;

    private String price;
}