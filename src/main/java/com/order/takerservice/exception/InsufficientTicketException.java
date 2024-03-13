package com.order.takerservice.exception;

public class InsufficientTicketException extends RuntimeException {

    public InsufficientTicketException(String message) {
        super(message);
    }

}
