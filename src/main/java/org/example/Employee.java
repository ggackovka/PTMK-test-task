package org.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

public class Employee {
    String fullName;
    LocalDate birthDate;
    String gender;
    private static final Random random = new Random();
    private static final String[] MALES = {"Ivan", "Petr", "Sergey", "Fedor"};
    private static final String[] FEMALES = {"Anna", "Maria", "Olga", "Elena"};

    public Employee(String fullName, LocalDate birthDate, String gender) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public void save(DBManager db) throws SQLException {
        db.saveEmployee(this);
    }

    public int calculateAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static Employee generateRandom() {
        boolean isMale = random.nextBoolean();
        String firstName = isMale ?
            MALES[random.nextInt(MALES.length)] :
            FEMALES[random.nextInt(FEMALES.length)];
        String lastName = (char) ('A' + random.nextInt(26)) + "mith";
        return new Employee(
            lastName + " " + firstName + " " + "Olg",
            LocalDate.now().minusYears(20 + random.nextInt(40)),
            isMale ? "Male" : "Female"
        );
    }

    public static Employee generateSpecificF() {
        String firstName = MALES[random.nextInt(MALES.length)];
        return new Employee(
            "F" + (char) ('A' + random.nextInt(26)) + "omin " + firstName + " " + "ovich",
            LocalDate.now().minusYears(20 + random.nextInt(40)),
            "Male"
        );
    }

    @Override
    public String toString() {
        return fullName + " " + gender;
    }
}
