
package com.flairs.library_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// *TODO 1: add Bean Validation annotations (with a message for each) so that:*
//   *title  : not blank, at most 120 characters*
//   *author : not blank*
//   *isbn   : not blank, exactly 13 digits  (@Pattern(regexp = "\\d{13}"))*
//   *year   : between 1450 and 2100         (@Min and @Max)*
//   *copies : zero or more                  (@PositiveOrZero)*
public record CreateBookRequest(

        @NotBlank(message = "Title must not be blank")
        @Size(max = 120, message = "Title must not exceed 120 characters")
        String title,

        @NotBlank(message = "Author must not be blank")
        String author,

        @NotBlank(message = "ISBN must not be blank")
        @Pattern(regexp = "\\d{13}", message = "ISBN must contain exactly 13 digits")
        String isbn,

        @Min(value = 1450, message = "Year must be at least 1450")
        @Max(value = 2100, message = "Year must not exceed 2100")
        int year,

        @PositiveOrZero(message = "Copies must be zero or more")
        int copies

) {
}
