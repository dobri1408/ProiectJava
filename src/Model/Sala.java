package Model;

import java.util.List;

public class Sala {
    private String id;
    private String nume;
    private int capacitate;
    private List<String> facilitati;

    public Sala(String id, String nume, int capacitate, List<String> facilitati) {
        this.id = id;
        this.nume = nume;
        this.capacitate = capacitate;
        this.facilitati = facilitati;
    }

    // Getters
    public String getId() { return id; }
    public String getNume() { return nume; }
    public int getCapacitate() { return capacitate; }
    public List<String> getFacilitati() { return facilitati; }

    @Override
    public String toString() {
        return "Sala " + nume + " (ID: " + id + ")" +
                "\nCapacitate: " + capacitate +
                "\nFacilități: " + String.join(", ", facilitati);
    }
}