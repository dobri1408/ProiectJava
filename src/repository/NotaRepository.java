package repository;

import model.Curs;
import model.Nota;
import model.Student;
import service.AuditService;
import service.DatabaseConnectionSingleton;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotaRepository {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;
    private final StudentRepository studentRepository;
    private final CursRepository cursRepository;

    public NotaRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
        this.studentRepository = new StudentRepository();
        this.cursRepository = new CursRepository();
    }

    public void save(Nota nota) {
        String sql = "INSERT INTO note (student_id, curs_id, valoare, data_atribuire) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, nota.getStudent().getId());
            stmt.setString(2, nota.getCurs().getId());
            stmt.setDouble(3, nota.getValoare());
            stmt.setDate(4, Date.valueOf(nota.getDataAtribuire()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating nota failed, no rows affected.");
            }
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            System.out.println("Debug - NotaRepository - Saved grade " + nota.getValoare() + 
                    " for student " + nota.getStudent().getId() + 
                    " in course " + nota.getCurs().getId());
            
            auditService.logActiune("Salvare nota in baza de date", 
                    nota.getStudent().getId() + " la " + nota.getCurs().getId() + " nota " + nota.getValoare());
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving nota: " + e.getMessage());
            throw new RuntimeException("Error saving nota", e);
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

    public List<Nota> findByStudentId(String studentId) {
        String sql = "SELECT * FROM note WHERE student_id = ? ORDER BY data_atribuire";
        List<Nota> note = new ArrayList<>();
        
        System.out.println("Debug - NotaRepository.findByStudentId - Searching grades for student: " + studentId);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    String cursId = rs.getString("curs_id");
                    double valoare = rs.getDouble("valoare");
                    LocalDate dataAtribuire = rs.getDate("data_atribuire").toLocalDate();
                    
                    System.out.println("Debug - NotaRepository - Found grade " + valoare + 
                            " for course " + cursId + 
                            " on date " + dataAtribuire);
                    
                    Optional<Student> studentOpt = studentRepository.findById(studentId);
                    Optional<Curs> cursOpt = cursRepository.findById(cursId);
                    
                    if (studentOpt.isPresent() && cursOpt.isPresent()) {
                        Nota nota = new Nota(studentOpt.get(), cursOpt.get(), valoare);
                        // Get the date from the database
                        // We need to create a special method to set the date since the constructor uses the current date
                        try {
                            // Using reflection to set the data_atribuire field
                            java.lang.reflect.Field field = Nota.class.getDeclaredField("dataAtribuire");
                            field.setAccessible(true);
                            field.set(nota, dataAtribuire);
                        } catch (Exception e) {
                            System.err.println("Error setting data_atribuire: " + e.getMessage());
                        }
                        note.add(nota);
                        System.out.println("Debug - NotaRepository - Added grade to list");
                    } else {
                        System.out.println("Debug - NotaRepository - Student or course not found: " + 
                                "Student present: " + studentOpt.isPresent() + 
                                ", Course present: " + cursOpt.isPresent());
                    }
                }
                System.out.println("Debug - NotaRepository.findByStudentId - Found " + count + " grades for student: " + studentId);
            }
            
            auditService.logActiune("Cautare note dupa ID student in baza de date");
            System.out.println("Debug - NotaRepository.findByStudentId - Returning " + note.size() + " grades");
            return note;
        } catch (SQLException e) {
            System.err.println("Error finding note by student ID: " + e.getMessage());
            throw new RuntimeException("Error finding note by student ID", e);
        }
    }
    
    public List<Nota> findByStudentAndCursId(String studentId, String cursId) {
        String sql = "SELECT * FROM note WHERE student_id = ? AND curs_id = ? ORDER BY data_atribuire";
        List<Nota> note = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, cursId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double valoare = rs.getDouble("valoare");
                    
                    Optional<Student> studentOpt = studentRepository.findById(studentId);
                    Optional<Curs> cursOpt = cursRepository.findById(cursId);
                    
                    if (studentOpt.isPresent() && cursOpt.isPresent()) {
                        Nota nota = new Nota(studentOpt.get(), cursOpt.get(), valoare);
                        // Get the date from the database
                        LocalDate dataAtribuire = rs.getDate("data_atribuire").toLocalDate();
                        // We need to create a special method to set the date since the constructor uses the current date
                        try {
                            // Using reflection to set the data_atribuire field
                            java.lang.reflect.Field field = Nota.class.getDeclaredField("dataAtribuire");
                            field.setAccessible(true);
                            field.set(nota, dataAtribuire);
                        } catch (Exception e) {
                            System.err.println("Error setting data_atribuire: " + e.getMessage());
                        }
                        note.add(nota);
                    }
                }
            }
            
            auditService.logActiune("Cautare note dupa ID student si ID curs in baza de date");
            return note;
        } catch (SQLException e) {
            System.err.println("Error finding note by student and curs ID: " + e.getMessage());
            throw new RuntimeException("Error finding note by student and curs ID", e);
        }
    }
    
    public boolean updateNota(String studentId, String cursId, double newValue) {
        String sql = "UPDATE note SET valoare = ? WHERE student_id = ? AND curs_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newValue);
            stmt.setString(2, studentId);
            stmt.setString(3, cursId);
            
            int affectedRows = stmt.executeUpdate();
            
            auditService.logActiune("Actualizare nota in baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating nota: " + e.getMessage());
            throw new RuntimeException("Error updating nota", e);
        }
    }
    
    public double getMedieStudent(String studentId) {
        String sql = "SELECT AVG(valoare) FROM note WHERE student_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
                return 0.0;
            }
        } catch (SQLException e) {
            System.err.println("Error calculating student average: " + e.getMessage());
            throw new RuntimeException("Error calculating student average", e);
        }
    }
    
    /**
     * Verifică dacă există note pentru un anumit student
     * @param studentId ID-ul studentului
     * @return true dacă există note, false altfel
     */
    public boolean hasGradesByStudentId(String studentId) {
        String sql = "SELECT COUNT(*) FROM note WHERE student_id = ?";
        
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
            System.err.println("Error checking grades for student: " + e.getMessage());
            throw new RuntimeException("Error checking grades for student", e);
        }
    }
    
    /**
     * Verifică dacă există note pentru un anumit curs
     * @param cursId ID-ul cursului
     * @return true dacă există note, false altfel
     */
    public boolean hasGradesByCursId(String cursId) {
        String sql = "SELECT COUNT(*) FROM note WHERE curs_id = ?";
        
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
            System.err.println("Error checking grades for course: " + e.getMessage());
            throw new RuntimeException("Error checking grades for course", e);
        }
    }
    
    /**
     * Șterge toate notele unui student
     * @param studentId ID-ul studentului
     * @return numărul de note șterse
     */
    public int deleteByStudentId(String studentId) {
        String sql = "DELETE FROM note WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, studentId);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere note pentru student din baza de date");
            return affectedRows;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting grades for student: " + e.getMessage());
            throw new RuntimeException("Error deleting grades for student", e);
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
     * Șterge toate notele pentru un curs
     * @param cursId ID-ul cursului
     * @return numărul de note șterse
     */
    public int deleteByCursId(String cursId) {
        String sql = "DELETE FROM note WHERE curs_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, cursId);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere note pentru curs din baza de date");
            return affectedRows;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting grades for course: " + e.getMessage());
            throw new RuntimeException("Error deleting grades for course", e);
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
     * Șterge o notă specifică
     * @param studentId ID-ul studentului
     * @param cursId ID-ul cursului
     * @param valoare Valoarea notei (opțional, dacă există mai multe note pentru același curs)
     * @return numărul de note șterse
     */
    public int deleteByStudentAndCursId(String studentId, String cursId, Double valoare) {
        String sql = valoare != null ?
                "DELETE FROM note WHERE student_id = ? AND curs_id = ? AND valoare = ?" :
                "DELETE FROM note WHERE student_id = ? AND curs_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, studentId);
            stmt.setString(2, cursId);
            if (valoare != null) {
                stmt.setDouble(3, valoare);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit transaction explicitly
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere nota specifica din baza de date");
            return affectedRows;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting specific grade: " + e.getMessage());
            throw new RuntimeException("Error deleting specific grade", e);
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