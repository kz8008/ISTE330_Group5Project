// Group 5 - Final 
// Base: your MainDataLayer.java
// - Password encryption via encryptPassword(...) wrapper (currently SHA-256)

import java.sql.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class MainDataLayer {

  
    private Connection conn;

    // ---------------------------
    // DB CONNECTION
    // ---------------------------
    public boolean connect() {
        conn = null;
        String databaseName = "ResearchCollaborationDB";
        String userName = "root";
        String password = "student";

        String url = "jdbc:mysql://localhost/" + databaseName + "?serverTimezone=UTC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, userName, password);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR CONNECTING TO DATABASE.");
            System.out.println("MESSAGE: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------
    // Password encryption wrapper
    // ---------------------------
    // NOTE: professor's encryption routine can be plugged in here.
    // Right now this uses SHA-256 (secure). To use the professor's encrypt method,
    // replace the body of encryptPassword with a call to that routine.
    public String encryptPassword(String plain) {
        if (plain == null) return null;
        // SHA-256 implementation (safe fallback / default)
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR encryptPassword: algorithm not found.");
            return null;
        }
    }

    // ---------------------------
    // Account methods
    // ---------------------------
    public int registerAccount(String username, String passwordPlain, String role) {
        int accountId = -1;
        String sql = "INSERT INTO Account (username, passwordHash, role) VALUES (?, ?, ?)";

        String hashed = encryptPassword(passwordPlain);
        if (hashed == null) {
            System.out.println("ERROR registerAccount: could not hash password.");
            return -1;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.setString(3, role);
            int updated = ps.executeUpdate();
            if (updated == 0) return -1;
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) accountId = gk.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("ERROR registerAccount: " + e.getMessage());
        }
        return accountId;
    }

    public int authenticateAccount(String username, String passwordPlain) {
        String sql = "SELECT accountID, passwordHash FROM Account WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) {
                    String storedHash = r.getString("passwordHash");
                    String suppliedHash = encryptPassword(passwordPlain);
                    if (suppliedHash != null && suppliedHash.equals(storedHash)) {
                        return r.getInt("accountID");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR authenticateAccount: " + e.getMessage());
        }
        return -1;
    }

    public String getRoleByAccountId(int accountId) {
        String sql = "SELECT role FROM Account WHERE accountID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) return r.getString("role");
            }
        } catch (SQLException e) {
            System.out.println("ERROR getRoleByAccountId: " + e.getMessage());
        }
        return null;
    }

    // ---------------------------
    // Professor CRUD & helpers
    // ---------------------------
    public int addProfessor(int professorID, String fname, String lname, String buildingCode, String officeNum, String email, String phone, Integer accountID) {
        try {
            if (professorID > 0) {
                String sql = "INSERT INTO Professor (professorID, firstName, lastName, buildingCode, officeNum, email, phone, accountID) VALUES (?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, professorID);
                    ps.setString(2, fname);
                    ps.setString(3, lname);
                    ps.setString(4, buildingCode);
                    ps.setString(5, officeNum);
                    ps.setString(6, email);
                    ps.setString(7, phone);
                    if (accountID != null) ps.setInt(8, accountID); else ps.setNull(8, Types.INTEGER);
                    return ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO Professor (firstName, lastName, buildingCode, officeNum, email, phone, accountID) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, fname);
                    ps.setString(2, lname);
                    ps.setString(3, buildingCode);
                    ps.setString(4, officeNum);
                    ps.setString(5, email);
                    ps.setString(6, phone);
                    if (accountID != null) ps.setInt(7, accountID); else ps.setNull(7, Types.INTEGER);
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        try (ResultSet gk = ps.getGeneratedKeys()) {
                            if (gk.next()) return gk.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR addProfessor: " + e.getMessage());
        }
        return 0;
    }

    public int updateProfessor(int professorID, String buildingCode, String officeNum) {
        String sql = "UPDATE Professor SET buildingCode = ?, officeNum = ? WHERE professorID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, buildingCode);
            ps.setString(2, officeNum);
            ps.setInt(3, professorID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR updateProfessor: " + e.getMessage());
            return 0;
        }
    }

    public int deleteProfessor(int professorID) {
        String sql = "DELETE FROM Professor WHERE professorID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, professorID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR deleteProfessor: " + e.getMessage());
            return 0;
        }
    }

    // ---------------------------
    // Abstract CRUD & linking
    // ---------------------------
    public int addAbstract(int abstractID, String title, String abstractText, String filePath) {
        try {
            if (abstractID > 0) {
                String sql = "INSERT INTO Abstract (abstractID, title, abstractText, filePath) VALUES (?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, abstractID);
                    ps.setString(2, title);
                    ps.setString(3, abstractText);
                    if (filePath != null) ps.setString(4, filePath); else ps.setNull(4, Types.VARCHAR);
                    return ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO Abstract (title, abstractText, filePath) VALUES (?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, title);
                    ps.setString(2, abstractText);
                    if (filePath != null) ps.setString(3, filePath); else ps.setNull(3, Types.VARCHAR);
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        try (ResultSet gk = ps.getGeneratedKeys()) {
                            if (gk.next()) return gk.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR addAbstract: " + e.getMessage());
        }
        return 0;
    }

    public int updateAbstract(int abstractID, String title, String abstractText) {
        String sql = "UPDATE Abstract SET title = ?, abstractText = ? WHERE abstractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, abstractText);
            ps.setInt(3, abstractID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR updateAbstract: " + e.getMessage());
            return 0;
        }
    }
    
    public boolean updateAbstractIfOwned(int professorID, int abstractID, String newTitle, String newText) {
    String verifySql = "SELECT * FROM ProfessorAbstract WHERE professorID = ? AND abstractID = ?";
    String updateSql = "UPDATE Abstract SET title = ?, abstractText = ? WHERE abstractID = ?";

    try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
        verifyStmt.setInt(1, professorID);
        verifyStmt.setInt(2, abstractID);
        ResultSet rs = verifyStmt.executeQuery();

        if (!rs.next()) {
            System.out.println("You do not own this abstract.");
            return false;
        }
    } catch (Exception e) {
        System.out.println("ERROR verifying abstract ownership.");
        return false;
    }

    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
        updateStmt.setString(1, newTitle);
        updateStmt.setString(2, newText);
        updateStmt.setInt(3, abstractID);

        return updateStmt.executeUpdate() > 0;
    } catch (Exception e) {
        System.out.println("ERROR updating abstract.");
        return false;
    }
}


    public int deleteAbstract(int abstractID) {
        String sql = "DELETE FROM Abstract WHERE abstractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, abstractID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR deleteAbstract: " + e.getMessage());
            return 0;
        }
    }
    
    public boolean deleteAbstractIfOwned(int professorID, int abstractID) {
    String verifySql = "SELECT * FROM ProfessorAbstract WHERE professorID = ? AND abstractID = ?";

    try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
        verifyStmt.setInt(1, professorID);
        verifyStmt.setInt(2, abstractID);
        ResultSet rs = verifyStmt.executeQuery();

        if (!rs.next()) {
            System.out.println("You do not own this abstract.");
            return false;
        }
    } catch (Exception e) {
        System.out.println("ERROR verifying abstract ownership.");
        return false;
    }

    String deleteSql = "DELETE FROM Abstract WHERE abstractID = ?";
    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
        deleteStmt.setInt(1, abstractID);
        return deleteStmt.executeUpdate() > 0;
    } catch (Exception e) {
        System.out.println("ERROR deleting abstract.");
        return false;
    }
}


    public int addProfessorAbstract(int professorID, int abstractID, String authorRole) {
        String sql = "INSERT INTO ProfessorAbstract (professorID, abstractID, authorRole) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, professorID);
            ps.setInt(2, abstractID);
            ps.setString(3, authorRole);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR addProfessorAbstract: " + e.getMessage());
            return 0;
        }
    }

    // ---------------------------
    // Keyword helpers
    // ---------------------------
    // ensureKeyword: finds existing keyword by exact match (case-insensitive) or inserts and returns id
    public int ensureKeyword(String interest) {
        if (interest == null || interest.isBlank()) return -1;
        String find = "SELECT keywordID FROM Keyword WHERE LOWER(interest) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(find)) {
            ps.setString(1, interest.trim());
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) return r.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("ERROR ensureKeyword (find): " + e.getMessage());
            return -1;
        }

        // not found -> insert
        String ins = "INSERT INTO Keyword (interest) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, interest.trim());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) return gk.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR ensureKeyword (insert): " + e.getMessage());
        }
        return -1;
    }

    // getKeywordIdByName: find id but do not create new keyword (useful for delete/search)
    public int getKeywordIdByName(String interest) {
        if (interest == null || interest.isBlank()) return -1;
        String sql = "SELECT keywordID FROM Keyword WHERE LOWER(interest) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, interest.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("ERROR getKeywordIdByName: " + e.getMessage());
        }
        return -1;
    }

    public int addProfessorKeyword(int professorID, int keywordID) {
        String sql = "INSERT INTO ProfessorKeyword (professorID, keywordID) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, professorID);
            ps.setInt(2, keywordID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR addProfessorKeyword: " + e.getMessage());
            return 0;
        }
    }

    public int addStudentKeyword(int studentID, int keywordID) {
        String sql = "INSERT INTO StudentKeyword (studentID, keywordID) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentID);
            ps.setInt(2, keywordID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR addStudentKeyword: " + e.getMessage());
            return 0;
        }
    }

    // delete student_keyword 
    public int deleteStudentKeyword(int studentID, int keywordID) {
        int rows = 0;
        String sql = "DELETE FROM StudentKeyword WHERE studentID = ? AND keywordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentID);
            ps.setInt(2, keywordID);
            rows = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL ERROR -> deleteStudentKeyword()");
            System.out.println("MESSAGE: " + e.getMessage());
        }
        return rows;
    }

    // ---------------------------
    // Student CRUD
    // ---------------------------
    public int addStudent(int studentID, String firstName, String lastName, String major, String email, String phone, Integer accountID) {
        try {
            if (studentID > 0) {
                String sql = "INSERT INTO Student (studentID, firstName, lastName, major, email, phone, accountID) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, studentID);
                    ps.setString(2, firstName);
                    ps.setString(3, lastName);
                    ps.setString(4, major);
                    ps.setString(5, email);
                    ps.setString(6, phone);
                    if (accountID != null) ps.setInt(7, accountID); else ps.setNull(7, Types.INTEGER);
                    return ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO Student (firstName, lastName, major, email, phone, accountID) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, firstName);
                    ps.setString(2, lastName);
                    ps.setString(3, major);
                    ps.setString(4, email);
                    ps.setString(5, phone);
                    if (accountID != null) ps.setInt(6, accountID); else ps.setNull(6, Types.INTEGER);
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        try (ResultSet gk = ps.getGeneratedKeys()) {
                            if (gk.next()) return gk.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR addStudent: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------
    // Search students by interest 
    // ---------------------------
    public List<String> searchStudentByInterest(String interest) {
        List<String> results = new ArrayList<>();
        if (interest == null || interest.isBlank()) return results;

        String sql =
            "SELECT s.firstName, s.lastName, s.major, s.email " +
            "FROM Student s " +
            "JOIN StudentKeyword sk ON s.studentID = sk.studentID " +
            "JOIN Keyword k ON sk.keywordID = k.keywordID " +
            "WHERE LOWER(k.interest) = LOWER(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, interest.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fn = rs.getString("firstName");
                    String ln = rs.getString("lastName");
                    String major = rs.getString("major");
                    String email = rs.getString("email");
                    results.add(fn + " " + ln + " | Major: " + major + " | Email: " + email);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR -> searchStudentByInterest()");
            System.out.println("MESSAGE: " + e.getMessage());
        }
        return results;
    }

    // ---------------------------
    // Public user
    // ---------------------------
    public int addPublicUser(String firstName, String lastName, String organization, String email, Integer accountID) {
        String sql = "INSERT INTO PublicUser (firstName, lastName, organization, email, accountID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, organization);
            ps.setString(4, email);
            if (accountID != null) ps.setInt(5, accountID); else ps.setNull(5, Types.INTEGER);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) return gk.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR addPublicUser: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------
    // Finders
    // ---------------------------
    public int findProfessorIdByAccountId(int accountID) {
        String sql = "SELECT professorID FROM Professor WHERE accountID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountID);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) return r.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("ERROR findProfessorIdByAccountId: " + e.getMessage());
        }
        return -1;
    }

    public int findStudentIdByAccountId(int accountID) {
        String sql = "SELECT studentID FROM Student WHERE accountID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountID);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) return r.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("ERROR findStudentIdByAccountId: " + e.getMessage());
        }
        return -1;
    }

    // ---------------------------
    // Matching & search results (returns lists)
    // ---------------------------
    public List<String> findMatchingFaculty(int studentID) {
        List<String> out = new ArrayList<>();
        String sql = "SELECT DISTINCT p.firstName, p.lastName, p.buildingCode, p.officeNum, p.email " +
                     "FROM Professor p JOIN ProfessorKeyword pk ON p.professorID = pk.professorID " +
                     "JOIN StudentKeyword sk ON pk.keywordID = sk.keywordID WHERE sk.studentID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentID);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    String line = String.format("%s %s | Building %s | Office %s | %s",
                            r.getString(1), r.getString(2),
                            r.getString(3), r.getString(4), r.getString(5));
                    out.add(line);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR findMatchingFaculty: " + e.getMessage());
        }
        return out;
    }

    public List<String> searchFacultyByKeyword(String interest) {
        List<String> out = new ArrayList<>();
        String sql = "SELECT DISTINCT p.firstName, p.lastName, p.email FROM Professor p " +
                     "JOIN ProfessorKeyword pk ON p.professorID = pk.professorID " +
                     "JOIN Keyword k ON pk.keywordID = k.keywordID WHERE k.interest LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + interest + "%");
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    out.add(String.format("%s %s <%s>", r.getString(1), r.getString(2), r.getString(3)));
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR searchFacultyByKeyword: " + e.getMessage());
        }
        return out;
    }

    public List<String> listAllAbstracts() {
        List<String> out = new ArrayList<>();
        String sql = "SELECT a.abstractID, a.title, a.abstractText, a.filePath, GROUP_CONCAT(CONCAT(p.firstName, ' ', p.lastName) SEPARATOR '; ') AS authors " +
                     "FROM Abstract a LEFT JOIN ProfessorAbstract pa ON a.abstractID = pa.abstractID " +
                     "LEFT JOIN Professor p ON pa.professorID = p.professorID " +
                     "GROUP BY a.abstractID, a.title, a.abstractText, a.filePath ORDER BY a.abstractID";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet r = ps.executeQuery()) {
            while (r.next()) {
                int id = r.getInt("abstractID");
                String t = r.getString("title");
                String txt = r.getString("abstractText");
                String fp = r.getString("filePath");
                String authors = r.getString("authors");
                out.add(String.format("%d: %s\n   %s\n   Authors: %s\n   File: %s", id, t, txt, (authors==null?"(none)":authors), (fp==null?"(none)":fp)));
            }
        } catch (SQLException e) {
            System.out.println("ERROR listAllAbstracts: " + e.getMessage());
        }
        return out;
    }

    // ---------------------------
    // Close
    // ---------------------------
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            // ignore
        }
    }
}
