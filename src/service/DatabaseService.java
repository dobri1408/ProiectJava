package service;

import model.*;
import repository.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class DatabaseService {
    private static DatabaseService instance;
    
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;
    
    // Repositories made public for access by CatalogService
    public final StudentRepository studentRepository;
    public final ProfesorRepository profesorRepository;
    public final MaterieRepository materieRepository;
    public final SalaRepository salaRepository;
    public final CursRepository cursRepository;
    public final InscriereRepository inscriereRepository;
    public final NotaRepository notaRepository;
    
    // Register a shutdown hook to ensure the connection is closed when the JVM exits
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.closeConnection();
                System.out.println("Database service shutdown hook: connection closed");
            }
        }));
    }
    
    private DatabaseService() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
        
        this.studentRepository = new StudentRepository();
        this.profesorRepository = new ProfesorRepository();
        this.materieRepository = new MaterieRepository();
        this.salaRepository = new SalaRepository();
        this.cursRepository = new CursRepository();
        this.inscriereRepository = new InscriereRepository();
        this.notaRepository = new NotaRepository();
        
        initDatabase();
    }
    
    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    private void initDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            // Folosim scriptul pentru PostgreSQL
            executeSqlScript(conn, "src/resources/init_db.sql");
            
            auditService.logActiune("Initializare baza de date");
        } catch (SQLException | IOException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    private void executeSqlScript(Connection conn, String scriptPath) throws SQLException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptPath));
             Statement stmt = conn.createStatement()) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignoră comentariile și liniile goale
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                
                sb.append(line);
                
                // Dacă linia se termină cu ";", atunci execută comanda SQL
                if (line.trim().endsWith(";")) {
                    stmt.execute(sb.toString());
                    sb.setLength(0);
                }
            }
            
            // Execută ultima comandă dacă există
            if (sb.length() > 0) {
                stmt.execute(sb.toString());
            }
        }
    }
    
    
    // Student methods
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    public Optional<Student> getStudent(String id) {
        return studentRepository.findById(id);
    }
    
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Student updateStudent(String id, Student student) {
        return studentRepository.update(id, student);
    }
    
    public boolean deleteStudent(String id) {
        return studentRepository.deleteById(id);
    }
    
    // Profesor methods
    public Profesor saveProfesor(Profesor profesor) {
        return profesorRepository.save(profesor);
    }
    
    public Optional<Profesor> getProfesor(String id) {
        return profesorRepository.findById(id);
    }
    
    public List<Profesor> getAllProfesors() {
        return profesorRepository.findAll();
    }
    
    public Profesor updateProfesor(String id, Profesor profesor) {
        return profesorRepository.update(id, profesor);
    }
    
    public boolean deleteProfesor(String id) {
        return profesorRepository.deleteById(id);
    }
    
    // Materie methods
    public Materie saveMaterie(Materie materie) {
        return materieRepository.save(materie);
    }
    
    public Optional<Materie> getMaterie(String cod) {
        return materieRepository.findById(cod);
    }
    
    public List<Materie> getAllMaterii() {
        return materieRepository.findAll();
    }
    
    public Materie updateMaterie(String cod, Materie materie) {
        return materieRepository.update(cod, materie);
    }
    
    public boolean deleteMaterie(String cod) {
        return materieRepository.deleteById(cod);
    }
    
    // Sala methods
    public Sala saveSala(Sala sala) {
        return salaRepository.save(sala);
    }
    
    public Optional<Sala> getSala(String id) {
        return salaRepository.findById(id);
    }
    
    public List<Sala> getAllSali() {
        return salaRepository.findAll();
    }
    
    public Sala updateSala(String id, Sala sala) {
        return salaRepository.update(id, sala);
    }
    
    public boolean deleteSala(String id) {
        return salaRepository.deleteById(id);
    }
    
    // Curs methods
    public Curs saveCurs(Curs curs) {
        return cursRepository.save(curs);
    }
    
    public Optional<Curs> getCurs(String id) {
        return cursRepository.findById(id);
    }
    
    public List<Curs> getAllCursuri() {
        return cursRepository.findAll();
    }
    
    public Curs updateCurs(String id, Curs curs) {
        return cursRepository.update(id, curs);
    }
    
    public boolean deleteCurs(String id) {
        return cursRepository.deleteById(id);
    }
    
    // Inscriere methods
    public void saveInscriere(Inscriere inscriere) {
        inscriereRepository.save(inscriere);
    }
    
    public List<Inscriere> getInscrieriByStudent(String studentId) {
        return inscriereRepository.findByStudentId(studentId);
    }
    
    public boolean deleteInscriere(String studentId, String cursId) {
        return inscriereRepository.deleteByStudentAndCursId(studentId, cursId);
    }
    
    public boolean isStudentEnrolled(String studentId, String cursId) {
        return inscriereRepository.isStudentEnrolled(studentId, cursId);
    }
    
    // Nota methods
    public void saveNota(Nota nota) {
        notaRepository.save(nota);
    }
    
    public List<Nota> getNoteByStudent(String studentId) {
        return notaRepository.findByStudentId(studentId);
    }
    
    public List<Nota> getNoteByStudentAndCurs(String studentId, String cursId) {
        return notaRepository.findByStudentAndCursId(studentId, cursId);
    }
    
    public boolean updateNota(String studentId, String cursId, double newValue) {
        return notaRepository.updateNota(studentId, cursId, newValue);
    }
    
    public double getMedieStudent(String studentId) {
        return notaRepository.getMedieStudent(studentId);
    }
    
    // Course by student
    public List<Curs> getCursuriByStudent(String studentId) {
        List<Curs> cursuri = cursRepository.findByStudentId(studentId);
        System.out.println("Debug - DatabaseService - Cursuri pentru student " + studentId + ": " + cursuri.size());
        return cursuri;
    }
    
    // Close database connection
    public void closeConnection() {
        dbConnection.closeConnection();
    }
}