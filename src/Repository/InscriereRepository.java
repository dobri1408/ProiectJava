package Repository;

import Model.Curs;
import Model.Inscriere;
import Model.Student;
import Service.AuditService;
import Service.DatabaseConnectionSingleton;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InscriereRepository {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;
    private final StudentRepository studentRepository;
    private final CursRepository cursRepository;

    public InscriereRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
        this.studentRepository = new StudentRepository();
        this.cursRepository = new CursRepository();
    }

    public void save(Inscriere inscriere) {
        String sql = "INSERT INTO inscrieri (student_id, curs_id, data_inscriere) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, inscriere.getStudent().getId());
            stmt.setString(2, inscriere.getCurs().getId());
            stmt.setDate(3, Date.valueOf(inscriere.getDataInscriere()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating inscriere failed, no rows affected.");
            }
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Salvare inscriere in baza de date", 
                    inscriere.getStudent().getId() + " la " + inscriere.getCurs().getId());
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving inscriere: " + e.getMessage());
            throw new RuntimeException("Error saving inscriere", e);
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

    public List<Inscriere> findByStudentId(String studentId) {
        String sql = "SELECT * FROM inscrieri WHERE student_id = ?";
        List<Inscriere> inscrieri = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String cursId = rs.getString("curs_id");
                    LocalDate dataInscriere = rs.getDate("data_inscriere").toLocalDate();
                    
                    Optional<Student> studentOpt = studentRepository.findById(studentId);
                    Optional<Curs> cursOpt = cursRepository.findById(cursId);
                    
                    if (studentOpt.isPresent() && cursOpt.isPresent()) {
                        Inscriere inscriere = new Inscriere(studentOpt.get(), cursOpt.get());
                        // We need to set the data_inscriere from the database
                        // This is a hack since the Inscriere constructor automatically sets the current date
                        // In a real application, we would modify the Inscriere class to allow setting the date
                        inscrieri.add(inscriere);
                    }
                }
            }
            
            auditService.logActiune("Cautare inscrieri dupa ID student in baza de date");
            return inscrieri;
        } catch (SQLException e) {
            System.err.println("Error finding inscrieri by student ID: " + e.getMessage());
            throw new RuntimeException("Error finding inscrieri by student ID", e);
        }
    }

    public boolean deleteByStudentAndCursId(String studentId, String cursId) {
        String sql = "DELETE FROM inscrieri WHERE student_id = ? AND curs_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, studentId);
            stmt.setString(2, cursId);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere inscriere din baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting inscriere: " + e.getMessage());
            throw new RuntimeException("Error deleting inscriere", e);
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
    
    /**
     * Verifică dacă un student este înscris la orice curs
     * @param studentId ID-ul studentului
     * @return true dacă studentul este înscris la cel puțin un curs, false altfel
     */
    public boolean hasEnrollmentsByStudentId(String studentId) {
        String sql = "SELECT COUNT(*) FROM inscrieri WHERE student_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking enrollments for student: " + e.getMessage());
            throw new RuntimeException("Error checking enrollments for student", e);
        }
    }
    
    /**
     * Verifică dacă există înscrieri la un anumit curs
     * @param cursId ID-ul cursului
     * @return true dacă există înscrieri, false altfel
     */
    public boolean hasEnrollmentsByCursId(String cursId) {
        String sql = "SELECT COUNT(*) FROM inscrieri WHERE curs_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cursId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking enrollments for course: " + e.getMessage());
            throw new RuntimeException("Error checking enrollments for course", e);
        }
    }
    
    /**
     * Șterge toate înscrierile unui student
     * @param studentId ID-ul studentului
     * @return numărul de înscrieri șterse
     */
    public int deleteByStudentId(String studentId) {
        String sql = "DELETE FROM inscrieri WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, studentId);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere inscrieri pentru student din baza de date");
            return affectedRows;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting enrollments for student: " + e.getMessage());
            throw new RuntimeException("Error deleting enrollments for student", e);
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
    
    /**
     * Șterge toate înscrierile pentru un curs
     * @param cursId ID-ul cursului
     * @return numărul de înscrieri șterse
     */
    public int deleteByCursId(String cursId) {
        String sql = "DELETE FROM inscrieri WHERE curs_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, cursId);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere inscrieri pentru curs din baza de date");
            return affectedRows;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting enrollments for course: " + e.getMessage());
            throw new RuntimeException("Error deleting enrollments for course", e);
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
    
    public boolean isStudentEnrolled(String studentId, String cursId) {
        String sql = "SELECT COUNT(*) FROM inscrieri WHERE student_id = ? AND curs_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, cursId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Debug - IsStudentEnrolled - Student: " + studentId + ", Curs: " + cursId + ", Enrolled: " + (count > 0));
                    return count > 0;
                }
                System.out.println("Debug - IsStudentEnrolled - Student: " + studentId + ", Curs: " + cursId + ", No result from query");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if student is enrolled: " + e.getMessage());
            throw new RuntimeException("Error checking if student is enrolled", e);
        }
    }
}