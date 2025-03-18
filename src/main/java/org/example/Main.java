package org.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar PTMK.jar <mode>");
            return;
        }

        int mode = Integer.parseInt(args[0]);

        DBManager dbManager = new DBManager();

        try {
            switch (mode) {
                case 1 -> dbManager.createTable();
                case 2 -> handleMode2(args, dbManager);
                case 3 -> handleMode3(dbManager);
                case 4 -> handleMode4(dbManager);
                case 5 -> handleMode5(dbManager);
                case 6 -> handleMode6(dbManager);
                default -> System.out.println("Invalid mode");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbManager.close();
        }
    }

    private static void handleMode2(String[] args, DBManager dbManager) throws SQLException {
        if (args.length < 4) {
            System.out.println("Invalid arguments for mode 2");
            return;
        }
        Employee emp = new Employee(
            args[1].replaceAll("\"", ""),
            LocalDate.parse(args[2]),
            args[3]
        );
        emp.save(dbManager);
        System.out.println("Employee added");
    }

    private static void handleMode3(DBManager dbManager) throws SQLException {
        List<Employee> employees = dbManager.getUniqueEmployees();
        employees.forEach(emp -> System.out.println(
            emp.fullName + " | " + emp.birthDate + " | " +
                emp.gender + " | " + emp.calculateAge()
        ));
    }

    private static void handleMode4(DBManager dbManager) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        // Generate 1,000,000 random employees
        for (int i = 0; i < 1000000; i++) {
            employees.add(Employee.generateRandom());
        }
        // Add 100 specific employees
        for (int i = 0; i < 100; i++) {
            employees.add(Employee.generateSpecificF());
        }
        dbManager.batchInsert(employees);
        System.out.println("Employees added");
    }

    private static void handleMode5(DBManager dbManager) throws SQLException {
        long startTime = System.nanoTime();
        List<Employee> result = dbManager.getMaleWithF();
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        System.out.println("Found " + result.size() + " records in " + duration + " ms");
//        for (Employee e: result) {
//            System.out.println(e);
//        }
    }

    private static void handleMode6(DBManager dbManager) throws SQLException {
        dbManager.createIndex();
        System.out.println("Index created");
    }
}