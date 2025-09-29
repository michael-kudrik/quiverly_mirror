package com.quiverly.backend.exception;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String email){
        super("Email " + email + " is already taken!");
    }
}
