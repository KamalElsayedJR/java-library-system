package com.flairs.libraryapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAuthorRequest(@NotBlank(message = "name is required") @Size(max = 100) String name) {
}
