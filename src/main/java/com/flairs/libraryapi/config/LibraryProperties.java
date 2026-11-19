package com.flairs.libraryapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Bound from the "library:" block in application.yml
@ConfigurationProperties(prefix = "library")
public record LibraryProperties(int maxActiveLoans, int loanDays) {
}
