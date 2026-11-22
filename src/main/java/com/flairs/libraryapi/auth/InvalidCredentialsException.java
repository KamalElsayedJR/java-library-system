package com.flairs.libraryapi.auth;

// One message for "no such email" and "wrong password", so attackers cannot test which emails exist
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Email or password is wrong");
    }
}
