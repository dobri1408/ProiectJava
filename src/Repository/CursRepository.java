package Repository;

import Model.Curs;
import Model.Materie;
import Model.Profesor;
import Model.Sala;
import Model.Student;
import Service.AuditService;
import Service.CrudRepository;
import Service.DatabaseConnectionSingleton;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursRepository implements CrudRepository<Curs, String> {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;
    private final MaterieRepository materieRepository;
    private final ProfesorRepository profesorRepository;
    private final SalaRepository salaRepository;
    private final StudentRepository studentRepository;

    public CursRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
        this.materieRepository = new MaterieRepository();
        this.profesorRepository = new ProfesorRepository();
        this.salaRepository = new SalaRepository();
        this.studentRepository = new StudentRepository();
    }

    @Override
    public Curs save(Curs curs) {
        String sql = "INSERT INTO cursuri (id, materie_cod, profesor_id, sala_id, ora_inceput, ora_sfarsit) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, curs.getId());
            stmt.setString(2, curs.getMaterie().getCod());
            stmt.setString(3, curs.getProfesor().getId());
            stmt.setString(4, curs.getSala().getId());
            stmt.setTime(5, Time.valueOf(curs.getOraInceput()));
            stmt.setTime(6, Time.valueOf(curs.getOraSfarsit()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating curs failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Salvare curs in baza de date");
            return curs;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving curs: " + e.getMessage());
            throw new RuntimeException("Error saving curs", e);
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
    public Optional<Curs> findById(String id) {
        String sql = "SELECT * FROM cursuri WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String materieCod = rs.getString("materie_cod");
                    String profesorId = rs.getString("profesor_id");
                    String salaId = rs.getString("sala_id");
                    
                    Materie materie = materieRepository.findById(materieCod)
                            .orElseThrow(() -> new SQLException("Referenced materie not found"));
                    
                    Profesor profesor = profesorRepository.findById(profesorId)
                            .orElseThrow(() -> new SQLException("Referenced profesor not found"));
                    
                    Sala sala = salaRepository.findById(salaId)
                            .orElseThrow(() -> new SQLException("Referenced sala not found"));
                    
                    LocalTime oraInceput = rs.getTime("ora_inceput").toLocalTime();
                    LocalTime oraSfarsit = rs.getTime("ora_sfarsit").toLocalTime();
                    
                    Curs curs = new Curs(
                            rs.getString("id"),
                            materie,
                            profesor,
                            sala,
                            oraInceput,
                            oraSfarsit
                    );
                    
                    // Populăm lista de studenți înscriși la acest curs
                    populateEnrolledStudents(curs);
                    
                    auditService.logActiune("Cautare curs dupa ID in baza de date");
                    return Optional.of(curs);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding curs by ID: " + e.getMessage());
            throw new RuntimeException("Error finding curs by ID", e);
        }
    }

    @Override
    public List<Curs> findAll() {
        String sql = "SELECT * FROM cursuri";
        List<Curs> cursuri = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String materieCod = rs.getString("materie_cod");
                String profesorId = rs.getString("profesor_id");
                String salaId = rs.getString("sala_id");
                
                Optional<Materie> materieOpt = materieRepository.findById(materieCod);
                Optional<Profesor> profesorOpt = profesorRepository.findById(profesorId);
                Optional<Sala> salaOpt = salaRepository.findById(salaId);
                
                if (materieOpt.isPresent() && profesorOpt.isPresent() && salaOpt.isPresent()) {
                    LocalTime oraInceput = rs.getTime("ora_inceput").toLocalTime();
                    LocalTime oraSfarsit = rs.getTime("ora_sfarsit").toLocalTime();
                    
                    Curs curs = new Curs(
                            rs.getString("id"),
                            materieOpt.get(),
                            profesorOpt.get(),
                            salaOpt.get(),
                            oraInceput,
                            oraSfarsit
                    );
                    
                    // Populăm lista de studenți înscriși la acest curs
                    populateEnrolledStudents(curs);
                    
                    cursuri.add(curs);
                }
            }
            
            auditService.logActiune("Listare toate cursurile din baza de date");
            return cursuri;
        } catch (SQLException e) {
            System.err.println("Error finding all cursuri: " + e.getMessage());
            throw new RuntimeException("Error finding all cursuri", e);
        }
    }

    @Override
    public Curs update(String id, Curs curs) {
        String sql = "UPDATE cursuri SET materie_cod = ?, profesor_id = ?, sala_id = ?, ora_inceput = ?, ora_sfarsit = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, curs.getMaterie().getCod());
            stmt.setString(2, curs.getProfesor().getId());
            stmt.setString(3, curs.getSala().getId());
            stmt.setTime(4, Time.valueOf(curs.getOraInceput()));
            stmt.setTime(5, Time.valueOf(curs.getOraSfarsit()));
            stmt.setString(6, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating curs failed, no rows affected.");
            }
            
            auditService.logActiune("Actualizare curs in baza de date");
            return curs;
        } catch (SQLException e) {
            System.err.println("Error updating curs: " + e.getMessage());
            throw new RuntimeException("Error updating curs", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM cursuri WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            auditService.logActiune("Stergere curs din baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting curs: " + e.getMessage());
            throw new RuntimeException("Error deleting curs", e);
        }
    }
    
    /**
     * Populează lista de studenți înscriși la un curs
     * @param curs Cursul pentru care se populează lista de studenți
     */
    private void populateEnrolledStudents(Curs curs) {
        String sql = "SELECT s.* FROM studenti s " +
                     "JOIN inscrieri i ON s.id = i.student_id " +
                     "WHERE i.curs_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, curs.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("id");
                    Optional<Student> studentOpt = studentRepository.findById(studentId);
                    
                    studentOpt.ifPresent(student -> {
                        // Adăugăm studentul la lista de studenți înscriși la curs
                        // Verificăm mai întâi dacă studentul nu există deja în listă pentru a evita duplicatele
                        boolean exists = curs.getStudentiInscrisi().stream()
                                .anyMatch(s -> s.getId().equals(student.getId()));
                        
                        if (!exists) {
                            curs.inscriereStudent(student);
                        }
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error populating enrolled students for curs: " + e.getMessage());
            // Nu aruncăm excepție pentru a nu întrerupe fluxul principal
        }
    }

    public List<Curs> findByStudentId(String studentId) {
        String sql = "SELECT c.* FROM cursuri c " +
                     "JOIN inscrieri i ON c.id = i.curs_id " +
                     "WHERE i.student_id = ?";
        List<Curs> cursuri = new ArrayList<>();
        
        System.out.println("Debug - CursRepository.findByStudentId - Executing query for student: " + studentId);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int rowCount = 0;
                
                while (rs.next()) {
                    rowCount++;
                    String cursId = rs.getString("id");
                    System.out.println("Debug - Found course: " + cursId + " for student: " + studentId);
                    
                    String materieCod = rs.getString("materie_cod");
                    String profesorId = rs.getString("profesor_id");
                    String salaId = rs.getString("sala_id");
                    
                    Optional<Materie> materieOpt = materieRepository.findById(materieCod);
                    Optional<Profesor> profesorOpt = profesorRepository.findById(profesorId);
                    Optional<Sala> salaOpt = salaRepository.findById(salaId);
                    
                    if (materieOpt.isPresent() && profesorOpt.isPresent() && salaOpt.isPresent()) {
                        LocalTime oraInceput = rs.getTime("ora_inceput").toLocalTime();
                        LocalTime oraSfarsit = rs.getTime("ora_sfarsit").toLocalTime();
                        
                        Curs curs = new Curs(
                                cursId,
                                materieOpt.get(),
                                profesorOpt.get(),
                                salaOpt.get(),
                                oraInceput,
                                oraSfarsit
                        );
                        
                        // Obținem studentul și îl adăugăm la lista de studenți înscriși la curs
                        studentRepository.findById(studentId).ifPresent(student -> {
                            curs.inscriereStudent(student);
                            System.out.println("Debug - Added student " + studentId + " to course " + cursId);
                        });
                        
                        // Populăm și restul studenților înscriși la acest curs
                        populateEnrolledStudents(curs);
                        
                        cursuri.add(curs);
                    }
                }
                
                System.out.println("Debug - CursRepository.findByStudentId - Found " + rowCount + " rows for student: " + studentId);
            }
            
            auditService.logActiune("Cautare cursuri dupa ID student in baza de date");
            return cursuri;
        } catch (SQLException e) {
            System.err.println("Error finding cursuri by student ID: " + e.getMessage());
            throw new RuntimeException("Error finding cursuri by student ID", e);
        }
    }
}