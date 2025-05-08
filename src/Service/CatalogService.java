package Service;

import Model.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalTime;

public class CatalogService {
    // Colecții pentru gestionarea datelor
    private Map<String, Student> studenti = new HashMap<>();
    private Map<String, Materie> materii = new HashMap<>();
    private Map<String, Departament> departamente = new HashMap<>();
    private Map<String, Profesor> profesori = new HashMap<>();
    private Map<String, Sala> sali = new HashMap<>();
    private Map<String, Curs> cursuri = new HashMap<>();
    private final Set<String> iduriUnice = new HashSet<>();

    public String genereazaIdDisponibil(String prefix) {
        int maxNumber = iduriUnice.stream()
                .filter(id -> id.startsWith(prefix))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(prefix.length()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compare)
                .orElse(0);

        return prefix + (maxNumber + 1);
    }
    private void valideazaId(String id) {
        if (iduriUnice.contains(id)) {
            throw new IllegalArgumentException("ID-ul " + id + " există deja!");
        }
    }

    // Metode pentru cursuri
    public void adaugaCurs(Curs curs) {
        if (!esteProfesorDisponibil(curs.getProfesor(), curs.getOraInceput(), curs.getOraSfarsit())) {
            throw new IllegalArgumentException("Profesorul este ocupat în acest interval!");
        }
        valideazaId(curs.getId());
        cursuri.put(curs.getId(), curs);
        iduriUnice.add(curs.getId());
    }

    private boolean esteProfesorDisponibil(Profesor profesor, LocalTime start, LocalTime end) {
        return profesor.getCursuri().stream()
                .noneMatch(c -> c.getOraInceput().isBefore(end) && c.getOraSfarsit().isAfter(start));
    }

    // Metode pentru studenți
    public void adaugaStudent(Student student) {
        valideazaId(student.getId());
        studenti.put(student.getId(), student);
        iduriUnice.add(student.getId());
    }
    public void adaugaSala(Sala sala) {
        valideazaId(sala.getId());
        sali.put(sala.getId(), sala);
        iduriUnice.add(sala.getId());
    }


    public List<Student> getStudenti() {
        return new ArrayList<>(studenti.values());
    }

    public Optional<Student> getStudent(String studentId) {
        return Optional.ofNullable(studenti.get(studentId));
    }

    public Optional<Sala> getSala(String salaId) {
        return Optional.ofNullable(sali.get(salaId));
    }

    /**
     * Afișează detaliile unui student după ID, inclusiv notele sortate.
     * @param studentId ID-ul studentului căutat.
     */
    public void afiseazaStudentDupaId(String studentId) {
        getStudent(studentId).ifPresentOrElse(
                student -> {
                    System.out.println("=== Foaie Matricolă ===");
                    System.out.println("Nume: " + student.getNume());
                    System.out.println("ID: " + student.getId());
                    System.out.println("Email: " + student.getEmail());
                    System.out.println("An studiu: " + student.getAnStudiu());

                    System.out.println("\n=== Note ===");
                    if (student.getNote().isEmpty()) {
                        System.out.println("Nu există note înregistrate.");
                    } else {
                        student.getNote().forEach(nota -> System.out.println(
                                "- " + nota.getCurs().getMaterie().getNume() + ": " + nota.getValoare() +
                                        " (Data: " + nota.getDataAtribuire() + ")"
                        ));
                    }
                },
                () -> System.out.println("Studentul cu ID-ul " + studentId + " nu există!")
        );
    }

    // Metode pentru materii
    public void adaugaMaterie(Materie materie) {
        if (materii.containsKey(materie.getCod())) {
            throw new IllegalArgumentException("Materia cu codul " + materie.getCod() + " există deja!");
        }

        materii.put(materie.getCod(), materie);
    }

    public Optional<Materie> getMaterie(String codMaterie) {
        return Optional.ofNullable(materii.get(codMaterie));
    }

    public Optional<Curs> getCurs(String codCurs) {
        return Optional.ofNullable(cursuri.get(codCurs));
    }


    public List<Materie> getMateriiSortate() {
        return materii.values().stream()
                .sorted(Comparator.comparing(Materie::getNume))
                .collect(Collectors.toList());
    }

    // Metode pentru profesori
    public void adaugaProfesor(Profesor profesor) {
        valideazaId(profesor.getId());
        profesori.put(profesor.getId(), profesor);
        iduriUnice.add(profesor.getId());
    }

    public Optional<Profesor> getProfesor(String profesorId) {
        return Optional.ofNullable(profesori.get(profesorId));
    }

    public Map<String, Profesor> getProfesori() {
        return new HashMap<>(profesori);
    }

    // Metode pentru înscrieri și note
    public void inscriereStudentLaMaterie(String studentId, String codCurs) {
        Student student = getStudent(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));
        Curs curs = getCurs(codCurs)
                .orElseThrow(() -> new IllegalArgumentException("Materie inexistentă!"));
        new Inscriere(student, curs);
    }

    public void adaugaNota(String studentId, String codCurs, double nota) {
        Student student = getStudent(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));
        Curs curs = getCurs(codCurs)
                .orElseThrow(() -> new IllegalArgumentException("Materie inexistentă!"));
        new Nota(student, curs, nota);
    }

    public double getMedieStudent(String studentId) {
        return getStudent(studentId)
                .map(student -> student.getNote().stream()
                        .mapToDouble(Nota::getValoare)
                        .average()
                        .orElse(0.0))
                .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));
    }

    // Metode pentru cursuri
    public List<Curs> getCursuriByStudent(String studentId) {
        return studenti.values().stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst()
                .map(student -> student.getInscrieri().stream()
                        .map(Inscriere::getCurs)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

}