package repository;

import model.Materie;
import service.AuditService;
import service.CrudRepository;
import service.DatabaseConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterieRepository implements CrudRepository<Materie, String> {
    private final DatabaseConnectionSingleton dbConnection;
    private final AuditService auditService;

    public MaterieRepository() {
        this.dbConnection = DatabaseConnectionSingleton.getInstance();
        this.auditService = AuditService.getInstance();
    }

    @Override
    public Materie save(Materie materie) {
        String sql = "INSERT INTO materii (cod, nume, credite) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, materie.getCod());
            stmt.setString(2, materie.getNume());
            stmt.setInt(3, materie.getCredite());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating materie failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Salvare materie in baza de date");
            return materie;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error saving materie: " + e.getMessage());
            throw new RuntimeException("Error saving materie", e);
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
    public Optional<Materie> findById(String cod) {
        String sql = "SELECT * FROM materii WHERE cod = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, cod);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                Materie materie = new Materie(
                        rs.getString("nume"),
                        rs.getString("cod"),
                        rs.getInt("credite")
                );
                
                auditService.logActiune("Cautare materie dupa cod in baza de date");
                return Optional.of(materie);
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error finding materie by cod: " + e.getMessage());
            throw new RuntimeException("Error finding materie by cod", e);
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
    public List<Materie> findAll() {
        String sql = "SELECT * FROM materii";
        List<Materie> materii = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Materie materie = new Materie(
                        rs.getString("nume"),
                        rs.getString("cod"),
                        rs.getInt("credite")
                );
                materii.add(materie);
            }
            
            auditService.logActiune("Listare toate materiile din baza de date");
            return materii;
        } catch (SQLException e) {
            System.err.println("Error finding all materii: " + e.getMessage());
            throw new RuntimeException("Error finding all materii", e);
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
    public Materie update(String cod, Materie materie) {
        String sql = "UPDATE materii SET nume = ?, credite = ? WHERE cod = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, materie.getNume());
            stmt.setInt(2, materie.getCredite());
            stmt.setString(3, cod);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating materie failed, no rows affected.");
            }
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Actualizare materie in baza de date");
            return materie;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error updating materie: " + e.getMessage());
            throw new RuntimeException("Error updating materie", e);
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
    public boolean deleteById(String cod) {
        String sql = "DELETE FROM materii WHERE cod = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, cod);
            
            int affectedRows = stmt.executeUpdate();
            
            // Commit the transaction
            dbConnection.commitTransaction();
            
            auditService.logActiune("Stergere materie din baza de date");
            return affectedRows > 0;
        } catch (SQLException e) {
            // Rollback in case of error
            dbConnection.rollbackTransaction();
            System.err.println("Error deleting materie: " + e.getMessage());
            throw new RuntimeException("Error deleting materie", e);
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