package com.tipafriend.dto.request;

import com.tipafriend.model.enums.PaymentType;
import com.tipafriend.model.enums.PostCategory;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdatePostRequest(
        @Size(max = 200) String title,
        @Size(max = 2000) String description,
        PostCategory category,
        @Size(max = 200) String locationName,
        Double latitude,
        Double longitude,
        LocalDateTime scheduledTime,
        Integer durationMinutes,
        PaymentType paymentType,
        @Positive BigDecimal price
) {
}
