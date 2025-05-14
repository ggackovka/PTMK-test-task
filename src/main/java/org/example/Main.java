package org.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        InputHandler inputHandler = new InputHandler(new DBManager("jdbc:postgresql://localhost:5432/employeeDB", "employee_admin", "employee"));
        inputHandler.handleArg(args);
    }
}