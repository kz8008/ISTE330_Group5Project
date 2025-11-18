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
    abstractText TEXT,
    filePath VARCHAR(255) NULL,      -- optional: file stored on disk; store path here
    uploadedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Professor <-> Abstract many to many
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
    interest VARCHAR(100) NOT NULL UNIQUE
);

-- Professor <-> Keyword
CREATE TABLE ProfessorKeyword (
    professorID INT NOT NULL,
    keywordID INT NOT NULL,
    PRIMARY KEY (professorID, keywordID),
    FOREIGN KEY (professorID) REFERENCES Professor(professorID) ON DELETE CASCADE,
    FOREIGN KEY (keywordID) REFERENCES Keyword(keywordID) ON DELETE CASCADE
);

-- Student <-> Keyword
CREATE TABLE StudentKeyword (
    studentID INT NOT NULL,
    keywordID INT NOT NULL,
    PRIMARY KEY (studentID, keywordID),
    FOREIGN KEY (studentID) REFERENCES Student(studentID) ON DELETE CASCADE,
    FOREIGN KEY (keywordID) REFERENCES Keyword(keywordID) ON DELETE CASCADE
);

-- Public user (guest)
CREATE TABLE PublicUser (
    publicID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(30),
    lastName VARCHAR(50),
    organization VARCHAR(100),
    email VARCHAR(100),
    accountID INT,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE SET NULL
);
