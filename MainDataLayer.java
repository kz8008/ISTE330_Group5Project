// MainDataLayer.java
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

         System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------
    // Password encryption wrapper
    // ---------------------------
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
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    private void logError(Exception e, String context) {
    System.out.println("An error occurred while processing your request.");
    System.out.println("(Details: " + context + ")");
}

    // ---------------------------
    // Account methods
    // ---------------------------
    public int registerAccount(String username, String passwordPlain, String role) {
        int accountId = -1;
        String sql = "INSERT INTO Account (username, passwordHash, role) VALUES (?, ?, ?)";

        String hashed = encryptPassword(passwordPlain);
        if (hashed == null) {
            // log and return failure
         System.out.println("An error occurred while registering the account.");


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
            logError(e, "registerAccount()");
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
            logError(e, "authenticateAccount()");
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
            logError(e, "getRoleByAccountId()");
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
            logError(e, "addProfessor()");
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
            logError(e, "updateProfessor()");
            return 0;
        }
    }

    public int deleteProfessor(int professorID) {
        String sql = "DELETE FROM Professor WHERE professorID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, professorID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            logError(e, "deleteProfessor()");
            return 0;
        }
    }

    // ---------------------------
    // Abstract CRUD & linking
    // ---------------------------
    public int addAbstract(int abstractID, String title, String abstractText, String filePath) {
        try {
            if (abstractID > 0) {
                String sql = "INSERT INTO Abstract (abstractID, title, abstractText) VALUES (?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, abstractID);
                    ps.setString(2, title);
                    ps.setString(3, abstractText);
                    return ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO Abstract (title, abstractText) VALUES (?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, title);
                    ps.setString(2, abstractText);
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        try (ResultSet gk = ps.getGeneratedKeys()) {
                            if (gk.next()) return gk.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logError(e, "addAbstract()");
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
            logError(e, "updateAbstract()");
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
                return false;
            }
        } catch (Exception e) {
            logError(e, "updateAbstractIfOwned() - verify");
            return false;
        }

        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, newTitle);
            updateStmt.setString(2, newText);
            updateStmt.setInt(3, abstractID);

            return updateStmt.executeUpdate() > 0;
        } catch (Exception e) {
            logError(e, "updateAbstractIfOwned() - update");
            return false;
        }
    }

    public int deleteAbstract(int abstractID) {
        String sql = "DELETE FROM Abstract WHERE abstractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, abstractID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            logError(e, "deleteAbstract()");
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
                return false;
            }
        } catch (Exception e) {
            logError(e, "deleteAbstractIfOwned() - verify");
            return false;
        }

        String deleteSql = "DELETE FROM Abstract WHERE abstractID = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, abstractID);
            return deleteStmt.executeUpdate() > 0;
        } catch (Exception e) {
            logError(e, "deleteAbstractIfOwned() - delete");
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
            logError(e, "addProfessorAbstract()");
            return 0;
        }
    }

    // will be able to find an abstract based 
    public int getAbstractByID(int abstractID) {
        String sql = "SELECT title, abstractText FROM abstract WHERE abstractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, abstractID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logError(e, "getAbstractByID()");
        }
        return -1;
    }

    // ---------------------------
    // Keyword helpers
    // ---------------------------
    // ensureKeyword: finds existing keyword by exact match (case-insensitive) or inserts
    public int ensureKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return -1;

        String selectQuery = "SELECT keywordID FROM Keyword WHERE LOWER(term) = LOWER(?)";
        String insertQuery = "INSERT INTO Keyword (term) VALUES (?)";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, keyword.trim());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("keywordID");  // Found existing keyword
            }

            // If not found, insert new keyword
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, keyword.trim());
                int updated = insertStmt.executeUpdate();
                if (updated > 0) {
                    ResultSet keys = insertStmt.getGeneratedKeys();
                    if (keys.next()) {
                        return keys.getInt(1);  // Return new keywordID
                    }
                }
            }

        } catch (SQLException e) {
            logError(e, "ensureKeyword()");
        }

        return -1;  // Only if something unexpected goes wrong
    }

    // getKeywordIdByName: find id but do not create new keyword (useful for delete/search)
    public int getKeywordIdByName(String interest) {
        if (interest == null || interest.isBlank()) return -1;
        String sql = "SELECT keywordID FROM Keyword WHERE LOWER(term) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, interest.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logError(e, "getKeywordIdByName()");
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
            logError(e, "addProfessorKeyword()");
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
            logError(e, "addStudentKeyword()");
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
            logError(e, "deleteStudentKeyword()");
        }
        return rows;
    }

    public List<String> viewProfessorInterests(int professorID) {
        List<String> out = new ArrayList<>();

        String sql =
        "select term from keyword inner join professorkeyword using(keywordID) where professorID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, professorID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String formatted =
                       "Your Interests: " + rs.getString(1) + " ";
                    out.add(formatted);
                }
            }
        } catch (SQLException e) {
            logError(e, "viewProfessorInterests()");
        }

        return out;
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
            logError(e, "addStudent()");
        }
        return 0;
    }

    // ---------------------------
    // Search students by interest
    // ---------------------------
    public List<String> searchProfessorByInterest(String interest) {
        List<String> results = new ArrayList<>();
        if (interest == null || interest.isBlank()) return results;

        String sql =
            "SELECT CONCAT(p.firstName,' ', p.lastName) AS 'ProfessorName'," + 
            "p.buildingCode, p.officeNum " +
            "p.email, p.phone " +
            "FROM professor p " +
            "JOIN professorkeyword pk USING(professorID) " +
            "JOIN Keyword k USING(keywordID) " +
            "WHERE LOWER(k.term) = LOWER(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, interest.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("ProfessorName");
                    String bc = rs.getString("buildingCode");
                    String officeNum = rs.getString("officeNum");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    results.add(name + "| Building Code: " + bc + " | Office Num: " + officeNum
                    + " | Email: " + email + " | Phone: " + phone
                    );
                }
            }
        } catch (SQLException e) {
            logError(e, "searchProfessorByInterest()");
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
            logError(e, "addPublicUser()");
        }
        return 0;
    }

    public List<String> searchStudentByInterest(String interest) {
        List<String> results = new ArrayList<>();
        if (interest == null || interest.isBlank()) return results;

        String sql =
            "SELECT s.firstName, s.lastName, s.major, s.email " +
            "FROM Student s " +
            "JOIN StudentKeyword sk ON s.studentID = sk.studentID " +
            "JOIN Keyword k ON sk.keywordID = k.keywordID " +
            "WHERE LOWER(k.term) = LOWER(?)";

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
            logError(e, "searchStudentByInterest()");
        }
        return results;
    }

    // ---------------------------------------------------
    // Public User Keyword (Guest Interests)
   //  ---------------------------------------------------
      public int addPublicKeyword(int publicID, int keywordID) {
          String sql = "INSERT INTO PublicKeyword (publicID, keywordID) VALUES (?, ?)";
          try (PreparedStatement ps = conn.prepareStatement(sql)) {
              ps.setInt(1, publicID);
              ps.setInt(2, keywordID);
              return ps.executeUpdate();
          } catch (SQLException e) {
              logError(e, "addPublicKeyword()");
              return 0;
          }
      }

      public int deletePublicKeyword(int publicID, int keywordID) {
          String sql = "DELETE FROM PublicKeyword WHERE publicID = ? AND keywordID = ?";
          try (PreparedStatement ps = conn.prepareStatement(sql)) {
              ps.setInt(1, publicID);
              ps.setInt(2, keywordID);
              return ps.executeUpdate();
          } catch (SQLException e) {
              logError(e, "deletePublicKeyword()");
              return 0;
          }
      }

      public List<String> listPublicKeywords(int publicID) {
          List<String> out = new ArrayList<>();
          String sql = "SELECT k.term FROM Keyword k "
                     + "JOIN PublicKeyword pk ON k.keywordID = pk.keywordID "
                     + "WHERE pk.publicID = ?";

          try (PreparedStatement ps = conn.prepareStatement(sql)) {
              ps.setInt(1, publicID);
              try (ResultSet rs = ps.executeQuery()) {
                  while (rs.next()) {
                      out.add(rs.getString("term"));
                  }
              }
          } catch (SQLException e) {
              logError(e, "listPublicKeywords()");
          }
          return out;
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
            logError(e, "findProfessorIdByAccountId()");
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
            logError(e, "findStudentIdByAccountId()");
        }
        return -1;
    }

    public List<String> listStudentKeywords(int studentID) {
    List<String> out = new ArrayList<>();
    String sql = "SELECT k.term FROM Keyword k " +
                 "JOIN StudentKeyword sk ON k.keywordID = sk.keywordID " +
                 "WHERE sk.studentID = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, studentID);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getString("term"));
            }
        }
    } catch (SQLException e) {
        logError(e, "listStudentKeywords()");
    }
    return out;
}

      public List<String> searchAbstracts(String text) {
          List<String> list = new ArrayList<>();
          if (text == null || text.isBlank()) return list;

          String sql =
              "SELECT abstractID, title, abstractText " +
              "FROM Abstract " +
              "WHERE title LIKE ? OR abstractText LIKE ?";

          try (PreparedStatement ps = conn.prepareStatement(sql)) {
              ps.setString(1, "%" + text + "%");
              ps.setString(2, "%" + text + "%");

              try (ResultSet rs = ps.executeQuery()) {
                  while (rs.next()) {
                      String formatted =
                          "Abstract ID: " + rs.getInt("abstractID") + "\n" +
                          "Title: " + rs.getString("title") + "\n" +
                          "Text: " + rs.getString("abstractText") + "\n" +
                          "--------------------------";
                      list.add(formatted);
                  }
              }
          } catch (SQLException e) {
              logError(e, "searchAbstracts()");
          }
          return list;
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
            logError(e, "findMatchingFaculty()");
        }
        return out;
    }

    public List<String> getStudentInterests(int studentId) {
    List<String> out = new ArrayList<>();
    String sql = "SELECT k.keyword FROM StudentKeyword sk " +
                 "JOIN Keyword k ON sk.keywordID = k.keywordID " +
                 "WHERE sk.studentID = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, studentId);
        try (ResultSet r = ps.executeQuery()) {
            while (r.next()) out.add(r.getString(1));
        }
    } catch (SQLException e) {
        logError(e, "getStudentInterests()");
    }
    return out;
}

   public List<String> searchProfessorsByAbstractText(String text) {
    List<String> out = new ArrayList<>();
    if (text == null || text.isBlank()) return out;

    String sql =
        "SELECT p.firstName, p.lastName, p.email, " +
        "       a.abstractID, a.title, a.abstractText " +
        "FROM Professor p " +
        "JOIN ProfessorAbstract pa ON p.professorID = pa.professorID " +
        "JOIN Abstract a ON pa.abstractID = a.abstractID " +
        "WHERE a.title LIKE ? OR a.abstractText LIKE ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + text + "%");
        ps.setString(2, "%" + text + "%");

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String formatted =
                    "Professor: " + rs.getString("firstName") + " " + rs.getString("lastName") + "\n" +
                    "Email: " + rs.getString("email") + "\n" +
                    "Abstract ID: " + rs.getInt("abstractID") + "\n" +
                    "Title: " + rs.getString("title") + "\n" +
                    "Text: " + rs.getString("abstractText") + "\n" +
                    "----------------------------------------";
                out.add(formatted);
            }
        }
    } catch (SQLException e) {
        logError(e, "searchProfessorsByAbstractText()");
    }

    return out;
}

