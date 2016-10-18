package com.blackducksoftware.integration.exception;

public class EncryptionException extends Exception {
    private static final long serialVersionUID = -4763293329821557087L;

    public EncryptionException() {
    }

    public EncryptionException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EncryptionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(final String message) {
        super(message);
    }

    public EncryptionException(final Throwable cause) {
        super(cause);
    }

}
