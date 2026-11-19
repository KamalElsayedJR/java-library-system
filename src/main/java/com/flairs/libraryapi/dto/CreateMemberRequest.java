package com.flairs.libraryapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
        @NotBlank(message = "name is required") @Size(max = 100) String name,
        @NotBlank(message = "email is required") @Email(message = "email is not valid") String email) {
}
