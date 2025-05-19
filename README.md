
# Catalog Electronic

Aplicație de gestionare a catalogului electronic pentru universități/facultăți.

## Descriere

Aplicația permite:
- Adăugarea și gestionarea studenților
- Adăugarea și gestionarea profesorilor
- Adăugarea și gestionarea materiilor
- Adăugarea și gestionarea cursurilor
- Înscrierea studenților la cursuri
- Adăugarea notelor pentru studenți
- Afișarea foii matricole
- Afișarea programului unui student

## Structura proiectului

- `src/Model/` - Conține clasele de model (Student, Profesor, Materie, etc.)
- `src/Service/` - Conține serviciile aplicației
- `src/Repository/` - Conține clasele pentru interacțiunea cu baza de date
- `src/resources/` - Conține fișiere de configurare și script-uri SQL

## Configurare Bază de Date

### Varianta 1: Folosirea H2 Database (implicită)

H2 este o bază de date încorporată care nu necesită instalare separată. Este configurată implicit în acest proiect.

1. Maven va descărca automat dependența H2 la prima rulare
2. Baza de date va fi creată automat la pornirea aplicației în directorul principal al proiectului (`catalogdb.mv.db`)
3. Tabelele vor fi create automat de către aplicație la prima rulare

### Varianta 2: Folosirea PostgreSQL

1. Instalați PostgreSQL (dacă nu aveți deja instalat)
2. Creați o bază de date nouă:
   ```sql
   CREATE DATABASE catalog;
   ```
3. Creați un utilizator pentru aplicație:
   ```sql
   CREATE USER app_user WITH PASSWORD 'parola_ta_puternica';
   GRANT ALL PRIVILEGES ON DATABASE catalog TO app_user;
   ```
4. Conectați-vă la baza de date:
   ```bash
   psql -U app_user -d catalog
   ```
5. Rulați script-ul de inițializare a bazei de date (init_db.sql):
   ```bash
   psql -U app_user -d catalog -f src/resources/init_db.sql
   ```
6. Modificați fișierul `src/resources/db.properties` pentru a configura conexiunea la PostgreSQL:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/catalog
   db.user=app_user
   db.password=parola_ta_puternica
   db.driver=org.postgresql.Driver
   ```
7. Adăugați dependența PostgreSQL în pom.xml:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <version>42.6.0</version>
   </dependency>
   ```

## Dependențe

Pentru a executa aplicația, aveți nevoie de:
- Java JDK 8 sau mai recent
- Maven (pentru gestionarea dependențelor)

## Rulare

### Cu Maven
```bash
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

### Din IDE
Deschideți proiectul în IDE-ul preferat (IntelliJ IDEA, Eclipse, etc.) și rulați clasa Main.

## Diagrama de Clase

Aplicația conține următoarele clase principale:
- `Utilizator` - Clasă abstractă pentru utilizatorii sistemului
- `Student` - Extinde Utilizator și reprezintă un student
- `Profesor` - Extinde Utilizator și reprezintă un profesor
- `Materie` - Reprezintă o materie de studiu
- `Curs` - Reprezintă un curs cu o materie, profesor și sală
- `Sala` - Reprezintă o sală de curs
- `Nota` - Reprezintă o notă primită de un student la un curs
- `Inscriere` - Reprezintă înscrierea unui student la un curs
- `Departament` - Reprezintă un departament care conține profesori și materii

## Implementarea persistenței

Aplicația utilizează JDBC pentru a stoca și recupera date dintr-o bază de date. Implementarea include:
- Servicii singleton pentru gestionarea conexiunilor la baza de date
- Repository pentru fiecare entitate din sistem
- Operații CRUD (Create, Read, Update, Delete) pentru fiecare entitate
- Un serviciu de audit care înregistrează toate acțiunile importante efectuate în aplicație