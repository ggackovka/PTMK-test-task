package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.io.ByteArrayOutputStream;

import java.io.PrintStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MainTest {
    private DBManager dbManager;
    private InputHandler inputHandler;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpassword");


    @BeforeEach
    void setUp() {
        dbManager = new DBManager(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword()
        );
        inputHandler = new InputHandler(dbManager);
        inputHandler.handleArg(new String[]{"1"});
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testMod1() throws SQLException {
        try (Connection conn = dbManager.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "employees", null);

            assertTrue(columns.next());
            assertEquals("id", columns.getString("COLUMN_NAME"));

            assertTrue(columns.next());
            assertEquals("full_name", columns.getString("COLUMN_NAME"));

            assertTrue(columns.next());
            assertEquals("birth_date", columns.getString("COLUMN_NAME"));

            assertTrue(columns.next());
            assertEquals("gender", columns.getString("COLUMN_NAME"));
        }
    }

    @Test
    void testMod2() throws SQLException {
        inputHandler.handleArg(new String[]{"2", "Ivanov Petr Sergeevich", "2000-01-01", "Male"});

        List<Employee> employees = dbManager.getAllEmployees();
        assertEquals(1, employees.size());
        assertEquals("Ivanov Petr Sergeevich", employees.get(0).fullName);
        assertEquals(25, employees.get(0).calculateAge());
        dbManager.executeUpdate("TRUNCATE TABLE employees");
    }

    @Test
    void testMod3() throws SQLException {
        dbManager.saveEmployee(new Employee("A", LocalDate.parse("2000-01-01"), "Male"));
        dbManager.saveEmployee(new Employee("A", LocalDate.parse("2000-01-01"), "Male"));
        dbManager.saveEmployee(new Employee("B", LocalDate.parse("2000-01-01"), "Female"));

        inputHandler.handleArg(new String[]{"3"});

        assertEquals(
            "A | 2000-01-01 | Male | 25\r\n" +
            "B | 2000-01-01 | Female | 25\r\n", outputStream.toString());
        dbManager.executeUpdate("TRUNCATE TABLE employees");
    }

    @Test
    void testMod4() throws SQLException {
        inputHandler.handleArg(new String[]{"4"});
        List<Employee> result = dbManager.getAllEmployees();
        assertEquals(1000100, result.size());
        dbManager.executeUpdate("TRUNCATE TABLE employees");
    }

    @Test
    void testMod5() {
        inputHandler.handleArg(new String[]{"4"});
        inputHandler.handleArg(new String[]{"5"});
        assertTrue(outputStream.toString().contains("Found 100 records in "));
    }

    @Test
    void testMod6() throws SQLException {
        inputHandler.handleArg(new String[]{"6"});

        String indexName = "idx_lastname_gender";
        String sql = "SELECT * FROM pg_indexes WHERE tablename = 'employees' AND indexname = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, indexName);
            ResultSet rs = pstmt.executeQuery();

            assertTrue(rs.next(), "Index was not created");
        }
    }
}
