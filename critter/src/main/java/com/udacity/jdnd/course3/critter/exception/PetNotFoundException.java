package com.udacity.jdnd.course3.critter.exception;

public class PetNotFoundException extends Throwable
{
    public PetNotFoundException()
    {

    }
    public PetNotFoundException(String message)
    {
        super(message);
    }
}
