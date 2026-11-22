package com.flairs.libraryapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// A librarian creates the member with a first password. New members are always MEMBER.
public record CreateMemberRequest(
        @NotBlank(message = "name is required") @Size(max = 100) String name,
        @NotBlank(message = "email is required") @Email(message = "email is not valid") @Size(max = 120) String email,
        @NotBlank(message = "password is required") @Size(min = 8, max = 72, message = "password must be 8 to 72 characters")
        String password) {

    // Records print every field in toString(). Keep the password out of logs.
    @Override
    public String toString() {
        return "CreateMemberRequest[name=" + name + ", email=" + email + "]";
    }
}
