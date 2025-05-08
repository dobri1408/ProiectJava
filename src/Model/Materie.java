package Model;

import java.util.ArrayList;
import java.util.List;

public class Materie {
    private String nume;
    private String cod;
    private int credite;

    public Materie(String nume, String cod, int credite) {
        this.nume = nume;
        this.cod = cod;
        this.credite = credite;
    }

    // Metode pentru înscrierea studenților

    // Getteri și setteri
    public String getNume() { return nume; }
    public String getCod() { return cod; }
    public int getCredite() { return credite; }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Materie: ").append(nume).append(" ");
        sb.append("Cod: ").append(cod).append(" ");
        sb.append("Credite: ").append(credite).append(" ");
       
        return sb.toString();
    }
}