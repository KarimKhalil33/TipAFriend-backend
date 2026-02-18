package com.tipafriend.dto.request;

import com.tipafriend.model.enums.PaymentType;
import com.tipafriend.model.enums.PostCategory;
import com.tipafriend.model.enums.PostType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePostRequest(
        PostType type,
        String title,
        String description,
        PostCategory category,
        String locationName,
        Double latitude,
        Double longitude,
        LocalDateTime scheduledTime,
        Integer durationMinutes,
        PaymentType paymentType,
        BigDecimal price
) {
}

