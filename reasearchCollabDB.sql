-- researchCollabDB_fixed_and_seed.sql
DROP DATABASE IF EXISTS ResearchCollaborationDB;
CREATE DATABASE ResearchCollaborationDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ResearchCollaborationDB;

-- Account table (central login system for all user types)
CREATE TABLE Account (
    accountID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    passwordHash VARCHAR(200) NOT NULL,
    role ENUM('Professor', 'Student', 'Public') NOT NULL
);

-- Professor table
CREATE TABLE Professor (
    professorID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(30),
    lastName VARCHAR(50),
    buildingCode VARCHAR(10),
    officeNum VARCHAR(10),
    email VARCHAR(100),
    phone VARCHAR(20),
    accountID INT,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE SET NULL
);

-- Abstracts
CREATE TABLE Abstract (
    abstractID INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    abstractText VARCHAR(1000)
);

-- Professor <-> Abstract
CREATE TABLE ProfessorAbstract (
    professorID INT NOT NULL,
    abstractID INT NOT NULL,
    authorRole VARCHAR(50),
    PRIMARY KEY (professorID, abstractID),
    FOREIGN KEY (professorID) REFERENCES Professor(professorID) ON DELETE CASCADE,
    FOREIGN KEY (abstractID) REFERENCES Abstract(abstractID) ON DELETE CASCADE
);

-- Student
CREATE TABLE Student (
    studentID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(30),
    lastName VARCHAR(50),
    major VARCHAR(60),
    email VARCHAR(100),
    phone VARCHAR(20),
    accountID INT,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE SET NULL
);

-- Keywords
CREATE TABLE Keyword (
    keywordID INT AUTO_INCREMENT PRIMARY KEY,
    term VARCHAR(100) UNIQUE NOT NULL
);

-- ProfessorKeyword
CREATE TABLE ProfessorKeyword (
    professorID INT NOT NULL,
    keywordID INT NOT NULL,
    PRIMARY KEY (professorID, keywordID),
    FOREIGN KEY (professorID) REFERENCES Professor(professorID) ON DELETE CASCADE,
    FOREIGN KEY (keywordID) REFERENCES Keyword(keywordID) ON DELETE CASCADE
);

-- StudentKeyword
CREATE TABLE StudentKeyword (
    studentID INT NOT NULL,
    keywordID INT NOT NULL,
    PRIMARY KEY (studentID, keywordID),
    FOREIGN KEY (studentID) REFERENCES Student(studentID) ON DELETE CASCADE,
    FOREIGN KEY (keywordID) REFERENCES Keyword(keywordID) ON DELETE CASCADE
);

-- PublicUser
CREATE TABLE PublicUser (
    publicID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(30),
    lastName VARCHAR(50),
    organization VARCHAR(100),
    email VARCHAR(100),
    accountID INT,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE SET NULL
);

-- PublicKeyword (bridge)
CREATE TABLE PublicKeyword (
    publicID INT NOT NULL,
    keywordID INT NOT NULL,
    PRIMARY KEY (publicID, keywordID),
    FOREIGN KEY (publicID) REFERENCES PublicUser(publicID) ON DELETE CASCADE,
    FOREIGN KEY (keywordID) REFERENCES Keyword(keywordID) ON DELETE CASCADE
);

-------------------------
-- Seed data (sample accounts + users + keywords + links)
-------------------------

-- Accounts for Professors (role='Professor')
INSERT INTO Account (username, passwordHash, role) VALUES
  ('prof1', SHA1('Prof1pass'), 'Professor'),
  ('prof2', SHA1('Prof2pass'), 'Professor');

-- Accounts for Students (role='Student')
INSERT INTO Account (username, passwordHash, role) VALUES
  ('stud1', SHA1('Stud1pass'), 'Student'),
  ('stud2', SHA1('Stud2pass'), 'Student');

-- Accounts for Public users (role='Public')
INSERT INTO Account (username, passwordHash, role) VALUES
  ('pub1', SHA1('Public1pass'), 'Public'),
  ('pub2', SHA1('Public2pass'), 'Public');

