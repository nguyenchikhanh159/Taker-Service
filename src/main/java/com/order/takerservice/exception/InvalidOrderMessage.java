package com.order.takerservice.exception;

public class InvalidOrderMessage extends RuntimeException {

    public InvalidOrderMessage(String message) {
        super(message);
    }
}

