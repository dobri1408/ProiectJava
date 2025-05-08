package Model;

/**
 * Clasa abstractă care reprezintă un utilizator în sistemul de catalog electronic.
 * Este moștenită de către Student și Profesor.
 */
public abstract class Utilizator {
    protected String nume;
    protected String id;

    /**
     * Constructor protejat pentru a preveni instanțierea directă.
     * @param nume Numele utilizatorului.
     * @param id ID-ul unic al utilizatorului.
     * @throws IllegalArgumentException dacă numele sau ID-ul sunt nule/goale.
     */
    protected Utilizator(String nume, String id) {
        if (nume == null || nume.trim().isEmpty()) {
            throw new IllegalArgumentException("Numele nu poate fi gol.");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID-ul nu poate fi gol.");
        }
        this.nume = nume;
        this.id = id;
    }

    // Getteri și setteri cu validări
    public String getNume() { return nume; }
    public void setNume(String nume) {
        if (nume == null || nume.trim().isEmpty()) {
            throw new IllegalArgumentException("Numele nu poate fi gol.");
        }
        this.nume = nume;
    }

    public String getId() { return id; }
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID-ul nu poate fi gol.");
        }
        this.id = id;
    }

    /**
     * Metodă abstractă care returnează rolul utilizatorului în sistem.
     * @return Rolul utilizatorului (ex: "Student", "Profesor").
     */
    public abstract String getRol();
}