public List<String> getAllStudentInterests(int studentID) {
    List<String> out = new ArrayList<>();

    String sql =
        "SELECT k.term " +
        "FROM Keyword k " +
        "JOIN StudentKeyword sk ON k.keywordID = sk.keywordID " +
        "WHERE sk.studentID = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, studentID);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getString("term"));
            }
        }
    } catch (SQLException e) {
        logError(e, "getAllStudentInterests()");
    }

    return out;
}


    public List<String> searchFacultyByKeyword(String interest) {
    List<String> out = new ArrayList<>();
    String sql = "SELECT DISTINCT p.firstName, p.lastName, p.buildingCode, p.officeNum, p.email " +
                 "FROM Professor p " +
                 "JOIN ProfessorKeyword pk ON p.professorID = pk.professorID " +
                 "JOIN Keyword k ON pk.keywordID = k.keywordID " +
                 "WHERE k.term LIKE ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + interest + "%");

        try (ResultSet r = ps.executeQuery()) {
            while (r.next()) {
                String line = String.format("%s %s | Building %s | Office %s | %s",
                        r.getString(1), r.getString(2),
                        r.getString(3), r.getString(4), r.getString(5));
                out.add(line);
            }
        }
    } catch (SQLException e) {
        logError(e, "searchFacultyByKeyword()");
    }
    return out;
}
      public int findPublicIdByAccountId(int accountId) {
          String sql = "SELECT publicID FROM PublicUser WHERE accountID = ?";
          try (PreparedStatement ps = conn.prepareStatement(sql)) {
              ps.setInt(1, accountId);
              try (ResultSet rs = ps.executeQuery()) {
                  if (rs.next()) return rs.getInt(1);
              }
          } catch (SQLException e) {
              logError(e, "findPublicIdByAccountId()");
          }
          return -1;
      }


   // ---------------------------------------------------
      // Search Professors by Abstract Keyword
      // ---------------------------------------------------
      public List<String> searchFacultyByAbstract(String term) {
          List<String> out = new ArrayList<>();

          String sql =
              "SELECT DISTINCT p.firstName, p.lastName, p.buildingCode, p.officeNum, p.email " +
              "FROM Professor p " +
              "JOIN ProfessorAbstract pa ON p.professorID = pa.professorID " +
              "JOIN Abstract a ON pa.abstractID = a.abstractID " +
              "WHERE a.title LIKE ? OR a.abstractText LIKE ?";

          try (PreparedStatement ps = conn.prepareStatement(sql)) {
              ps.setString(1, "%" + term + "%");
              ps.setString(2, "%" + term + "%");

              try (ResultSet r = ps.executeQuery()) {
                  while (r.next()) {
                      String line = String.format("%s %s | Building %s | Office %s | %s",
                              r.getString(1), r.getString(2),
                              r.getString(3), r.getString(4), r.getString(5));
                      out.add(line);
                  }
              }
          } catch (SQLException e) {
              logError(e, "searchFacultyByAbstract()");
          }

          return out;
      }

    public List<String> getProfessorAbstracts(int professorID) {
    List<String> out = new ArrayList<>();

    String sql =
        "SELECT a.abstractID, a.title, a.abstractText " +
        "FROM Abstract a " +
        "JOIN ProfessorAbstract pa ON a.abstractID = pa.abstractID " +
        "WHERE pa.professorID = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, professorID);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String formatted =
                    "Abstract ID: " + rs.getInt("abstractID") + "\n" +
                    "Title: " + rs.getString("title") + "\n" +
                    "Text: " + rs.getString("abstractText") + "\n" +
                    "--------------------------";
                out.add(formatted);
            }
        }
    } catch (SQLException e) {
        logError(e, "getProfessorAbstracts()");
    }

    return out;
}



    public List<String> listAllAbstracts() {
        List<String> list = new ArrayList<>();

        String query = "SELECT abstractID, title, abstractText FROM Abstract";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("abstractID");
                String title = rs.getString("title");
                String text = rs.getString("abstractText");

                String formatted =
                    "Abstract ID: " + id + "\n" +
                    "Title: " + title + "\n" +
                    "Text: " + text + "\n" +
                    "--------------------------";

                list.add(formatted);
            }

        } catch (SQLException e) {
            logError(e, "listAllAbstracts()");
        }

        return list;
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