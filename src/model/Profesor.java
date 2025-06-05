package model;

import java.util.ArrayList;
import java.util.List;

public class Profesor extends Utilizator {
    private String titulatura;
    private List<Materie> materiiPredate = new ArrayList<>();
    private List<Curs> cursuri = new ArrayList<>();
    public Profesor(String nume, String id,  String titulatura) {
        super(nume, id);
        this.titulatura = titulatura;
    }

    @Override
    public String getRol() {
        return "Profesor";
    }

    // Metode pentru gestionarea materiilor predate
    public void adaugaMaterie(Materie materie) {
        materiiPredate.add(materie);
    }

    // Getteri și setteri

    public void adaugaCurs(Curs curs) {
        cursuri.add(curs);
    }

    // Getteri
    public List<Curs> getCursuri() {
        return cursuri;
    }


    public String getTitulatura() {
        return titulatura;
    }

    public List<Materie> getMateriiPredate() {
        return materiiPredate;
    }
}