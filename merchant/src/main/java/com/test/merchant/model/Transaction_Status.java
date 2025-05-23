package com.test.merchant.model;

import lombok.Getter;

@Getter
public enum Transaction_Status {
    PENDING(1), SUCCESS(2), FAIL(3);

    private final int status;
    Transaction_Status(int status) {
        this.status = status;
    }

}
