package com.udacity.jdnd.course3.critter.entity;

/**
 * A example list of employee skills that could be included on an employee or a schedule request.
 */
// This is just an enum and at this point, this is not a table in the database. The table is created in the Employee entity
// It's a class containing some constants (constants could be strings and integers.
// These values can't be changed at runtime; for example, to add a new skill called sleeping, you would have to modify this class here.
public enum EmployeeSkill {
    PETTING, WALKING, FEEDING, MEDICATING, SHAVING;
}
