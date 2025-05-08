package Model;

import java.time.LocalDate;

public class Nota {
    private Student student;
    private Curs curs;
    private double valoare;
    private LocalDate dataAtribuire;

    // Constructor
    public Nota(Student student, Curs curs, double valoare) {
        if (valoare < 1 || valoare > 10) {
            throw new IllegalArgumentException("Nota trebuie să fie între 1 și 10.");
        }
        this.student = student;
        this.curs = curs;
        this.valoare = valoare;
        this.dataAtribuire = LocalDate.now();
        student.adaugaNota(this); // Auto-asociere cu studentul
    }

    // Getteri
    public double getValoare() { return valoare; }
    public LocalDate getDataAtribuire() { return dataAtribuire; }
    public Student getStudent() { return student; }
    public Curs getCurs() { return curs; }
}