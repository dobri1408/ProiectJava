package repository;

import model.Profesor;
import service.AuditService;
import service.CrudRepository;
import service.DatabaseConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfesorRepository implements CrudRepository<Profesor, String> {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;

    public ProfesorRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
    }

    @Override
    public Profesor save(Profesor profesor) {
        String sql = "INSERT INTO profesori (id, nume, titulatura) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, profesor.getId());
            stmt.setString(2, profesor.getNume());
            stmt.setString(3, profesor.getTitulatura());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating profesor failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Salvare profesor in baza de date");
            return profesor;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving profesor: " + e.getMessage());
            throw new RuntimeException("Error saving profesor", e);
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
    public Optional<Profesor> findById(String id) {
        String sql = "SELECT * FROM profesori WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Profesor profesor = new Profesor(
                            rs.getString("nume"),
                            rs.getString("id"),
                            rs.getString("titulatura")
                    );
                    
                    auditService.logActiune("Cautare profesor dupa ID in baza de date");
                    return Optional.of(profesor);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding profesor by ID: " + e.getMessage());
            throw new RuntimeException("Error finding profesor by ID", e);
        }
    }

    @Override
    public List<Profesor> findAll() {
        String sql = "SELECT * FROM profesori";
        List<Profesor> profesors = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Profesor profesor = new Profesor(
                        rs.getString("nume"),
                        rs.getString("id"),
                        rs.getString("titulatura")
                );
                profesors.add(profesor);
            }
            
            auditService.logActiune("Listare toti profesorii din baza de date");
            return profesors;
        } catch (SQLException e) {
            System.err.println("Error finding all profesors: " + e.getMessage());
            throw new RuntimeException("Error finding all profesors", e);
        }
    }

    @Override
    public Profesor update(String id, Profesor profesor) {
        String sql = "UPDATE profesori SET nume = ?, titulatura = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profesor.getNume());
            stmt.setString(2, profesor.getTitulatura());
            stmt.setString(3, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating profesor failed, no rows affected.");
            }
            
            auditService.logActiune("Actualizare profesor in baza de date");
            return profesor;
        } catch (SQLException e) {
            System.err.println("Error updating profesor: " + e.getMessage());
            throw new RuntimeException("Error updating profesor", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM profesori WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            auditService.logActiune("Stergere profesor din baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting profesor: " + e.getMessage());
            throw new RuntimeException("Error deleting profesor", e);
        }
    }
}