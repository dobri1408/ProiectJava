package Repository;

import Model.Student;
import Service.AuditService;
import Service.CrudRepository;
import Service.DatabaseConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository implements CrudRepository<Student, String> {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;

    public StudentRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
    }

    @Override
    public Student save(Student student) {
        String sql = "INSERT INTO studenti (id, nume, email, an_studiu) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getNume());
            stmt.setString(3, student.getEmail());
            stmt.setInt(4, student.getAnStudiu());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Salvare student in baza de date");
            return student;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving student: " + e.getMessage());
            throw new RuntimeException("Error saving student", e);
        } finally {
            // Don't close the connection, just the statement
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public Optional<Student> findById(String id) {
        String sql = "SELECT * FROM studenti WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, id);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                        rs.getString("nume"),
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getInt("an_studiu")
                );
                
                auditService.logActiune("Cautare student dupa ID in baza de date");
                return Optional.of(student);
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error finding student by ID: " + e.getMessage());
            throw new RuntimeException("Error finding student by ID", e);
        } finally {
            // Close resources but not the connection
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing result set: " + e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM studenti";
        List<Student> students = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("nume"),
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getInt("an_studiu")
                );
                students.add(student);
            }
            
            auditService.logActiune("Listare toti studentii din baza de date");
            return students;
        } catch (SQLException e) {
            System.err.println("Error finding all students: " + e.getMessage());
            throw new RuntimeException("Error finding all students", e);
        } finally {
            // Close resources but not the connection
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing result set: " + e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public Student update(String id, Student student) {
        String sql = "UPDATE studenti SET nume = ?, email = ?, an_studiu = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, student.getNume());
            stmt.setString(2, student.getEmail());
            stmt.setInt(3, student.getAnStudiu());
            stmt.setString(4, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating student failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Actualizare student in baza de date");
            return student;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error updating student: " + e.getMessage());
            throw new RuntimeException("Error updating student", e);
        } finally {
            // Don't close the connection, just the statement
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM studenti WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere student din baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting student: " + e.getMessage());
            throw new RuntimeException("Error deleting student", e);
        } finally {
            // Don't close the connection, just the statement
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }
}