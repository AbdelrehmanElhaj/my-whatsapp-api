package dev.hdrelhaj.whatsapp.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequest(
    @NotBlank String content,
    @NotNull Long receiverId
) {}
