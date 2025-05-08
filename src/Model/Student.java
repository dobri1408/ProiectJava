package Model;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Student extends Utilizator {
    private String email;
    private int anStudiu;
    private List<Nota> note = new ArrayList<>(); // Relație cu clasa Nota
    private List<Inscriere> inscrieri = new ArrayList<>(); // Relație cu clasa Inscriere

    public Student(String nume, String id, String email, int anStudiu) {
        super(nume, id);
        this.email = email;
        this.anStudiu = anStudiu;
    }

    @Override
    public String getRol() {
        return "Student";
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        StringBuilder sb = new StringBuilder();

        // Informații de bază
        sb.append("\n=== Foaie Matricolă ===")
                .append("\nNume: ").append(getNume())
                .append("\nID: ").append(getId())
                .append("\nEmail: ").append(email)
                .append("\nAn studiu: ").append(anStudiu)
                .append("\n\n=== Note ===");

        // Lista notelor sortate
        if (note.isEmpty()) {
            sb.append("\nNu există note înregistrate.");
        } else {
            for (Nota nota : note) {
                sb.append("\n- ")
                        .append(nota.getCurs().getMaterie().getNume())
                        .append(": ").append(nota.getValoare())
                        .append(" (")
                        .append(nota.getDataAtribuire().format(formatter))
                        .append(")");
            }
        }

        return sb.toString();
    }

    // Metode pentru gestionarea notelor și înscrierilor
    public void adaugaNota(Nota notaNoua) {
        // Găsește poziția corectă pentru a insera nota nouă (sortare crescătoare după dată)
        int index = Collections.binarySearch(
                note,
                notaNoua,
                Comparator.comparing(Nota::getDataAtribuire)
        );

        if (index < 0) {
            index = -index - 1; // Ajustare pentru inserare
        }
        note.add(index, notaNoua); // Inserare în poziția corectă
    }
    public void adaugaInscriere(Inscriere inscriere) {
        inscrieri.add(inscriere);
    }

    // Getteri și setteri
    public String getEmail() { return email; }
    public int getAnStudiu() { return anStudiu; }
    public List<Nota> getNote() { return note; }
    public List<Inscriere> getInscrieri() { return inscrieri; }
}