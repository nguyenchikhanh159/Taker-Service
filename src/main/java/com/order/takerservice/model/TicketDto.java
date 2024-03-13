package com.order.takerservice.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TicketDto {
    private Integer ticketId;
    private BigDecimal price;
    private Integer concertId;
    private Integer availability;
}
