USE ResearchCollaborationDB;

-- =============================
-- CLEAR TABLES FIRST (safe order)
-- =============================
DELETE FROM ProfessorKeyword;
DELETE FROM StudentKeyword;
DELETE FROM ProfessorAbstract;
DELETE FROM Abstract;
DELETE FROM Professor;
DELETE FROM Student;
DELETE FROM PublicUser;
DELETE FROM Keyword;
DELETE FROM Account;

-- =============================
-- ACCOUNTS
-- =============================
INSERT INTO Account (username, passwordHash, role)
VALUES 
('prof_jhab', 'pass1', 'Professor'),
('prof_bogaard', 'pass2', 'Professor'),
('stud_jacques', 'pass3', 'Student'),
('stud_jared', 'pass4', 'Student'),
('stud_johnny', 'pass5', 'Student'),
('pub_john', 'pass6', 'Public'),
('pub_jim', 'pass7', 'Public');

-- =============================
-- PROFESSORS
-- =============================
INSERT INTO Professor (firstName, lastName, buildingCode, officeNum, email, phone, accountID)
VALUES
('Jim', 'Habermas', 'GOL', '2673', 'jhabermas@g.rit.edu', '555-555-5555', 1),
('Dan', 'Bogaard', 'GOL', '2120', 'dbogaard@g.rit.edu', '777-777-7777', 2);

-- =============================
-- STUDENTS
-- =============================
INSERT INTO Student (firstName, lastName, major, email, phone, accountID)
VALUES
('Jacques', 'Webster', 'WMC', 'jwIII@rit.edu', '546-896-0987', 3),
('Jared', 'Blazer', 'CIT', 'jb@rit.edu', '546-896-0095', 4),
('Johnny', 'Suit', 'WMC', 'js@rit.edu', '546-896-4532', 5);

-- =============================
-- PUBLIC USERS
-- =============================
INSERT INTO PublicUser (firstName, lastName, organization, email, accountID)
VALUES
('John', 'Galecki', 'Big Bang Theory Org.', 'jg@bang.com', 6),
('Jim', 'Parsons', 'Big Bang Theory Org.', 'jp@bang.com', 7);

-- =============================
-- KEYWORDS (Interests)
-- =============================
INSERT INTO Keyword (interest)
VALUES
('java'),
('machine learning'),
('cybersecurity'),
('web dev'),
('ai'),
('c++'),
('databases'),
('networking');

-- =============================
-- STUDENT KEYWORDS (Interests)
-- =============================
-- Jacques: java, AI
INSERT INTO StudentKeyword (studentID, keywordID) VALUES (1, 1);
INSERT INTO StudentKeyword (studentID, keywordID) VALUES (1, 5);

-- Jared: C++, Web Dev
INSERT INTO StudentKeyword (studentID, keywordID) VALUES (2, 6);
INSERT INTO StudentKeyword (studentID, keywordID) VALUES (2, 4);

-- Johnny: Cybersecurity, Networking
INSERT INTO StudentKeyword (studentID, keywordID) VALUES (3, 3);
INSERT INTO StudentKeyword (studentID, keywordID) VALUES (3, 8);

-- =============================
-- PROFESSOR KEYWORDS
-- =============================
-- Habermas: AI, Databases
INSERT INTO ProfessorKeyword (professorID, keywordID) VALUES (1, 5);
INSERT INTO ProfessorKeyword (professorID, keywordID) VALUES (1, 7);

-- Bogaard: Java, C++
INSERT INTO ProfessorKeyword (professorID, keywordID) VALUES (2, 1);
INSERT INTO ProfessorKeyword (professorID, keywordID) VALUES (2, 6);

-- =============================
-- ABSTRACTS
-- =============================
INSERT INTO Abstract (title, abstractText)
VALUES
('AI in Education', 'Exploring AI-driven student feedback.'),
('Advanced Java Techniques', 'A deep look into Java performance optimizations.');

-- =============================
-- PROFESSOR ABSTRACT LINKS
-- =============================
-- Jim Habermas authored Abstract 1
INSERT INTO ProfessorAbstract (professorID, abstractID, role)
VALUES (1, 1, 'Author');

-- Dan Bogaard authored Abstract 2
INSERT INTO ProfessorAbstract (professorID, abstractID, role)
VALUES (2, 2, 'Author');
