package model;

import java.time.LocalDate;

public class Inscriere {
    private Student student;
    private Curs curs;
    private LocalDate dataInscriere;

    // Constructor
    public Inscriere(Student student, Curs curs) {
        this.student = student;
        this.curs = curs;
        this.dataInscriere = LocalDate.now();
        curs.inscriereStudent(student); // Auto-asociere cu materia
    }

    // Getteri
    public Student getStudent() { return student; }
    public Curs getCurs() { return curs; }
    public LocalDate getDataInscriere() { return dataInscriere; }
}