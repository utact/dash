package com.ssafy.dash.admin.presentation.dto.request;

public record GiftLogRequest(
        Long userId,
        Integer amount,
        String reason) {
}
