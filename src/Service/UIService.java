package Service;

import Model.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Serviciu pentru gestionarea interfeței utilizator și a interacțiunii cu utilizatorul
 */
public class UIService {
    
    /**
     * Afișează detaliile unui profesor și cursurile la care predă
     */
    private void afiseazaDetaliiProfesor() {
        System.out.print("ID profesor: ");
        String profesorId = scanner.nextLine();
        
        catalogService.afiseazaProfesorDupaId(profesorId);
    }
    
    /**
     * Afișează orarul unei săli (toate cursurile care se țin în ea)
     */
    private void afiseazaOrarSala() {
        System.out.print("ID sală: ");
        String salaId = scanner.nextLine();
        
        catalogService.afiseazaOrarSala(salaId);
    }
    
    /**
     * Afișează toate materiile din baza de date
     */
    public void afiseazaToateMateriile() {
        List<Materie> materii = catalogService.getMateriiSortate();
        
        if (materii.isEmpty()) {
            System.out.println("Nu există materii înregistrate.");
            return;
        }
        
        System.out.println("\n=== Lista Materiilor ===");
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-40s | %-10s\n", "Cod", "Nume", "Credite");
        System.out.println("────────────────────────────────────────────────────────────────────");
        
        for (Materie materie : materii) {
            System.out.printf("%-5s | %-40s | %-10d\n", 
                    materie.getCod(),
                    materie.getNume(), 
                    materie.getCredite());
        }
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.println("Total materii: " + materii.size());
    }
    
    /**
     * Afișează toate cursurile din baza de date
     */
    public void afiseazaToateCursurile() {
        List<Curs> cursuri = catalogService.getCursuri();
        
        if (cursuri.isEmpty()) {
            System.out.println("Nu există cursuri înregistrate.");
            return;
        }
        
        // Sortăm cursurile după materie și ora de început
        cursuri.sort(Comparator.comparing((Curs c) -> c.getMaterie().getNume())
                .thenComparing(Curs::getOraInceput));
        
        System.out.println("\n=== Lista Cursurilor ===");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-30s | %-20s | %-15s | %-15s | %-10s\n", 
                "ID", "Materie", "Profesor", "Sala", "Ora", "Studenți");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────");
        
