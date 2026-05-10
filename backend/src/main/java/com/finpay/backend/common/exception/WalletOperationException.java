package com.finpay.backend.common.exception;

public class WalletOperationException
        extends RuntimeException {

    public WalletOperationException(
            String message
    ) {
        super(message);
    }
}