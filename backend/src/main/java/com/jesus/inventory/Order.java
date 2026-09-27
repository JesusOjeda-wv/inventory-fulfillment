package com.jesus.inventory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Order(
        long id,
        long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String status,
        OffsetDateTime createdAt
){}
