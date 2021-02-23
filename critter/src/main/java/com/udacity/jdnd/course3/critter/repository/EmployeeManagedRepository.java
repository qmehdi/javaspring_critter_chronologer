package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class EmployeeManagedRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Long> findEmployeeIdsWithAllSkillsOnDay(Set<EmployeeSkill> skillsSet, DayOfWeek dayOfWeek) {

        // skillsSet is being passed into this function and is being used in the query
        // Since skillsSet is a list, it's being converted into a comma separated string so it can be used in the Query.
        String statement = "select emp.id FROM employee AS emp, employee_skill AS emp_sk, day_of_week AS dof where emp.id = emp_sk.id AND emp.id = dof.id AND emp_sk.skill in (" +
                skillsSet
                        .stream()
                        .map((skill) -> { return String.valueOf(skill.ordinal()); })
                        .collect(Collectors.joining(",")) +
                ") AND dof.day = "  + dayOfWeek.ordinal() + " " +
                "GROUP BY emp.id HAVING count(emp_sk.skill) = " + skillsSet.size();

        // Create the query and set parameters
        Query selectQuery = entityManager.createNativeQuery(statement);

        // Execute the query
        List<BigInteger> result = selectQuery.getResultList();

        // Convert native result to Long from BigInteger to match Employee ID type.
        return result.stream()
                .map(BigInteger::longValue)
                .collect(Collectors.toList());

    }
}
