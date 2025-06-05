package model;



import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Curs {
    private Materie materie;
    private Profesor profesor;
    private Sala sala;
    private String id;
    private LocalTime oraInceput;
    private LocalTime oraSfarsit;
    private List<Student> studentiInscrisi = new ArrayList<>(); // Relație cu clasa Student
    // Constructor
    public Curs(String id, Materie materie, Profesor profesor, Sala sala, LocalTime oraInceput, LocalTime oraSfarsit) {
        this.materie = materie;
        this.profesor = profesor;
        this.sala = sala;
        this.oraInceput = oraInceput;
        this.oraSfarsit = oraSfarsit;
        this.id = id;
    }

    public void inscriereStudent(Student student) {
        studentiInscrisi.add(student);
    }
    public List<Student> getStudentiInscrisi() { return studentiInscrisi; }

    // Getteri și setteri
    public Materie getMaterie() {
        return materie;
    }

    public void setMaterie(Materie materie) {
        this.materie = materie;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public String getId() {
        return id;
    }


    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalTime getOraInceput() {
        return oraInceput;
    }

    public void setOraInceput(LocalTime oraInceput) {
        this.oraInceput = oraInceput;
    }

    public LocalTime getOraSfarsit() {
        return oraSfarsit;
    }

    public void setOraSfarsit(LocalTime oraSfarsit) {
        this.oraSfarsit = oraSfarsit;
    }

}