-- Insert Professors and link accountIDs via subquery
INSERT INTO Professor (firstName, lastName, buildingCode, officeNum, email, phone, accountID)
VALUES
 ('Alice', 'Anderson', 'B1', '101', 'alice.anderson@rit.edu','555-0101',
    (SELECT accountID FROM Account WHERE username='prof1')),
 ('Bob', 'Brown', 'B2', '202', 'bob.brown@rit.edu','555-0102',
    (SELECT accountID FROM Account WHERE username='prof2'));

-- Insert Students and link accounts
INSERT INTO Student (firstName, lastName, major, email, phone, accountID)
VALUES
 ('Carol', 'Clark', 'CS', 'carol.clark@rit.edu','555-0201',
    (SELECT accountID FROM Account WHERE username='stud1')),
 ('Dave', 'Davis', 'IS', 'dave.davis@rit.edu','555-0202',
    (SELECT accountID FROM Account WHERE username='stud2'));

-- Insert Public users and link accounts
INSERT INTO PublicUser (firstName, lastName, organization, email, accountID)
VALUES
 ('Eve', 'Evans', 'Henrietta Library', 'eve.evans@library.org',
    (SELECT accountID FROM Account WHERE username='pub1')),
 ('Frank', 'Foster', 'Local Media', 'frank.foster@media.org',
    (SELECT accountID FROM Account WHERE username='pub2'));

-- Insert some keywords
INSERT INTO Keyword (term) VALUES
 ('machine learning'),
 ('cybersecurity'),
 ('databases'),
 ('human-computer interaction'),
 ('computer vision');

-- Link keywords to professors
INSERT IGNORE INTO ProfessorKeyword (professorID, keywordID)
VALUES
 ((SELECT professorID FROM Professor WHERE firstName='Alice' AND lastName='Anderson'), (SELECT keywordID FROM Keyword WHERE term='machine learning')),
 ((SELECT professorID FROM Professor WHERE firstName='Alice' AND lastName='Anderson'), (SELECT keywordID FROM Keyword WHERE term='human-computer interaction')),
 ((SELECT professorID FROM Professor WHERE firstName='Bob' AND lastName='Brown'), (SELECT keywordID FROM Keyword WHERE term='cybersecurity')),
 ((SELECT professorID FROM Professor WHERE firstName='Bob' AND lastName='Brown'), (SELECT keywordID FROM Keyword WHERE term='databases'));

-- Link keywords to students
INSERT IGNORE INTO StudentKeyword (studentID, keywordID)
VALUES
 ((SELECT studentID FROM Student WHERE firstName='Carol' AND lastName='Clark'), (SELECT keywordID FROM Keyword WHERE term='machine learning')),
 ((SELECT studentID FROM Student WHERE firstName='Dave' AND lastName='Davis'), (SELECT keywordID FROM Keyword WHERE term='databases'));

-- Link keywords to public users
INSERT IGNORE INTO PublicKeyword (publicID, keywordID)
VALUES
 ((SELECT publicID FROM PublicUser WHERE firstName='Eve' AND lastName='Evans'), (SELECT keywordID FROM Keyword WHERE term='human-computer interaction')),
 ((SELECT publicID FROM PublicUser WHERE firstName='Frank' AND lastName='Foster'), (SELECT keywordID FROM Keyword WHERE term='cybersecurity'));

-- Sample abstracts and linking (optional)
INSERT INTO Abstract (title, abstractText) VALUES
 ('Deep Learning for Time Series', 'Short abstract about DL and time series.'),
 ('Secure Systems Design', 'Abstract about security best practices in system design.');

INSERT INTO ProfessorAbstract (professorID, abstractID, authorRole)
VALUES
 ((SELECT professorID FROM Professor WHERE firstName='Alice' AND lastName='Anderson'), (SELECT abstractID FROM Abstract WHERE title='Deep Learning for Time Series'), 'Author'),
 ((SELECT professorID FROM Professor WHERE firstName='Bob' AND lastName='Brown'), (SELECT abstractID FROM Abstract WHERE title='Secure Systems Design'), 'Author');

-- End of seed
