package model;

import java.util.ArrayList;
import java.util.List;

public class Departament {
    private String nume;
    private String cod;
    private List<Profesor> profesori = new ArrayList<>();
    private List<Materie> materii = new ArrayList<>();

    // Constructor
    public Departament(String nume, String cod) {
        this.nume = nume;
        this.cod = cod;
    }

    // Metode pentru gestionarea profesorilor și materiilor
    public void adaugaProfesor(Profesor profesor) {
        profesori.add(profesor);
    }

    public void adaugaMaterie(Materie materie) {
        materii.add(materie);
    }

    // Getteri
    public String getNume() { return nume; }
    public String getCod() { return cod; }
    public List<Profesor> getProfesori() { return profesori; }
    public List<Materie> getMaterii() { return materii; }
}