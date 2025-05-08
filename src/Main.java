import Model.*;
import Service.*;

import java.util.Arrays;
import java.util.Scanner;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CatalogService catalogService = new CatalogService();


        while (true) {
            try {
                afiseazaMeniuPrincipal();
                int optiune = scanner.nextInt();
                scanner.nextLine(); // Curăță buffer-ul

                switch (optiune) {
                    case 1:
                        adaugaStudent(scanner, catalogService);
                        break;
                    case 2:
                        adaugaProfesor(scanner, catalogService);
                        break;
                    case 3:
                        adaugaMaterie(scanner, catalogService);
                        break;
                    case 4:
                        adaugaCurs(scanner, catalogService);
                        break;
                    case 5:
                        adaugaSala(scanner, catalogService);
                        break;
                    case 6:
                        inscriereStudentLaCurs(scanner, catalogService);
                        break;
                    case 7:
                        adaugaNota(scanner, catalogService);
                        break;
                    case 8:
                        afiseazaFoaieMatricola(scanner, catalogService);
                        break;
                    case 9:
                        afiseazaProgramStudent(scanner, catalogService);
                        break;
                    case 10:
                        afiseazaMaterie(scanner, catalogService);
                        break;
                    case 11:
                        System.out.println("Ieșire...");
                        System.exit(0);
                    default:
                        System.out.println("Opțiune invalidă!");
                }
            }
            catch (IllegalArgumentException e) {
                System.out.println("Eroare: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Eroare neașteptată: " + e.getMessage());
            }
        }
    }

    private static void adaugaSala(Scanner scanner, CatalogService catalogService) {
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
    private static void afiseazaFoaieMatricola(Scanner scanner, CatalogService catalogService) {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();

        catalogService.getStudent(studentId).ifPresentOrElse(
                student -> {
                    System.out.println("\n=== Foaie Matricolă ===");
                    System.out.println(student);
                    System.out.println("\n=== Cursuri Înscrise ===");
                    catalogService.getCursuriByStudent(studentId).forEach(curs ->
                            System.out.println("- " + curs.getMaterie().getNume() + " (" + curs.getOraInceput() + " - " + curs.getOraSfarsit() + ")")
                    );
                },
                () -> System.out.println("Studentul nu există!")
        );
    }
    private static void adaugaProfesor(Scanner scanner, CatalogService catalogService) {
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

    private static void afiseazaMaterie(Scanner scanner, CatalogService catalogService) {
        System.out.print("Cod materie: ");
        String codMaterie = scanner.nextLine();

        catalogService.getMaterie(codMaterie).ifPresentOrElse(
                materie -> {
                    System.out.println("\n=== Fisa disciplina ===");
                    System.out.println(materie);

                },
                () -> System.out.println("Materia nu exista!")
        );
    }

    private static void adaugaCurs(Scanner scanner, CatalogService service) {
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
            String cursId = service.genereazaIdDisponibil("CRS");
            Materie materie = service.getMaterie(codMaterie)
                    .orElseThrow(() -> new IllegalArgumentException("Materie inexistentă!"));
            Profesor profesor = service.getProfesor(profesorId)
                    .orElseThrow(() -> new IllegalArgumentException("Profesor inexistent!"));
            Sala sala = service.getSala(idSala)
                    .orElseThrow(() -> new IllegalArgumentException("Sala inexistentă!"));
            Curs curs = new Curs(cursId, materie, profesor, sala, start, end);
            service.adaugaCurs(curs);
            System.out.println("Curs adăugat cu ID: " + cursId);
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }
       private static void afiseazaProgramStudent(Scanner scanner, CatalogService service) {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();

        List<Curs> cursuri = service.getCursuriByStudent(studentId);
        if (cursuri.isEmpty()) {
            System.out.println("Studentul nu este înscris la niciun curs.");
        } else {
            System.out.println("Programul studentului:");
            cursuri.forEach(c -> System.out.println(
                    c.getMaterie().getNume() + " - " +
                            c.getOraInceput() + " -> " + c.getOraSfarsit() + ", Sala " + c.getSala()
            ));
        }
    }
    // Metodă pentru înscrierea la curs


    // Metodă pentru afișarea programului unui student
    private static void afiseazaMeniuPrincipal() {
        System.out.println("\n=== Catalog Electronic ===");
        System.out.println("1. Adaugă student");
        System.out.println("2. Adaugă profesor");
        System.out.println("3. Adaugă materie");
        System.out.println("4. Adaugă curs");
        System.out.println("5. Adaugă sala");
        System.out.println("6. Înscrie student la curs");
        System.out.println("7. Adaugă notă");
        System.out.println("8. Afișează foaie matricolă");
        System.out.println("9. Afișează program student");
        System.out.println("10. Afișează Materie");
        System.out.println("11. Ieșire");
        System.out.print("Alege opțiunea: ");
    }
    // Metode auxiliare pentru gestionarea input-ului
    private static void adaugaStudent(Scanner scanner, CatalogService service) {
        System.out.print("Nume student: ");
        String nume = scanner.nextLine();
        System.out.print("Email student: ");
        String email = scanner.nextLine();
        System.out.print("An studiu: ");
        int anStudiu = scanner.nextInt();
        scanner.nextLine(); // Curăță buffer

        try {
            String id = service.genereazaIdDisponibil("S");
            Student student = new Student(nume, id, email, anStudiu);
            service.adaugaStudent(student);
            System.out.println("Student adăugat cu ID: " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }
    private static void adaugaMaterie(Scanner scanner, CatalogService service) {
        System.out.print("Nume materie: ");
        String nume = scanner.nextLine();
        System.out.print("Credite: ");
        int credite = scanner.nextInt();
        scanner.nextLine(); // Curăță buffer

        try {
            String cod = service.genereazaIdDisponibil("MAT");
            Materie materie = new Materie(nume, cod, credite);
            service.adaugaMaterie(materie);
            System.out.println("Materie adăugată cu codul: " + cod);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private static void inscriereStudentLaCurs(Scanner scanner, CatalogService service) {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();
        System.out.print("Cod curs: ");
        String codCurs = scanner.nextLine();

        try {
            Student student = service.getStudent(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student inexistent!"));

            Curs curs = service.getCurs(codCurs)
                    .orElseThrow(() -> new IllegalArgumentException("Curs inexistent!"));

            // Verifică dacă studentul este deja înscris
            if (curs.getStudentiInscrisi().contains(student)) {
                throw new IllegalArgumentException("Studentul este deja înscris la acest curs!");
            }

            // Verifică conflict de orar
            if (areConflictOrar(service,student, curs)) {
                throw new IllegalArgumentException("Studentul are deja un curs în acest interval orar!");
            }
            System.out.println(curs.getSala().getCapacitate());
            System.out.println(curs.getStudentiInscrisi().size());
            if(curs.getSala().getCapacitate() < curs.getStudentiInscrisi().size()+1){
                throw new IllegalArgumentException("Nu mai este loc in sala");
            }
            curs.inscriereStudent(student);
            student.adaugaInscriere(new Inscriere(student, curs));
            System.out.println("Înscriere reușită: " + student.getNume() + " la " + curs.getMaterie().getNume());

        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    // Helper method pentru verificarea conflictului de orar
    private static boolean areConflictOrar(CatalogService service, Student student, Curs cursNou) {
        return service.getCursuriByStudent(student.getId()).stream()
                .anyMatch(cursExist -> cursExist.getOraInceput().isBefore(cursNou.getOraSfarsit())
                        && cursExist.getOraSfarsit().isAfter(cursNou.getOraInceput()));
    }

    private static void adaugaNota(Scanner scanner, CatalogService service) {
        System.out.print("ID student: ");
        String studentId = scanner.nextLine();
        System.out.print("Cod Curs: ");
        String codCurs = scanner.nextLine();
        System.out.print("Nota: ");
        double nota = scanner.nextDouble();

        try {
            service.adaugaNota(studentId, codCurs, nota);

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}