        for (Curs curs : cursuri) {
            System.out.printf("%-5s | %-30s | %-20s | %-15s | %5s - %-5s | %-10d\n", 
                    curs.getId(),
                    curs.getMaterie().getNume(), 
                    curs.getProfesor().getNume(),
                    curs.getSala().getNume(),
                    curs.getOraInceput().format(TIME_FORMATTER),
                    curs.getOraSfarsit().format(TIME_FORMATTER),
                    curs.getStudentiInscrisi().size());
        }
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.println("Total cursuri: " + cursuri.size());
    }
    
    /**
     * Afișează toate sălile din baza de date
     */
    public void afiseazaToateSalile() {
        List<Sala> sali = catalogService.getSali();
        
        if (sali.isEmpty()) {
            System.out.println("Nu există săli înregistrate.");
            return;
        }
        
        // Sortăm sălile după nume
        sali.sort(Comparator.comparing(Sala::getNume));
        
        System.out.println("\n=== Lista Sălilor ===");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-25s | %-10s | %-30s\n", 
                "ID", "Nume", "Capacitate", "Facilități");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────");
        
        for (Sala sala : sali) {
            System.out.printf("%-5s | %-25s | %-10d | %-30s\n", 
                    sala.getId(),
                    sala.getNume(), 
                    sala.getCapacitate(),
                    String.join(", ", sala.getFacilitati()));
        }
        System.out.println("──────────────────────────────────────────────────────────────────────────────────");
        System.out.println("Total săli: " + sali.size());
    }
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final CatalogService catalogService;
    private final Scanner scanner;

    public UIService(CatalogService catalogService) {
        this.catalogService = catalogService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Afișează meniul principal al aplicației
     */
    public void afiseazaMeniuPrincipal() {
        System.out.println("\n=== Catalog Electronic ===");
        System.out.println("=== Adăugare ===");
        System.out.println("1. Adaugă student");
        System.out.println("2. Adaugă profesor");
        System.out.println("3. Adaugă materie");
        System.out.println("4. Adaugă curs");
        System.out.println("5. Adaugă sala");
        System.out.println("6. Înscrie student la curs");
        System.out.println("7. Adaugă notă");
        
        System.out.println("\n=== Vizualizare individuală ===");
        System.out.println("8. Afișează foaie matricolă");
        System.out.println("9. Afișează program student");
        System.out.println("10. Afișează materie");
        System.out.println("11. Afișează detalii profesor");
        System.out.println("12. Afișează orar sală");
        
        System.out.println("\n=== Liste complete ===");
        System.out.println("13. Afișează toți studenții");
        System.out.println("14. Afișează toți profesorii");
        System.out.println("15. Afișează toate materiile");
        System.out.println("16. Afișează toate cursurile");
        System.out.println("17. Afișează toate sălile");
        
        System.out.println("\n=== Administrare ===");
        System.out.println("18. Populează baza de date cu date demo");
        
        System.out.println("\n=== Ștergere ===");
        System.out.println("19. Șterge student");
        System.out.println("20. Șterge profesor");
        System.out.println("21. Șterge materie");
        System.out.println("22. Șterge curs");
        System.out.println("23. Șterge sală");
        System.out.println("24. Șterge notă");
        
        System.out.println("\n25. Ieșire");
        System.out.print("Alege opțiunea: ");
    }

    /**
     * Tratează opțiunea selectată de utilizator
     * @param optiune Opțiunea selectată
     * @return true dacă aplicația ar trebui să continue, false dacă ar trebui să se încheie
     */
    public boolean trateazaOptiune(int optiune) {
        try {
            switch (optiune) {
                case 1:
                    adaugaStudent();
                    break;
                case 2:
                    adaugaProfesor();
                    break;
                case 3:
                    adaugaMaterie();
                    break;
                case 4:
                    adaugaCurs();
                    break;
                case 5:
                    adaugaSala();
                    break;
                case 6:
                    inscriereStudentLaCurs();
                    break;
                case 7:
                    adaugaNota();
                    break;
                case 8:
                    afiseazaFoaieMatricola();
                    break;
                case 9:
                    afiseazaProgramStudent();
                    break;
                case 10:
                    afiseazaMaterie();
                    break;
                case 11:
                    afiseazaDetaliiProfesor();
                    break;
                case 12:
                    afiseazaOrarSala();
                    break;
                case 13:
                    afiseazaTotiStudentii();
                    break;
                case 14:
                    afiseazaTotiProfesorii();
                    break;
                case 15:
                    afiseazaToateMateriile();
                    break;
                case 16:
                    afiseazaToateCursurile();
                    break;
                case 17:
                    afiseazaToateSalile();
                    break;
                case 18:
                    populateazaDateDemo();
                    break;
                case 19:
                    stergeStudent();
                    break;
                case 20:
                    stergeProfesor();
                    break;
                case 21:
                    stergeMaterie();
                    break;
                case 22:
                    stergeCurs();
                    break;
                case 23:
                    stergeSala();
                    break;
                case 24:
                    stergeNota();
                    break;
                case 25:
                    System.out.println("Ieșire...");
                    return false;
                default:
                    System.out.println("Opțiune invalidă!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Eroare neașteptată: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
    
    /**
     * Populează baza de date cu date demo
     */
    private void populateazaDateDemo() {
        System.out.println("Se populează baza de date cu date demo...");
        catalogService.populateDemoData();
        System.out.println("Baza de date a fost populată cu succes!");
    }

    /**
     * Adaugă un student nou
     */
    private void adaugaStudent() {
        System.out.print("Nume student: ");
        String nume = scanner.nextLine();
        System.out.print("Email student: ");
        String email = scanner.nextLine();
        System.out.print("An studiu: ");
        int anStudiu = scanner.nextInt();
        scanner.nextLine(); // Curăță buffer

        try {
            String id = catalogService.genereazaIdDisponibil("S");
            Student student = new Student(nume, id, email, anStudiu);
            catalogService.adaugaStudent(student);
            System.out.println("Student adăugat cu ID: " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Adaugă un profesor nou
     */
    private void adaugaProfesor() {
        System.out.print("Nume profesor: ");
        String nume = scanner.nextLine();
        System.out.print("Titulatură (ex: Conf. Dr.): ");
        String titulatura = scanner.nextLine();

        try {
            String id = catalogService.genereazaIdDisponibil("P");
            Profesor profesor = new Profesor(nume, id, titulatura);
            catalogService.adaugaProfesor(profesor);
            System.out.println("Profesor adăugat cu ID: " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Adaugă o materie nouă
     */
    private void adaugaMaterie() {
        System.out.print("Nume materie: ");
        String nume = scanner.nextLine();
        System.out.print("Credite: ");
        int credite = scanner.nextInt();
        scanner.nextLine(); // Curăță buffer

        try {
            String cod = catalogService.genereazaIdDisponibil("MAT");
            Materie materie = new Materie(nume, cod, credite);
            catalogService.adaugaMaterie(materie);
            System.out.println("Materie adăugată cu codul: " + cod);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Adaugă o sală nouă
     */
    private void adaugaSala() {
        System.out.print("Nume sala: ");
        String nume = scanner.nextLine();
        System.out.print("Capacitate: ");
        int capacitate = scanner.nextInt();
        scanner.nextLine(); // Curăță buffer
        System.out.print("Facilități (separate prin virgulă): ");
        List<String> facilitati = Arrays.asList(scanner.nextLine().split("\\s*,\\s*"));

        try {
            String id = catalogService.genereazaIdDisponibil("R");
            Sala sala = new Sala(id, nume, capacitate, facilitati);
            catalogService.adaugaSala(sala);
            System.out.println("Sala adăugată cu ID: " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Adaugă un curs nou
     */
    private void adaugaCurs() {
        System.out.print("Cod materie: ");
        String codMaterie = scanner.nextLine();
        System.out.print("ID profesor: ");
        String profesorId = scanner.nextLine();
        System.out.print("ID Sala: ");
        String idSala = scanner.nextLine();
        System.out.print("Ora început (HH:mm): ");
        LocalTime start = LocalTime.parse(scanner.nextLine());
        System.out.print("Ora sfârșit (HH:mm): ");
        LocalTime end = LocalTime.parse(scanner.nextLine());

        try {
            // Verificăm disponibilitatea profesorului în intervalul specificat
            Profesor profesor = catalogService.getProfesor(profesorId)
                    .orElseThrow(() -> new IllegalArgumentException("Profesor inexistent!"));
            
            if (!catalogService.esteProfesorDisponibil(profesor, start, end)) {
                throw new IllegalArgumentException("Profesorul este ocupat în acest interval orar!");
            }
            
            // Verificăm disponibilitatea sălii în intervalul specificat
            Sala sala = catalogService.getSala(idSala)
                    .orElseThrow(() -> new IllegalArgumentException("Sala inexistentă!"));
            
            if (!catalogService.esteSalaDisponibila(sala, start, end)) {
                throw new IllegalArgumentException("Sala este ocupată în acest interval orar!");
            }
            
            String cursId = catalogService.genereazaIdDisponibil("CRS");
            Materie materie = catalogService.getMaterie(codMaterie)
                    .orElseThrow(() -> new IllegalArgumentException("Materie inexistentă!"));
            
            Curs curs = new Curs(cursId, materie, profesor, sala, start, end);
            catalogService.adaugaCurs(curs);
            System.out.println("Curs adăugat cu ID: " + cursId);
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Înscrie un student la un curs
     */
    private void inscriereStudentLaCurs() {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();
        System.out.print("Cod curs: ");
        String codCurs = scanner.nextLine();

        try {
            // Verificăm dacă studentul există
            Student student = catalogService.getStudent(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));

            // Verificăm dacă cursul există
            Curs curs = catalogService.getCurs(codCurs)
                    .orElseThrow(() -> new IllegalArgumentException("Curs inexistent!"));

            // Verificăm dacă studentul este deja înscris
            if (catalogService.esteStudentInscris(studentId, codCurs)) {
                throw new IllegalArgumentException("Studentul este deja înscris la acest curs!");
            }

            // Verificăm conflict de orar pentru student
            if (catalogService.areConflictOrarStudent(student, curs)) {
                throw new IllegalArgumentException("Studentul are deja un curs în acest interval orar!");
            }
            
            // Verificăm capacitatea sălii
            if (!catalogService.areSalaCapacitate(curs)) {
                throw new IllegalArgumentException("Nu mai este loc în sală!");
            }
            
            // Înscriem studentul
            catalogService.inscriereStudentLaMaterie(studentId, codCurs);
            
            System.out.println("Înscriere reușită: " + student.getNume() + " la " + curs.getMaterie().getNume());

        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Adaugă o notă pentru un student la un curs
     */
    private void adaugaNota() {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();
        
        // Verificăm dacă studentul există
        Optional<Student> studentOpt = catalogService.getStudent(studentId);
        if (!studentOpt.isPresent()) {
            System.out.println("Studentul cu ID-ul " + studentId + " nu există!");
            return;
        }
        
        // Obținem cursurile la care este înscris studentul
        List<Curs> cursuri = catalogService.getCursuriByStudent(studentId);
        if (cursuri.isEmpty()) {
            System.out.println("Studentul nu este înscris la niciun curs!");
            return;
        }
        
        // Afișăm cursurile la care este înscris studentul
        System.out.println("\n=== Cursuri la care este înscris studentul " + studentOpt.get().getNume() + " ===");
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-30s | %-20s | %-15s\n", 
                "ID", "Materie", "Profesor", "Orar");
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
        
        for (Curs curs : cursuri) {
            System.out.printf("%-5s | %-30s | %-20s | %5s - %-5s\n", 
                    curs.getId(),
                    curs.getMaterie().getNume(), 
                    curs.getProfesor().getNume(),
                    curs.getOraInceput().format(TIME_FORMATTER),
                    curs.getOraSfarsit().format(TIME_FORMATTER));
        }
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
        
        // Solicităm ID-ul cursului
        System.out.print("Cod Curs: ");
        String codCurs = scanner.nextLine();
        
        // Verificăm dacă cursul există în lista cursurilor studentului
        boolean cursuriContainCodCurs = cursuri.stream()
                .anyMatch(curs -> curs.getId().equals(codCurs));
        
        if (!cursuriContainCodCurs) {
            System.out.println("Studentul nu este înscris la cursul cu ID-ul " + codCurs + "!");
            return;
        }
        
        // Solicităm nota
        System.out.print("Nota: ");
        double nota = scanner.nextDouble();
        scanner.nextLine(); // Curăță buffer

        try {
            // Verificare dacă nota este validă
            if (nota < 1 || nota > 10) {
                throw new IllegalArgumentException("Nota trebuie să fie între 1 și 10!");
            }
            
            catalogService.adaugaNota(studentId, codCurs, nota);
            System.out.println("Notă adăugată cu succes!");

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Afișează foaia matricolă pentru un student
     */
    private void afiseazaFoaieMatricola() {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();

        catalogService.getStudent(studentId).ifPresentOrElse(
                student -> {
                    // Informații de bază despre student
                    System.out.println("\n=== Foaie Matricolă ===");
                    System.out.println("Nume: " + student.getNume());
                    System.out.println("ID: " + student.getId());
                    System.out.println("Email: " + student.getEmail());
                    System.out.println("An studiu: " + student.getAnStudiu());
                    
                    // Obținem lista de cursuri la care e înscris studentul
                    List<Curs> cursuri = catalogService.getCursuriByStudent(studentId);
                    
                    // Afișăm cursurile la care e înscris studentul
                    System.out.println("\n=== Cursuri Înscrise ===");
                    if (cursuri.isEmpty()) {
                        System.out.println("Studentul nu este înscris la niciun curs.");
                    } else {
                        System.out.println("──────────────────────────────────────────────────────────────────────────────");
                        System.out.printf("%-5s | %-30s | %-20s | %-15s\n", 
                                "ID", "Materie", "Profesor", "Orar");
                        System.out.println("──────────────────────────────────────────────────────────────────────────────");
                        
                        for (Curs curs : cursuri) {
                            System.out.printf("%-5s | %-30s | %-20s | %5s - %-5s\n", 
                                    curs.getId(),
                                    curs.getMaterie().getNume(), 
                                    curs.getProfesor().getNume(),
                                    curs.getOraInceput().format(TIME_FORMATTER),
                                    curs.getOraSfarsit().format(TIME_FORMATTER));
                        }
                        System.out.println("──────────────────────────────────────────────────────────────────────────────");
                    }
                    
                    // Obținem toate notele studentului
                    List<Nota> note = catalogService.dbService.getNoteByStudent(studentId);
                    
                    // Afișăm toate notele studentului
                    System.out.println("\n=== Note Obținute ===");
                    if (note.isEmpty()) {
                        System.out.println("Studentul nu are note înregistrate.");
                    } else {
                        System.out.println("──────────────────────────────────────────────────────────────────────────────");
                        System.out.printf("%-30s | %-5s | %-15s\n", 
                                "Materie", "Nota", "Data");
                        System.out.println("──────────────────────────────────────────────────────────────────────────────");
                        
                        // Sortăm notele după materie și dată
                        note.sort(Comparator.comparing((Nota n) -> n.getCurs().getMaterie().getNume())
                                .thenComparing(Nota::getDataAtribuire));
                        
                        for (Nota nota : note) {
                            System.out.printf("%-30s | %-5.2f | %-15s\n", 
                                    nota.getCurs().getMaterie().getNume(),
                                    nota.getValoare(),
                                    nota.getDataAtribuire().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                        }
                        System.out.println("──────────────────────────────────────────────────────────────────────────────");
                    }
                    
                    // Afișăm media generală
                    double medie = catalogService.getMedieStudent(studentId);
                    System.out.println("\n=== Media generală: " + String.format("%.2f", medie) + " ===");
                },
                () -> System.out.println("Studentul nu există!")
        );
    }

    /**
     * Afișează programul unui student
     */
    private void afiseazaProgramStudent() {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();

        List<Curs> cursuri = catalogService.getCursuriByStudent(studentId);
        if (cursuri.isEmpty()) {
            System.out.println("Studentul nu este înscris la niciun curs.");
        } else {
            System.out.println("Programul studentului:");
            
            // Sortăm cursurile după oră
            cursuri.sort(Comparator.comparing(Curs::getOraInceput));
            
            cursuri.forEach(c -> System.out.println(
                    c.getMaterie().getNume() + " - " +
                            c.getOraInceput().format(TIME_FORMATTER) + " -> " + 
                            c.getOraSfarsit().format(TIME_FORMATTER) + ", Sala " + c.getSala().getNume()
            ));
        }
    }

    /**
     * Afișează detaliile unei materii
     */
    private void afiseazaMaterie() {
        System.out.print("Cod materie: ");
        String codMaterie = scanner.nextLine();

        catalogService.getMaterie(codMaterie).ifPresentOrElse(
                materie -> {
                    System.out.println("\n=== Fișa disciplinei ===");
                    System.out.println(materie);
                },
                () -> System.out.println("Materia nu există!")
        );
    }
    
    /**
     * Șterge un student dacă nu are înscrieri sau note
     */
    private void stergeStudent() {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();
        
        // Verificăm dacă studentul există
        Optional<Student> studentOpt = catalogService.getStudent(studentId);
        if (!studentOpt.isPresent()) {
            System.out.println("Studentul cu ID-ul " + studentId + " nu există!");
            return;
        }
        
        // Verificăm dacă studentul poate fi șters direct
        if (catalogService.canDeleteStudent(studentId)) {
            if (catalogService.deleteStudent(studentId)) {
                System.out.println("Studentul a fost șters cu succes!");
            } else {
                System.out.println("Eroare la ștergerea studentului!");
            }
            return;
        }
        
        // Dacă are înscrieri sau note, întrebăm utilizatorul dacă dorește să le șteargă automat
        System.out.println("Studentul are înscrieri la cursuri și/sau note.");
        System.out.print("Doriți să ștergeți automat toate înscrierile și notele studentului? (da/nu): ");
        String raspuns = scanner.nextLine().trim().toLowerCase();
        
        if (raspuns.equals("da")) {
            if (catalogService.deleteStudent(studentId)) {
                System.out.println("Studentul și toate datele asociate au fost șterse cu succes!");
            } else {
                System.out.println("Eroare la ștergerea studentului!");
            }
        } else {
            System.out.println("Operațiunea de ștergere a fost anulată.");
        }
    }
    
    /**
     * Șterge un profesor dacă nu predă la niciun curs
     */
    private void stergeProfesor() {
        System.out.print("ID profesor: ");
        String profesorId = scanner.nextLine();
        
        // Verificăm dacă profesorul există
        Optional<Profesor> profesorOpt = catalogService.getProfesor(profesorId);
        if (!profesorOpt.isPresent()) {
            System.out.println("Profesorul cu ID-ul " + profesorId + " nu există!");
            return;
        }
        
        // Verificăm dacă profesorul poate fi șters
        if (catalogService.canDeleteProfesor(profesorId)) {
            if (catalogService.deleteProfesor(profesorId)) {
                System.out.println("Profesorul a fost șters cu succes!");
            } else {
                System.out.println("Eroare la ștergerea profesorului!");
            }
        } else {
            System.out.println("Profesorul nu poate fi șters deoarece predă la unul sau mai multe cursuri!");
            System.out.println("Trebuie să ștergeți mai întâi cursurile la care predă profesorul.");
        }
    }
    
    /**
     * Șterge o materie dacă nu este folosită în niciun curs
     */
    private void stergeMaterie() {
        System.out.print("Cod materie: ");
        String codMaterie = scanner.nextLine();
        
        // Verificăm dacă materia există
        Optional<Materie> materieOpt = catalogService.getMaterie(codMaterie);
        if (!materieOpt.isPresent()) {
            System.out.println("Materia cu codul " + codMaterie + " nu există!");
            return;
        }
        
        // Verificăm dacă materia poate fi ștearsă
        if (catalogService.canDeleteMaterie(codMaterie)) {
            if (catalogService.deleteMaterie(codMaterie)) {
                System.out.println("Materia a fost ștearsă cu succes!");
            } else {
                System.out.println("Eroare la ștergerea materiei!");
            }
        } else {
            System.out.println("Materia nu poate fi ștearsă deoarece este folosită în unul sau mai multe cursuri!");
            System.out.println("Trebuie să ștergeți mai întâi cursurile care folosesc această materie.");
        }
    }
    
    /**
     * Șterge un curs și datele asociate (înscrieri, note)
     */
    private void stergeCurs() {
        System.out.print("ID curs: ");
        String cursId = scanner.nextLine();
        
        // Verificăm dacă cursul există
        Optional<Curs> cursOpt = catalogService.getCurs(cursId);
        if (!cursOpt.isPresent()) {
            System.out.println("Cursul cu ID-ul " + cursId + " nu există!");
            return;
        }
        
        // Verificăm dacă cursul are înscrieri sau note
        if (catalogService.canDeleteCurs(cursId)) {
            // Cursul poate fi șters direct
            if (catalogService.deleteCurs(cursId)) {
                System.out.println("Cursul a fost șters cu succes!");
            } else {
                System.out.println("Eroare la ștergerea cursului!");
            }
            return;
        }
        
        // Dacă are înscrieri sau note, întrebăm utilizatorul dacă dorește să le șteargă automat
        System.out.println("Cursul are studenți înscriși și/sau note asociate.");
        System.out.print("Doriți să ștergeți automat toate înscrierile și notele asociate? (da/nu): ");
        String raspuns = scanner.nextLine().trim().toLowerCase();
        
        if (raspuns.equals("da")) {
            if (catalogService.deleteCurs(cursId)) {
                System.out.println("Cursul și toate datele asociate au fost șterse cu succes!");
            } else {
                System.out.println("Eroare la ștergerea cursului!");
            }
        } else {
            System.out.println("Operațiunea de ștergere a fost anulată.");
        }
    }
    
    /**
     * Șterge o sală dacă nu este folosită în niciun curs
     */
    private void stergeSala() {
        System.out.print("ID sală: ");
        String salaId = scanner.nextLine();
        
        // Verificăm dacă sala există
        Optional<Sala> salaOpt = catalogService.getSala(salaId);
        if (!salaOpt.isPresent()) {
            System.out.println("Sala cu ID-ul " + salaId + " nu există!");
            return;
        }
        
        // Verificăm dacă sala poate fi ștearsă
        if (catalogService.canDeleteSala(salaId)) {
            if (catalogService.deleteSala(salaId)) {
                System.out.println("Sala a fost ștearsă cu succes!");
            } else {
                System.out.println("Eroare la ștergerea sălii!");
            }
        } else {
            System.out.println("Sala nu poate fi ștearsă deoarece este folosită în unul sau mai multe cursuri!");
            System.out.println("Trebuie să ștergeți mai întâi cursurile care folosesc această sală.");
        }
    }
    
    /**
     * Șterge o notă specifică
     */
    private void stergeNota() {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();
        
        // Verificăm dacă studentul există
        Optional<Student> studentOpt = catalogService.getStudent(studentId);
        if (!studentOpt.isPresent()) {
            System.out.println("Studentul cu ID-ul " + studentId + " nu există!");
            return;
        }
        
        // Obținem cursurile la care este înscris studentul
        List<Curs> cursuri = catalogService.getCursuriByStudent(studentId);
        if (cursuri.isEmpty()) {
            System.out.println("Studentul nu este înscris la niciun curs!");
            return;
        }
        
        // Afișăm cursurile la care este înscris studentul
        System.out.println("\n=== Cursuri la care este înscris studentul " + studentOpt.get().getNume() + " ===");
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-30s | %-20s | %-15s\n", 
                "ID", "Materie", "Profesor", "Orar");
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
        
        for (Curs curs : cursuri) {
            System.out.printf("%-5s | %-30s | %-20s | %5s - %-5s\n", 
                    curs.getId(),
                    curs.getMaterie().getNume(), 
                    curs.getProfesor().getNume(),
                    curs.getOraInceput().format(TIME_FORMATTER),
                    curs.getOraSfarsit().format(TIME_FORMATTER));
        }
        System.out.println("──────────────────────────────────────────────────────────────────────────────");
        
        // Solicităm ID-ul cursului pentru care se va șterge nota
        System.out.print("ID curs: ");
        String cursId = scanner.nextLine();
        
        // Verificăm dacă studentul este înscris la cursul respectiv
        if (!cursuri.stream().anyMatch(c -> c.getId().equals(cursId))) {
            System.out.println("Studentul nu este înscris la cursul cu ID-ul " + cursId + "!");
            return;
        }
        
        // Obținem notele studentului la cursul respectiv
        List<Nota> note = catalogService.dbService.getNoteByStudentAndCurs(studentId, cursId);
        if (note.isEmpty()) {
            System.out.println("Studentul nu are note la acest curs!");
            return;
        }
        
        // Afișăm notele studentului la cursul respectiv
        System.out.println("\n=== Notele studentului la acest curs ===");
        System.out.println("───────────────────────────────────────");
        System.out.printf("%-5s | %-15s\n", "Notă", "Data");
        System.out.println("───────────────────────────────────────");
        
        for (int i = 0; i < note.size(); i++) {
            Nota nota = note.get(i);
            System.out.printf("%-5.2f | %-15s\n", 
                    nota.getValoare(),
                    nota.getDataAtribuire().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        System.out.println("───────────────────────────────────────");
        
        // Dacă există o singură notă, o ștergem direct
        if (note.size() == 1) {
            if (catalogService.dbService.notaRepository.deleteByStudentAndCursId(studentId, cursId, null) > 0) {
                System.out.println("Nota a fost ștearsă cu succes!");
            } else {
                System.out.println("Eroare la ștergerea notei!");
            }
            return;
        }
        
        // Dacă există mai multe note, solicităm valoarea notei
        System.out.print("Introduceți valoarea notei de șters: ");
        double valoareNota = scanner.nextDouble();
        scanner.nextLine(); // Curăță buffer
        
        if (catalogService.dbService.notaRepository.deleteByStudentAndCursId(studentId, cursId, valoareNota) > 0) {
            System.out.println("Nota a fost ștearsă cu succes!");
        } else {
            System.out.println("Nu s-a găsit o notă cu valoarea " + valoareNota + " pentru cursul specificat!");
        }
    }
    
    /**
     * Afișează toți studenții din baza de date
     */
    public void afiseazaTotiStudentii() {
        List<Student> studenti = catalogService.getStudenti();
        if (studenti.isEmpty()) {
            System.out.println("Nu există studenți înregistrați.");
            return;
        }
        
        // Sortăm studenții alfabetic după nume
        studenti.sort(Comparator.comparing(Student::getNume));
        
        System.out.println("\n=== Lista Studenților ===");
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-30s | %-25s | %-10s\n", "ID", "Nume", "Email", "An Studiu");
        System.out.println("────────────────────────────────────────────────────────────────────");
        
        for (Student student : studenti) {
            System.out.printf("%-5s | %-30s | %-25s | %-10d\n", 
                    student.getId(),
                    student.getNume(), 
                    student.getEmail(),
                    student.getAnStudiu());
        }
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.println("Total studenți: " + studenti.size());
    }
    
    /**
     * Afișează toți profesorii din baza de date
     */
    public void afiseazaTotiProfesorii() {
        Map<String, Profesor> profesoriMap = catalogService.getProfesori();
        if (profesoriMap.isEmpty()) {
            System.out.println("Nu există profesori înregistrați.");
            return;
        }
        
        // Convertim map-ul în listă și sortăm alfabetic după nume
        List<Profesor> profesori = new ArrayList<>(profesoriMap.values());
        profesori.sort(Comparator.comparing(Profesor::getNume));
        
        System.out.println("\n=== Lista Profesorilor ===");
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.printf("%-5s | %-30s | %-25s\n", "ID", "Nume", "Titulatura");
        System.out.println("────────────────────────────────────────────────────────────────────");
        
        for (Profesor profesor : profesori) {
            System.out.printf("%-5s | %-30s | %-25s\n", 
                    profesor.getId(),
                    profesor.getNume(), 
                    profesor.getTitulatura());
        }
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.println("Total profesori: " + profesori.size());
    }
}