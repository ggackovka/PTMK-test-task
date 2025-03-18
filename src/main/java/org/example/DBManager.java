package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    public static final String dbUrl = "jdbc:postgresql://localhost:5432/employeeDB";
    public static final String user = "employee_admin";
    public static final String password = "employee";
    private Connection connection;

    public DBManager() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(dbUrl, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS employees (" +
            "id SERIAL PRIMARY KEY," +
            "full_name VARCHAR(255) NOT NULL," +
            "birth_date DATE NOT NULL," +
            "gender VARCHAR(10) NOT NULL)";
        executeUpdate(sql);
        System.out.println("Employee table created");
    }

    public void saveEmployee(Employee emp) throws SQLException {
        String sql = "INSERT INTO employees (full_name, birth_date, gender) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emp.fullName);
            pstmt.setDate(2, Date.valueOf(emp.birthDate));
            pstmt.setString(3, emp.gender);
            pstmt.executeUpdate();
        }
    }

    public List<Employee> getUniqueEmployees() throws SQLException {
        String sql = "SELECT DISTINCT ON (full_name, birth_date) * FROM employees ORDER BY full_name";
        return executeQuery(sql);
    }

    public void batchInsert(List<Employee> employees) throws SQLException {
        String sql = "INSERT INTO employees (full_name, birth_date, gender) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Employee emp : employees) {
                pstmt.setString(1, emp.fullName);
                pstmt.setDate(2, Date.valueOf(emp.birthDate));
                pstmt.setString(3, emp.gender);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public List<Employee> getMaleWithF() throws SQLException {
        String sql = "SELECT * FROM employees " +
            "WHERE gender = 'Male' AND split_part(full_name, ' ', 1) LIKE 'F%'";
        return executeQuery(sql);
    }

    public void createIndex() throws SQLException {
        String sql = "CREATE INDEX idx_gender_lastname ON employees (gender, split_part(full_name, ' ', 1)) " +
            "WHERE (gender = 'Male' AND split_part(full_name, ' ', 1) LIKE 'F%')";
        executeUpdate(sql);
    }

    private List<Employee> executeQuery(String sql) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getString("full_name"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("gender")
                ));
            }
        }
        return employees;
    }

    private void executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
