package repository;

import model.Sala;
import service.AuditService;
import service.CrudRepository;
import service.DatabaseConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SalaRepository implements CrudRepository<Sala, String> {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;

    public SalaRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
    }

    @Override
    public Sala save(Sala sala) {
        String sql = "INSERT INTO sali (id, nume, capacitate, facilitati) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, sala.getId());
            stmt.setString(2, sala.getNume());
            stmt.setInt(3, sala.getCapacitate());
            stmt.setString(4, String.join(",", sala.getFacilitati()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating sala failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Salvare sala in baza de date");
            return sala;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving sala: " + e.getMessage());
            throw new RuntimeException("Error saving sala", e);
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
    public Optional<Sala> findById(String id) {
        String sql = "SELECT * FROM sali WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    List<String> facilitati = Arrays.asList(rs.getString("facilitati").split(","));
                    
                    Sala sala = new Sala(
                            rs.getString("id"),
                            rs.getString("nume"),
                            rs.getInt("capacitate"),
                            facilitati
                    );
                    
                    auditService.logActiune("Cautare sala dupa ID in baza de date");
                    return Optional.of(sala);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error finding sala by ID: " + e.getMessage());
            throw new RuntimeException("Error finding sala by ID", e);
        }
    }

    @Override
    public List<Sala> findAll() {
        String sql = "SELECT * FROM sali";
        List<Sala> sali = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                List<String> facilitati = Arrays.asList(rs.getString("facilitati").split(","));
                
                Sala sala = new Sala(
                        rs.getString("id"),
                        rs.getString("nume"),
                        rs.getInt("capacitate"),
                        facilitati
                );
                sali.add(sala);
            }
            
            auditService.logActiune("Listare toate salile din baza de date");
            return sali;
        } catch (SQLException e) {
            System.err.println("Error finding all sali: " + e.getMessage());
            throw new RuntimeException("Error finding all sali", e);
        }
    }

    @Override
    public Sala update(String id, Sala sala) {
        String sql = "UPDATE sali SET nume = ?, capacitate = ?, facilitati = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sala.getNume());
            stmt.setInt(2, sala.getCapacitate());
            stmt.setString(3, String.join(",", sala.getFacilitati()));
            stmt.setString(4, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating sala failed, no rows affected.");
            }
            
            auditService.logActiune("Actualizare sala in baza de date");
            return sala;
        } catch (SQLException e) {
            System.err.println("Error updating sala: " + e.getMessage());
            throw new RuntimeException("Error updating sala", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM sali WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            auditService.logActiune("Stergere sala din baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting sala: " + e.getMessage());
            throw new RuntimeException("Error deleting sala", e);
        }
    }
}