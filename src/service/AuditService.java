package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static AuditService instance;
    private static final String CSV_FILE = "audit_log.csv";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor privat pentru a preveni instanțierea directă
    private AuditService() {}

    // Metodă statică pentru a obține instanța singleton
    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    /**
     * Loghează o acțiune în fișierul CSV de audit.
     * @param numeActiune Numele acțiunii (ex: "Adăugare student").
     */
    public void logActiune(String numeActiune) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String linie = String.format("%s,%s", numeActiune, timestamp);
            writer.write(linie);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Eroare la scrierea în fișierul de audit: " + e.getMessage());
        }
    }
    
    /**
     * Loghează o acțiune în fișierul CSV de audit, cu ID/cod specific.
     * @param numeActiune Numele acțiunii (ex: "Adăugare student").
     * @param id ID-ul sau codul entității (ex: "S1", "MAT1", etc.).
     */
    public void logActiune(String numeActiune, String id) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String linie = String.format("%s,%s,%s", numeActiune, id, timestamp);
            writer.write(linie);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Eroare la scrierea în fișierul de audit: " + e.getMessage());
        }
    }
}