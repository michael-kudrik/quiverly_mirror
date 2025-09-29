package com.quiverly.backend.exception;


public class SurfboardNotFoundException extends RuntimeException {
    public SurfboardNotFoundException(Long id){
        super("Surfboard with id " + id + " not found!");
    }
}
