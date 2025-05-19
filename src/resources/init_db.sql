-- Create tables for Catalog Electronic system

-- Studenti table
CREATE TABLE IF NOT EXISTS studenti (
    id VARCHAR(50) PRIMARY KEY,
    nume VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    an_studiu INT NOT NULL
);

-- Profesori table
CREATE TABLE IF NOT EXISTS profesori (
    id VARCHAR(50) PRIMARY KEY,
    nume VARCHAR(100) NOT NULL,
    titulatura VARCHAR(100) NOT NULL
);

-- Materii table
CREATE TABLE IF NOT EXISTS materii (
    cod VARCHAR(50) PRIMARY KEY,
    nume VARCHAR(100) NOT NULL,
    credite INT NOT NULL
);

-- Sali table
CREATE TABLE IF NOT EXISTS sali (
    id VARCHAR(50) PRIMARY KEY,
    nume VARCHAR(100) NOT NULL,
    capacitate INT NOT NULL,
    facilitati TEXT NOT NULL
);

-- Cursuri table
CREATE TABLE IF NOT EXISTS cursuri (
    id VARCHAR(50) PRIMARY KEY,
    materie_cod VARCHAR(50) NOT NULL,
    profesor_id VARCHAR(50) NOT NULL,
    sala_id VARCHAR(50) NOT NULL,
    ora_inceput TIME NOT NULL,
    ora_sfarsit TIME NOT NULL,
    FOREIGN KEY (materie_cod) REFERENCES materii(cod),
    FOREIGN KEY (profesor_id) REFERENCES profesori(id),
    FOREIGN KEY (sala_id) REFERENCES sali(id)
);

-- Inscrieri table
CREATE TABLE IF NOT EXISTS inscrieri (
    student_id VARCHAR(50) NOT NULL,
    curs_id VARCHAR(50) NOT NULL,
    data_inscriere DATE NOT NULL,
    PRIMARY KEY (student_id, curs_id),
    FOREIGN KEY (student_id) REFERENCES studenti(id),
    FOREIGN KEY (curs_id) REFERENCES cursuri(id)
);

-- Note table
CREATE TABLE IF NOT EXISTS note (
    id SERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    curs_id VARCHAR(50) NOT NULL,
    valoare DOUBLE PRECISION NOT NULL,
    data_atribuire DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES studenti(id),
    FOREIGN KEY (curs_id) REFERENCES cursuri(id)
);

-- Departamente table
CREATE TABLE IF NOT EXISTS departamente (
    cod VARCHAR(50) PRIMARY KEY,
    nume VARCHAR(100) NOT NULL
);

-- Relatie departament-profesor
CREATE TABLE IF NOT EXISTS departament_profesor (
    departament_cod VARCHAR(50) NOT NULL,
    profesor_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (departament_cod, profesor_id),
    FOREIGN KEY (departament_cod) REFERENCES departamente(cod),
    FOREIGN KEY (profesor_id) REFERENCES profesori(id)
);

-- Relatie departament-materie
CREATE TABLE IF NOT EXISTS departament_materie (
    departament_cod VARCHAR(50) NOT NULL,
    materie_cod VARCHAR(50) NOT NULL,
    PRIMARY KEY (departament_cod, materie_cod),
    FOREIGN KEY (departament_cod) REFERENCES departamente(cod),
    FOREIGN KEY (materie_cod) REFERENCES materii(cod)
);