package service;

import model.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.Arrays;

public class CatalogService {
    // Colecții pentru gestionarea datelor în memorie
    private Map<String, Student> studenti = new HashMap<>();
    private Map<String, Materie> materii = new HashMap<>();
    private Map<String, Profesor> profesori = new HashMap<>();
    private Map<String, Sala> sali = new HashMap<>();
    private Map<String, Curs> cursuri = new HashMap<>();
    private final Set<String> iduriUnice = new HashSet<>();
    
    // Servicii pentru persistență
    public final DatabaseService dbService;
    private final AuditService auditService;
    
    // Register a shutdown hook to ensure proper cleanup when the application exits
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down, ensuring database is properly closed...");
            // DatabaseService has its own shutdown hook that will handle the connection closing
        }));
    }
    
    public CatalogService() {
        this.dbService = DatabaseService.getInstance();
        this.auditService = AuditService.getInstance();
        
        // Încărcăm datele din baza de date în memorie
        incarcaDateDinBD();
    }
    
    private void incarcaDateDinBD() {
        // Încarcă studenții
        dbService.getAllStudents().forEach(student -> {
            studenti.put(student.getId(), student);
            iduriUnice.add(student.getId());
        });
        
        // Încarcă profesorii
        dbService.getAllProfesors().forEach(profesor -> {
            profesori.put(profesor.getId(), profesor);
            iduriUnice.add(profesor.getId());
        });
        
        // Încarcă materiile
        dbService.getAllMaterii().forEach(materie -> {
            materii.put(materie.getCod(), materie);
            iduriUnice.add(materie.getCod());
        });
        
        // Încarcă sălile
        dbService.getAllSali().forEach(sala -> {
            sali.put(sala.getId(), sala);
            iduriUnice.add(sala.getId());
        });
        
        // Încarcă cursurile
        dbService.getAllCursuri().forEach(curs -> {
            cursuri.put(curs.getId(), curs);
            iduriUnice.add(curs.getId());
        });
        
        // Dacă nu există date, populăm baza de date cu date demo
        if (studenti.isEmpty() && profesori.isEmpty() && materii.isEmpty() && sali.isEmpty()) {
            System.out.println("Baza de date este goală. Se populează cu date demo...");
            populateDemoData();
        }
    }
    
    /**
     * Populează baza de date cu date demo - studenți, profesori, săli, materii, cursuri
     */
    public void populateDemoData() {
        // Adăugăm săli
        List<String> facilitatiBasic = Arrays.asList("Proiector", "Tablă");
        List<String> facilitatiPremium = Arrays.asList("Proiector", "Tablă interactivă", "Computere", "Aer condiționat");
        
        Sala sala1 = new Sala("R1", "Sala 101", 30, facilitatiBasic);
        Sala sala2 = new Sala("R2", "Sala 102", 25, facilitatiBasic);
        Sala sala3 = new Sala("R3", "Laborator Informatică", 20, facilitatiPremium);
        Sala sala4 = new Sala("R4", "Amfiteatru A1", 100, facilitatiBasic);
        Sala sala5 = new Sala("R5", "Laborator Fizică", 25, facilitatiPremium);
        
        adaugaSala(sala1);
        adaugaSala(sala2);
        adaugaSala(sala3);
        adaugaSala(sala4);
        adaugaSala(sala5);
        
        // Adăugăm profesori
        Profesor prof1 = new Profesor("Alexandru Popescu", "P1", "Prof. Dr.");
        Profesor prof2 = new Profesor("Maria Ionescu", "P2", "Conf. Dr.");
        Profesor prof3 = new Profesor("Ion Dumitrescu", "P3", "Lect. Dr.");
        Profesor prof4 = new Profesor("Elena Vasilescu", "P4", "Prof. Dr.");
        Profesor prof5 = new Profesor("Mihai Georgescu", "P5", "Asist. Univ.");
        
        adaugaProfesor(prof1);
        adaugaProfesor(prof2);
        adaugaProfesor(prof3);
        adaugaProfesor(prof4);
        adaugaProfesor(prof5);
        
        // Adăugăm materii
        Materie mat1 = new Materie("Algebra liniară", "MAT1", 5);
        Materie mat2 = new Materie("Analiză matematică", "MAT2", 6);
        Materie mat3 = new Materie("Programare orientată pe obiecte", "POO1", 6);
        Materie mat4 = new Materie("Structuri de date", "SD1", 5);
        Materie mat5 = new Materie("Baze de date", "BD1", 5);
        
        adaugaMaterie(mat1);
        adaugaMaterie(mat2);
        adaugaMaterie(mat3);
        adaugaMaterie(mat4);
        adaugaMaterie(mat5);
        
        // Adăugăm cursuri
        Curs curs1 = new Curs("CRS1", mat1, prof1, sala4, LocalTime.of(8, 0), LocalTime.of(10, 0));
        Curs curs2 = new Curs("CRS2", mat2, prof2, sala4, LocalTime.of(10, 0), LocalTime.of(12, 0));
        Curs curs3 = new Curs("CRS3", mat3, prof3, sala3, LocalTime.of(12, 0), LocalTime.of(14, 0));
        Curs curs4 = new Curs("CRS4", mat4, prof4, sala3, LocalTime.of(14, 0), LocalTime.of(16, 0));
        Curs curs5 = new Curs("CRS5", mat5, prof5, sala3, LocalTime.of(16, 0), LocalTime.of(18, 0));
        
        adaugaCurs(curs1);
        adaugaCurs(curs2);
        adaugaCurs(curs3);
        adaugaCurs(curs4);
        adaugaCurs(curs5);
        
        // Adăugăm studenți
        Student stud1 = new Student("Andrei Popa", "S1", "andrei.popa@stud.univ.ro", 1);
        Student stud2 = new Student("Ana Maria Dobre", "S2", "ana.dobre@stud.univ.ro", 1);
        Student stud3 = new Student("Cristian Munteanu", "S3", "cristian.munteanu@stud.univ.ro", 2);
        Student stud4 = new Student("Diana Stanciu", "S4", "diana.stanciu@stud.univ.ro", 2);
        Student stud5 = new Student("Florin Neagu", "S5", "florin.neagu@stud.univ.ro", 3);
        Student stud6 = new Student("Gabriela Lungu", "S6", "gabriela.lungu@stud.univ.ro", 3);
        Student stud7 = new Student("Horia Bădescu", "S7", "horia.badescu@stud.univ.ro", 1);
        Student stud8 = new Student("Ioana Tomescu", "S8", "ioana.tomescu@stud.univ.ro", 2);
        Student stud9 = new Student("Lucian Preda", "S9", "lucian.preda@stud.univ.ro", 3);
        Student stud10 = new Student("Mirela Radu", "S10", "mirela.radu@stud.univ.ro", 1);
        
        adaugaStudent(stud1);
        adaugaStudent(stud2);
        adaugaStudent(stud3);
        adaugaStudent(stud4);
        adaugaStudent(stud5);
        adaugaStudent(stud6);
        adaugaStudent(stud7);
        adaugaStudent(stud8);
        adaugaStudent(stud9);
        adaugaStudent(stud10);
        
        // Înscriem studenți la cursuri și adăugăm note
        try {
            // Toți studenții la Algebră liniară
            inscriereStudentLaMaterie("S1", "CRS1");
            inscriereStudentLaMaterie("S2", "CRS1");
            inscriereStudentLaMaterie("S3", "CRS1");
            inscriereStudentLaMaterie("S4", "CRS1");
            inscriereStudentLaMaterie("S5", "CRS1");
            inscriereStudentLaMaterie("S6", "CRS1");
            inscriereStudentLaMaterie("S7", "CRS1");
            inscriereStudentLaMaterie("S8", "CRS1");
            inscriereStudentLaMaterie("S9", "CRS1");
            inscriereStudentLaMaterie("S10", "CRS1");
            
            // Studenții de anul 1 și 2 la Analiză
            inscriereStudentLaMaterie("S1", "CRS2");
            inscriereStudentLaMaterie("S2", "CRS2");
            inscriereStudentLaMaterie("S3", "CRS2");
            inscriereStudentLaMaterie("S4", "CRS2");
            inscriereStudentLaMaterie("S7", "CRS2");
            inscriereStudentLaMaterie("S8", "CRS2");
            inscriereStudentLaMaterie("S10", "CRS2");
            
            // Studenții de anul 2 și 3 la POO
            inscriereStudentLaMaterie("S3", "CRS3");
            inscriereStudentLaMaterie("S4", "CRS3");
            inscriereStudentLaMaterie("S5", "CRS3");
            inscriereStudentLaMaterie("S6", "CRS3");
            inscriereStudentLaMaterie("S8", "CRS3");
            inscriereStudentLaMaterie("S9", "CRS3");
            
            // Studenții de anul 3 la Structuri de date și Baze de date
            inscriereStudentLaMaterie("S5", "CRS4");
            inscriereStudentLaMaterie("S6", "CRS4");
            inscriereStudentLaMaterie("S9", "CRS4");
            
            inscriereStudentLaMaterie("S5", "CRS5");
            inscriereStudentLaMaterie("S6", "CRS5");
            inscriereStudentLaMaterie("S9", "CRS5");
            
            // Adăugăm note
            // Algebră liniară
            adaugaNota("S1", "CRS1", 9.5);
            adaugaNota("S2", "CRS1", 8.0);
            adaugaNota("S3", "CRS1", 7.5);
            adaugaNota("S4", "CRS1", 10.0);
            adaugaNota("S5", "CRS1", 9.0);
            adaugaNota("S6", "CRS1", 8.5);
            adaugaNota("S7", "CRS1", 7.0);
            adaugaNota("S8", "CRS1", 9.0);
            adaugaNota("S9", "CRS1", 6.5);
            adaugaNota("S10", "CRS1", 8.5);
            
            // Analiză
            adaugaNota("S1", "CRS2", 8.0);
            adaugaNota("S2", "CRS2", 7.5);
            adaugaNota("S3", "CRS2", 9.0);
            adaugaNota("S4", "CRS2", 8.5);
            adaugaNota("S7", "CRS2", 7.0);
            adaugaNota("S8", "CRS2", 10.0);
            adaugaNota("S10", "CRS2", 6.5);
            
            // POO
            adaugaNota("S3", "CRS3", 10.0);
            adaugaNota("S4", "CRS3", 9.5);
            adaugaNota("S5", "CRS3", 9.0);
            adaugaNota("S6", "CRS3", 8.5);
            adaugaNota("S8", "CRS3", 7.5);
            adaugaNota("S9", "CRS3", 8.0);
            
            // Structuri de date
            adaugaNota("S5", "CRS4", 9.5);
            adaugaNota("S6", "CRS4", 8.0);
            adaugaNota("S9", "CRS4", 7.5);
            
            // Baze de date
            adaugaNota("S5", "CRS5", 10.0);
            adaugaNota("S6", "CRS5", 9.0);
            adaugaNota("S9", "CRS5", 8.5);
            
            System.out.println("Baza de date a fost populată cu succes cu date demo!");
        } catch (Exception e) {
            System.err.println("Eroare la popularea datelor demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
        System.out.println(maxNumber);
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
        
        // Salvare în baza de date
        dbService.saveCurs(curs);
        auditService.logActiune("Adaugare curs", curs.getId());
    }

    /**
     * Verifică dacă un profesor este disponibil într-un interval orar specificat
     * @param profesor Profesorul care trebuie verificat
     * @param start Ora de început
     * @param end Ora de sfârșit
     * @return true dacă profesorul este disponibil, false altfel
     */
    public boolean esteProfesorDisponibil(Profesor profesor, LocalTime start, LocalTime end) {
        return cursuri.values().stream()
                .filter(c -> c.getProfesor().getId().equals(profesor.getId()))
                .noneMatch(c -> c.getOraInceput().isBefore(end) && c.getOraSfarsit().isAfter(start));
    }
    
    /**
     * Verifică dacă o sală este disponibilă într-un interval orar specificat
     * @param sala Sala care trebuie verificată
     * @param start Ora de început
     * @param end Ora de sfârșit
     * @return true dacă sala este disponibilă, false altfel
     */
    public boolean esteSalaDisponibila(Sala sala, LocalTime start, LocalTime end) {
        // Verificăm dacă există cursuri în acea sală în intervalul specificat
        return cursuri.values().stream()
                .filter(c -> c.getSala().getId().equals(sala.getId()))
                .noneMatch(c -> c.getOraInceput().isBefore(end) && c.getOraSfarsit().isAfter(start));
    }
    
    /**
     * Verifică dacă un student are conflict de orar cu un curs
     * @param student Studentul care trebuie verificat
     * @param cursNou Cursul nou la care studentul dorește să se înscrie
     * @return true dacă există conflict de orar, false altfel
     */
    public boolean areConflictOrarStudent(Student student, Curs cursNou) {
        // Obținem cursurile direct din baza de date pentru a asigura cea mai recentă stare
        List<Curs> cursuriStudent = dbService.getCursuriByStudent(student.getId());
        
        // Verificăm dacă există conflict de orar cu oricare din cursurile existente
        boolean hasConflict = cursuriStudent.stream()
                .anyMatch(cursExistent -> {
                    // Un conflict există dacă:
                    // 1. Începutul cursului existent este înainte de sfârșitul cursului nou ȘI
                    // 2. Sfârșitul cursului existent este după începutul cursului nou
                    boolean timeOverlap = cursExistent.getOraInceput().isBefore(cursNou.getOraSfarsit()) && 
                                         cursExistent.getOraSfarsit().isAfter(cursNou.getOraInceput());
                    
                    // Nu consideram conflict dacă este același curs
                    boolean isSameCourse = cursExistent.getId().equals(cursNou.getId());
                    
                    return timeOverlap && !isSameCourse;
                });
        
        // Logăm verificarea pentru audit
        auditService.logActiune("Verificare conflict orar student");
        
        return hasConflict;
    }
    
    /**
     * Verifică dacă o sală are suficientă capacitate pentru a adăuga încă un student
     * @param curs Cursul pentru care se verifică capacitatea sălii
     * @return true dacă sala are suficientă capacitate, false altfel
     */
    public boolean areSalaCapacitate(Curs curs) {
        return curs.getSala().getCapacitate() > curs.getStudentiInscrisi().size();
    }
    
    /**
     * Verifică dacă un student este deja înscris la un curs
     * @param studentId ID-ul studentului
     * @param cursId ID-ul cursului
     * @return true dacă studentul este înscris, false altfel
     */
    public boolean esteStudentInscris(String studentId, String cursId) {
        return dbService.isStudentEnrolled(studentId, cursId);
    }

    // Metode pentru studenți
    public void adaugaStudent(Student student) {
        valideazaId(student.getId());
        
        // Salvare în baza de date FIRST
        Student savedStudent = dbService.saveStudent(student);
        
        // Only after successful DB save, add to in-memory collections
        studenti.put(savedStudent.getId(), savedStudent);
        iduriUnice.add(savedStudent.getId());
        
        auditService.logActiune("Adaugare student", student.getId());
    }
    
    public void adaugaSala(Sala sala) {
        valideazaId(sala.getId());
        sali.put(sala.getId(), sala);
        iduriUnice.add(sala.getId());
        
        // Salvare în baza de date
        dbService.saveSala(sala);
        auditService.logActiune("Adaugare sala", sala.getId());
    }

    public List<Student> getStudenti() {
        return new ArrayList<>(studenti.values());
    }

    public Optional<Student> getStudent(String studentId) {
        // Always try to get from database first to ensure we have the latest version
        Optional<Student> studentOpt = dbService.getStudent(studentId);
        
        if (studentOpt.isPresent()) {
            // Update the in-memory cache with the data from the database
            Student student = studentOpt.get();
            studenti.put(student.getId(), student);
            iduriUnice.add(student.getId());
            return studentOpt;
        } else if (studenti.containsKey(studentId)) {
            // If not in DB but in memory, something's wrong - remove from memory
            // This ensures consistency between memory and database
            studenti.remove(studentId);
            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    public Optional<Sala> getSala(String salaId) {
        if (sali.containsKey(salaId)) {
            return Optional.of(sali.get(salaId));
        } else {
            // Încercăm să luăm din baza de date dacă nu există în memorie
            Optional<Sala> salaOpt = dbService.getSala(salaId);
            salaOpt.ifPresent(sala -> {
                sali.put(sala.getId(), sala);
                iduriUnice.add(sala.getId());
            });
            return salaOpt;
        }
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
                    List<Nota> note = dbService.getNoteByStudent(studentId);
                    if (note.isEmpty()) {
                        System.out.println("Nu există note înregistrate.");
                    } else {
                        note.forEach(nota -> System.out.println(
                                "- " + nota.getCurs().getMaterie().getNume() + ": " + nota.getValoare() +
                                        " (Data: " + nota.getDataAtribuire() + ")"
                        ));
                    }
                    
                    auditService.logActiune("Afisare detalii student", studentId);
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
        
        // Salvare în baza de date
        dbService.saveMaterie(materie);
        auditService.logActiune("Adaugare materie", materie.getCod());
    }

    public Optional<Materie> getMaterie(String codMaterie) {
        if (materii.containsKey(codMaterie)) {
            return Optional.of(materii.get(codMaterie));
        } else {
            // Încercăm să luăm din baza de date dacă nu există în memorie
            Optional<Materie> materieOpt = dbService.getMaterie(codMaterie);
            materieOpt.ifPresent(materie -> materii.put(materie.getCod(), materie));
            return materieOpt;
        }
    }

    public Optional<Curs> getCurs(String codCurs) {
        if (cursuri.containsKey(codCurs)) {
            return Optional.of(cursuri.get(codCurs));
        } else {
            // Încercăm să luăm din baza de date dacă nu există în memorie
            Optional<Curs> cursOpt = dbService.getCurs(codCurs);
            cursOpt.ifPresent(curs -> {
                cursuri.put(curs.getId(), curs);
                iduriUnice.add(curs.getId());
            });
            return cursOpt;
        }
    }

    /**
     * Returnează lista tuturor materiilor sortate după nume
     * @return Lista materiilor sortate după nume
     */
    public List<Materie> getMateriiSortate() {
        return materii.values().stream()
                .sorted(Comparator.comparing(Materie::getNume))
                .collect(Collectors.toList());
    }
    
    /**
     * Returnează lista tuturor cursurilor
     * @return Lista cursurilor
     */
    public List<Curs> getCursuri() {
        return new ArrayList<>(cursuri.values());
    }
    
    /**
     * Returnează lista tuturor sălilor
     * @return Lista sălilor
     */
    public List<Sala> getSali() {
        return new ArrayList<>(sali.values());
    }

    // Metode pentru profesori
    public void adaugaProfesor(Profesor profesor) {
        valideazaId(profesor.getId());
        profesori.put(profesor.getId(), profesor);
        iduriUnice.add(profesor.getId());
        
        // Salvare în baza de date
        dbService.saveProfesor(profesor);
        auditService.logActiune("Adaugare profesor", profesor.getId());
    }

    public Optional<Profesor> getProfesor(String profesorId) {
        if (profesori.containsKey(profesorId)) {
            return Optional.of(profesori.get(profesorId));
        } else {
            // Încercăm să luăm din baza de date dacă nu există în memorie
            Optional<Profesor> profesorOpt = dbService.getProfesor(profesorId);
            profesorOpt.ifPresent(profesor -> {
                profesori.put(profesor.getId(), profesor);
                iduriUnice.add(profesor.getId());
            });
            return profesorOpt;
        }
    }

    public Map<String, Profesor> getProfesori() {
        return new HashMap<>(profesori);
    }
    
    /**
     * Returnează lista de cursuri la care predă un anumit profesor
     * @param profesorId ID-ul profesorului
     * @return Lista de cursuri
     */
    public List<Curs> getCursuriByProfesor(String profesorId) {
        return cursuri.values().stream()
                .filter(curs -> curs.getProfesor().getId().equals(profesorId))
                .collect(Collectors.toList());
    }
    
    /**
     * Returnează lista de cursuri care se țin într-o anumită sală
     * @param salaId ID-ul sălii
     * @return Lista de cursuri
     */
    public List<Curs> getCursuribySala(String salaId) {
        return cursuri.values().stream()
                .filter(curs -> curs.getSala().getId().equals(salaId))
                .collect(Collectors.toList());
    }
    
    /**
     * Afișează orarul unei săli (toate cursurile care se țin în ea)
     * @param salaId ID-ul sălii
     */
    public void afiseazaOrarSala(String salaId) {
        getSala(salaId).ifPresentOrElse(
                sala -> {
                    System.out.println("=== Orar Sala: " + sala.getNume() + " (ID: " + sala.getId() + ") ===");
                    System.out.println("Capacitate: " + sala.getCapacitate());
                    System.out.println("Facilități: " + String.join(", ", sala.getFacilitati()));
                    
                    List<Curs> cursuriInSala = getCursuribySala(salaId);
                    
                    if (cursuriInSala.isEmpty()) {
                        System.out.println("\nNu există cursuri programate în această sală.");
                    } else {
                        // Sortăm cursurile după ora de început
                        cursuriInSala.sort(Comparator.comparing(Curs::getOraInceput));
                        
                        System.out.println("\n=== Programul cursurilor ===");
                        System.out.println("─────────────────────────────────────────────────────────────────────────────");
                        System.out.printf("%-5s | %-25s | %-20s | %-15s | %-10s\n", 
                            "Orar", "Materie", "Profesor", "ID Curs", "Studenți");
                        System.out.println("─────────────────────────────────────────────────────────────────────────────");
                        
                        cursuriInSala.forEach(curs -> {
                            String orar = curs.getOraInceput().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + 
                                    " - " + curs.getOraSfarsit().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                                    
                            System.out.printf("%-5s | %-25s | %-20s | %-15s | %-10d\n", 
                                orar,
                                curs.getMaterie().getNume(), 
                                curs.getProfesor().getNume(),
                                curs.getId(),
                                curs.getStudentiInscrisi().size());
                        });
                        System.out.println("─────────────────────────────────────────────────────────────────────────────");
                        System.out.println("Total cursuri: " + cursuriInSala.size());
                    }
                    
                    auditService.logActiune("Afisare orar sala", salaId);
                },
                () -> System.out.println("Sala cu ID-ul " + salaId + " nu există!")
        );
    }
    
    /**
     * Afișează detaliile unui profesor după ID, inclusiv cursurile la care predă.
     * @param profesorId ID-ul profesorului căutat.
     */
    public void afiseazaProfesorDupaId(String profesorId) {
        getProfesor(profesorId).ifPresentOrElse(
                profesor -> {
                    System.out.println("=== Detalii Profesor ===");
                    System.out.println("Nume: " + profesor.getNume());
                    System.out.println("ID: " + profesor.getId());
                    System.out.println("Titulatură: " + profesor.getTitulatura());

                    System.out.println("\n=== Cursuri predate ===");
                    List<Curs> cursuri = getCursuriByProfesor(profesorId);
                    if (cursuri.isEmpty()) {
                        System.out.println("Profesorul nu predă niciun curs.");
                    } else {
                        // Sortăm cursurile după numele materiei
                        cursuri.sort(Comparator.comparing(c -> c.getMaterie().getNume()));
                        
                        cursuri.forEach(curs -> System.out.println(
                                "- " + curs.getMaterie().getNume() + " (" + 
                                curs.getOraInceput().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + 
                                " - " + curs.getOraSfarsit().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + ", " +
                                "Sala: " + curs.getSala().getNume() + ", " +
                                "Studenți înscriși: " + curs.getStudentiInscrisi().size() + ")"
                        ));
                    }
                    
                    auditService.logActiune("Afisare detalii profesor", profesorId);
                },
                () -> System.out.println("Profesorul cu ID-ul " + profesorId + " nu există!")
        );
    }

    // Metode pentru înscrieri și note
    public void inscriereStudentLaMaterie(String studentId, String codCurs) {
        Student student = getStudent(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));
        Curs curs = getCurs(codCurs)
                .orElseThrow(() -> new IllegalArgumentException("Materie inexistentă!"));
        
        // Verificăm dacă studentul este deja înscris
        if (dbService.isStudentEnrolled(studentId, codCurs)) {
            throw new IllegalArgumentException("Studentul este deja înscris la acest curs!");
        }
        
        Inscriere inscriere = new Inscriere(student, curs);
        student.adaugaInscriere(inscriere);
        curs.inscriereStudent(student);
        
        // Salvăm în baza de date
        dbService.saveInscriere(inscriere);
        auditService.logActiune("Inscriere student la materie", studentId + " la " + codCurs);
    }

    public void adaugaNota(String studentId, String codCurs, double notaValoare) {
        Student student = getStudent(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));
        Curs curs = getCurs(codCurs)
                .orElseThrow(() -> new IllegalArgumentException("Materie inexistentă!"));
        
        // Verificăm dacă studentul este înscris la curs
        if (!dbService.isStudentEnrolled(studentId, codCurs)) {
            throw new IllegalArgumentException("Studentul nu este înscris la acest curs!");
        }
        
        Nota nota = new Nota(student, curs, notaValoare);
        student.adaugaNota(nota);
        
        // Salvăm în baza de date
        dbService.saveNota(nota);
        auditService.logActiune("Adaugare nota", studentId + " la " + codCurs + " nota " + notaValoare);
    }

    public double getMedieStudent(String studentId) {
        // Verificăm dacă studentul există
        if (!getStudent(studentId).isPresent()) {
            throw new IllegalArgumentException("Student inexistent!");
        }
        
        // Calculăm media folosind baza de date
        double medie = dbService.getMedieStudent(studentId);
        auditService.logActiune("Calcul medie student", studentId);
        return medie;
    }

    // Metode pentru cursuri
    public List<Curs> getCursuriByStudent(String studentId) {
        // Verificăm dacă studentul există
        if (!getStudent(studentId).isPresent()) {
            return Collections.emptyList();
        }
        
        // Obținem cursurile la care este înscris studentul
        List<Curs> cursuri = dbService.getCursuriByStudent(studentId);
        
        // Debugging information
        System.out.println("Debug - Cursuri pentru student " + studentId + ": " + cursuri.size());
        
        auditService.logActiune("Obtinere cursuri student");
        return cursuri;
    }
    
    // Metode pentru ștergerea datelor cu validare ierarhică
    
    /**
     * Verifică dacă un student poate fi șters
     * (dacă nu are înscrieri la cursuri sau note)
     * @param studentId ID-ul studentului
     * @return true dacă studentul poate fi șters, false altfel
     */
    public boolean canDeleteStudent(String studentId) {
        boolean hasEnrollments = dbService.inscriereRepository.hasEnrollmentsByStudentId(studentId);
        boolean hasGrades = dbService.notaRepository.hasGradesByStudentId(studentId);
        
        return !hasEnrollments && !hasGrades;
    }
    
    /**
     * Șterge un student și toate datele asociate (note și înscrieri)
     * @param studentId ID-ul studentului
     * @return true dacă operația a reușit, false altfel
     */
    public boolean deleteStudent(String studentId) {
        // Verificăm dacă studentul există
        Optional<Student> studentOpt = getStudent(studentId);
        if (!studentOpt.isPresent()) {
            System.out.println("Studentul cu ID-ul " + studentId + " nu există!");
            return false;
        }
        
        try {
            // 1. Ștergem toate notele studentului
            dbService.notaRepository.deleteByStudentId(studentId);
            
            // 2. Ștergem toate înscrierile studentului
            dbService.inscriereRepository.deleteByStudentId(studentId);
            
            // 3. Ștergem studentul
            boolean success = dbService.deleteStudent(studentId);
            
            // 4. Îl ștergem și din memoria aplicației
            if (success) {
                studenti.remove(studentId);
                iduriUnice.remove(studentId);
            }
            
            auditService.logActiune("Stergere student", studentId);
            return success;
        } catch (Exception e) {
            System.err.println("Eroare la ștergerea studentului: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifică dacă un curs poate fi șters
     * (dacă nu are înscrieri sau note)
     * @param cursId ID-ul cursului
     * @return true dacă cursul poate fi șters, false altfel
     */
    public boolean canDeleteCurs(String cursId) {
        boolean hasEnrollments = dbService.inscriereRepository.hasEnrollmentsByCursId(cursId);
        boolean hasGrades = dbService.notaRepository.hasGradesByCursId(cursId);
        
        return !hasEnrollments && !hasGrades;
    }
    
    /**
     * Șterge un curs și toate datele asociate (note și înscrieri)
     * @param cursId ID-ul cursului
     * @return true dacă operația a reușit, false altfel
     */
    public boolean deleteCurs(String cursId) {
        // Verificăm dacă cursul există
        Optional<Curs> cursOpt = getCurs(cursId);
        if (!cursOpt.isPresent()) {
            System.out.println("Cursul cu ID-ul " + cursId + " nu există!");
            return false;
        }
        
        try {
            // 1. Ștergem toate notele asociate cursului
            dbService.notaRepository.deleteByCursId(cursId);
            
            // 2. Ștergem toate înscrierile la curs
            dbService.inscriereRepository.deleteByCursId(cursId);
            
            // 3. Ștergem cursul
            boolean success = dbService.deleteCurs(cursId);
            
            // 4. Îl ștergem și din memoria aplicației
            if (success) {
                cursuri.remove(cursId);
                iduriUnice.remove(cursId);
            }
            
            auditService.logActiune("Stergere curs", cursId);
            return success;
        } catch (Exception e) {
            System.err.println("Eroare la ștergerea cursului: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifică dacă un profesor poate fi șters
     * (dacă nu predă la niciun curs)
     * @param profesorId ID-ul profesorului
     * @return true dacă profesorul poate fi șters, false altfel
     */
    public boolean canDeleteProfesor(String profesorId) {
        List<Curs> cursuriProfesor = getCursuriByProfesor(profesorId);
        return cursuriProfesor.isEmpty();
    }
    
    /**
     * Șterge un profesor
     * @param profesorId ID-ul profesorului
     * @return true dacă operația a reușit, false altfel
     */
    public boolean deleteProfesor(String profesorId) {
        // Verificăm dacă profesorul există
        Optional<Profesor> profesorOpt = getProfesor(profesorId);
        if (!profesorOpt.isPresent()) {
            System.out.println("Profesorul cu ID-ul " + profesorId + " nu există!");
            return false;
        }
        
        // Verificăm dacă profesorul poate fi șters
        if (!canDeleteProfesor(profesorId)) {
            System.out.println("Profesorul nu poate fi șters deoarece predă la unul sau mai multe cursuri!");
            return false;
        }
        
        try {
            // Ștergem profesorul
            boolean success = dbService.deleteProfesor(profesorId);
            
            // Îl ștergem și din memoria aplicației
            if (success) {
                profesori.remove(profesorId);
                iduriUnice.remove(profesorId);
            }
            
            auditService.logActiune("Stergere profesor", profesorId);
            return success;
        } catch (Exception e) {
            System.err.println("Eroare la ștergerea profesorului: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifică dacă o materie poate fi ștearsă
     * (dacă nu este folosită la niciun curs)
     * @param codMaterie Codul materiei
     * @return true dacă materia poate fi ștearsă, false altfel
     */
    public boolean canDeleteMaterie(String codMaterie) {
        // Verificăm dacă materia este folosită în vreun curs
        return cursuri.values().stream()
                .noneMatch(curs -> curs.getMaterie().getCod().equals(codMaterie));
    }
    
    /**
     * Șterge o materie
     * @param codMaterie Codul materiei
     * @return true dacă operația a reușit, false altfel
     */
    public boolean deleteMaterie(String codMaterie) {
        // Verificăm dacă materia există
        Optional<Materie> materieOpt = getMaterie(codMaterie);
        if (!materieOpt.isPresent()) {
            System.out.println("Materia cu codul " + codMaterie + " nu există!");
            return false;
        }
        
        // Verificăm dacă materia poate fi ștearsă
        if (!canDeleteMaterie(codMaterie)) {
            System.out.println("Materia nu poate fi ștearsă deoarece este folosită în unul sau mai multe cursuri!");
            return false;
        }
        
        try {
            // Ștergem materia
            boolean success = dbService.deleteMaterie(codMaterie);
            
            // O ștergem și din memoria aplicației
            if (success) {
                materii.remove(codMaterie);
                iduriUnice.remove(codMaterie);
            }
            
            auditService.logActiune("Stergere materie", codMaterie);
            return success;
        } catch (Exception e) {
            System.err.println("Eroare la ștergerea materiei: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifică dacă o sală poate fi ștearsă
     * (dacă nu este folosită la niciun curs)
     * @param salaId ID-ul sălii
     * @return true dacă sala poate fi ștearsă, false altfel
     */
    public boolean canDeleteSala(String salaId) {
        // Verificăm dacă sala este folosită în vreun curs
        return cursuri.values().stream()
                .noneMatch(curs -> curs.getSala().getId().equals(salaId));
    }
    
    /**
     * Șterge o sală
     * @param salaId ID-ul sălii
     * @return true dacă operația a reușit, false altfel
     */
    public boolean deleteSala(String salaId) {
        // Verificăm dacă sala există
        Optional<Sala> salaOpt = getSala(salaId);
        if (!salaOpt.isPresent()) {
            System.out.println("Sala cu ID-ul " + salaId + " nu există!");
            return false;
        }
        
        // Verificăm dacă sala poate fi ștearsă
        if (!canDeleteSala(salaId)) {
            System.out.println("Sala nu poate fi ștearsă deoarece este folosită în unul sau mai multe cursuri!");
            return false;
        }
        
        try {
            // Ștergem sala
            boolean success = dbService.deleteSala(salaId);
            
            // O ștergem și din memoria aplicației
            if (success) {
                sali.remove(salaId);
                iduriUnice.remove(salaId);
            }
            
            auditService.logActiune("Stergere sala", salaId);
            return success;
        } catch (Exception e) {
            System.err.println("Eroare la ștergerea sălii: " + e.getMessage());
            return false;
        }
    }
}