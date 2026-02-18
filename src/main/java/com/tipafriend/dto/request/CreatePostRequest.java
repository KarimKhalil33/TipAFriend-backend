package com.tipafriend.dto.request;

import com.tipafriend.model.enums.PaymentType;
import com.tipafriend.model.enums.PostCategory;
import com.tipafriend.model.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePostRequest(
        @NotNull PostType type,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull PostCategory category,
        @Size(max = 200) String locationName,
        Double latitude,
        Double longitude,
        LocalDateTime scheduledTime,
        Integer durationMinutes,
        @NotNull PaymentType paymentType,
        @NotNull @Positive BigDecimal price
) {
}
