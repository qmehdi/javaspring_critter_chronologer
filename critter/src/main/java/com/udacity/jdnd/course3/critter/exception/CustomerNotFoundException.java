package com.udacity.jdnd.course3.critter.exception;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException()
    {}
    public CustomerNotFoundException(String message)
    {
        super(message);
    }
}
