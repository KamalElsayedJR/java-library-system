package com.flairs.libraryapi;

import org.springframework.boot.SpringApplication;

// Run the API on a throw-away database: mvn spring-boot:test-run
public class TestLibraryApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(LibraryApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
