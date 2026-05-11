package com.finpay.backend.common.exception;

public class InsufficientBalanceException
        extends RuntimeException {

    public InsufficientBalanceException(
            String message
    ) {
        super(message);
    }
}