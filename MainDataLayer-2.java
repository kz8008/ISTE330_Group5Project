

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainDataLayer {
    private Connection conn;
    private ResultSet rs;
    private String sql;
    private Statement stmt;
    private int rows;
    
    final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    public MainDataLayer() {}

    public boolean connect(String user, String password, String database) {
        conn = null;
        String url = "jdbc:mysql://localhost/" + database;
        url = url + "?serverTimezone=UTC";

        try {
            Class.forName(DEFAULT_DRIVER);
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException ce) {
            System.out.println("ERROR, CAN NOT CONNECT!!");
            System.out.println("Class");
            System.out.println("ERROR MESSAGE-> "+ ce);
            System.exit(0);
        } catch (SQLException sqle) {
            System.out.println("ERROR SQLExcepiton in connect()");
            System.out.println("ERROR MESSAGE -> "+sqle);
            // sqle.printStackTrace();
            System.exit(0);
        }
        return (conn != null);
    }

// ==========================================================================
// Professor Functions 
// ==========================================================================


    public int addProfessor(
    int professorID, 
    String fname, 
    String lname, 
    String buildingCode, 
    String officeNum,
    String email,
    String phone
) {
    rows = 0;
    try {
        sql = "INSERT INTO professor VALUES (?,?,?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, professorID);
        stmt.setString(2, fname);
        stmt.setString(3, lname);
        stmt.setString(4, buildingCode);
        stmt.setString(5, officeNum);
        stmt.setString(6, email);
        stmt.setString(7, phone);
        // System.out.println("Command: " + stmt);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        // TODO: handle exception
        System.out.println("SQL ERROR ---> addProfessor()");
        System.out.println(sqle);
        sqle.printStackTrace();
        return(0);
    } catch (Exception e) {
        System.out.println("Error occured in addProfessor()");
        System.out.println(e);
        e.printStackTrace();
        return(0);
    }
    return rows;
}
public int updateProfessor(int professorID, String buildingCode, String officeNum) {
    rows = 0;
    try {
        String sql = "UPDATE professor SET buildingCode = ?, officeNum = ? WHERE professorID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, buildingCode);
        stmt.setString(2, officeNum);
        stmt.setInt(3, professorID);
        System.out.println ("Command to be executed: " + stmt);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
         System.out.println("SQL ERROR ---> updatePassenger()");
        System.out.println(sqle);
           sqle.printStackTrace();
           System.exit(0);
    } catch (Exception e) {
        System.out.println("SQL ERROR");
        System.out.println("DELETION FAILED");
        e.printStackTrace();
        System.exit(0);
    }
    return (rows);
}


// // PRESENTATION LAYER
public int deleteProfessor(int professorID) {
    rows = 0;
       try {
           String sql = "DELETE FROM professor WHERE professorID =" + professorID + ";";
           PreparedStatement stmt = conn.prepareStatement(sql);
           System.out.println ("Command to be executed: " + stmt);
           rows = stmt.executeUpdate();
       } catch (SQLException sqle) {
           System.out.println("SQL ERROR ---> deleteProfessor()");
           System.out.println(sqle);
           sqle.printStackTrace();
           return(0);
       } catch (Exception e) {
        System.out.println("SQL ERROR");
        System.out.println("DELETION FAILED");
        e.printStackTrace();
        return(0);
     }
  return (rows);
}

public int addAbstract(
    int abstractID, 
    String title, 
    String abstractText
) {
    rows = 0;
    try {
        sql = "INSERT INTO abstract VALUES (?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, abstractID);
        stmt.setString(2, title);
        stmt.setString(3, abstractText);
        // System.out.println("Command: " + stmt);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        // TODO: handle exception
        System.out.println("SQL ERROR ---> addAbstract()");
        System.out.println(sqle);
        sqle.printStackTrace();
        return(0);
    } catch (Exception e) {
        System.out.println("Error occured in addAbstract()");
        System.out.println(e);
        e.printStackTrace();
        return(0);
    }
    return rows;
}
public int deleteAbstract(int abstractID) {
    rows = 0;
       try {
           String sql = "DELETE FROM abstract WHERE abstractID =" + abstractID + ";";
           PreparedStatement stmt = conn.prepareStatement(sql);
           System.out.println ("Command to be executed: " + stmt);
           rows = stmt.executeUpdate();
       } catch (SQLException sqle) {
           System.out.println("SQL ERROR ---> deleteAbstract()");
           System.out.println(sqle);
           sqle.printStackTrace();
           return(0);
       } catch (Exception e) {
        System.out.println("SQL ERROR");
        System.out.println("DELETION FAILED");
        e.printStackTrace();
        return(0);
     }
  return (rows);
}

public int updateAbstract(int abstractID, String title, String abstractText) {
    rows = 0;
    try {
        String sql = "UPDATE abstract SET title = ?, abstractText = ? WHERE abstractID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, title);
        stmt.setString(2, abstractText);
        stmt.setInt(3, abstractID);
        System.out.println ("Command to be executed: " + stmt);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
         System.out.println("SQL ERROR ---> updateAbstract()");
        System.out.println(sqle);
           sqle.printStackTrace();
           System.exit(0);
    } catch (Exception e) {
        System.out.println("SQL ERROR");
        System.out.println("DELETION FAILED");
        e.printStackTrace();
        System.exit(0);
    }
    return (rows);
}

public int addKeyword(
    int keywordID, 
    String interest
) {
    rows = 0;
    try {
        sql = "INSERT INTO abstract VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, keywordID);
        stmt.setString(2, interest);
        // System.out.println("Command: " + stmt);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        // TODO: handle exception
        System.out.println("SQL ERROR ---> addKeyword()");
        System.out.println(sqle);
        sqle.printStackTrace();
        return(0);
    } catch (Exception e) {
        System.out.println("Error occured in addKeyword()");
        System.out.println(e);
        e.printStackTrace();
        return(0);
    }
    return rows;
}
public int searchStudentByInterest(String interest) {
    rows = 0;
    try {
        sql = "SELECT * FROM student INNER JOIN studentkeyword USING(studentID) INNER JOIN keyword USING (keywordID) WHERE keyword.interest = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, interest);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        // TODO: handle exception
        System.out.println("SQL ERROR ---> searchStudentByInterest()");
        System.out.println(sqle);
        sqle.printStackTrace();
        return(0);
    } catch (Exception e) {
        System.out.println("Error occured in searchStudentByInterest()");
        System.out.println(e);
        e.printStackTrace();
        return(0);
    }
    return rows;
}
// ===================================================================================
// Account Functions
// ===================================================================================
public enum UserType {
    FACULTY,
    STUDENT,
    PUBLIC;
}
public int getNextID() {
    int next =0;
    try {
        sql = "SELECT MAX(accountID) FROM account";
        PreparedStatement stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                // increment the biggest
                next = rs.getInt("max_id") + 1;
            }
    } catch (SQLException sqle) {
        // TODO: handle exception
        System.out.println("SQL ERROR ---> getNextID()");
        System.out.println(sqle);
        sqle.printStackTrace();
        return(0);
    } catch (Exception e) {
        System.out.println("Error occured in getNextID()");
        System.out.println(e);
        e.printStackTrace();
        return(0);
    }
    return next;
}

public int registerAccount(String username, String password, Enum<UserType> role) {
    rows = 0;
    try {
        sql = "INSERT INTO account VALUES (?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, getNextID());
        stmt.setString(2, username);
        stmt.setString(2, password);
        stmt.setString(2, role.name());
        
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        // TODO: handle exception
        System.out.println("SQL ERROR ---> registerAccount()");
        System.out.println(sqle);
        sqle.printStackTrace();
        return(0);
    } catch (Exception e) {
        System.out.println("Error occured in registerAccount()");
        System.out.println(e);
        e.printStackTrace();
        return(0);
    }
    return rows;
}

public int addStudent(
    int studentID,
    String firstName,
    String lastName,
    String major,
    String email,
    String phone
) {
    rows = 0;
    try {
        sql = "INSERT INTO student VALUES (?,?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        stmt.setString(2, firstName);
        stmt.setString(3, lastName);
        stmt.setString(4, major);
        stmt.setString(5, email);
        stmt.setString(6, phone);

        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> addStudent()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}

public int updateStudent(
    int studentID,
    String firstName,
    String lastName,
    String major,
    String email,
    String phone
) {
    rows = 0;
    try {
        sql = "UPDATE student SET firstName=?, lastName=?, major=?, email=?, phone=? WHERE studentID=?";
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, major);
        stmt.setString(4, email);
        stmt.setString(5, phone);
        stmt.setInt(6, studentID);

        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> updateStudent()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}


public int deleteStudent(int studentID) {
    rows = 0;
    try {
        sql = "DELETE FROM student WHERE studentID=" + studentID;
        PreparedStatement stmt = conn.prepareStatement(sql);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> deleteStudent()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}


public ResultSet getAllStudents() {
    try {
        sql = "SELECT * FROM student";
        PreparedStatement stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> getAllStudents()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}


public ResultSet getStudent(int studentID) {
    try {
        sql = "SELECT * FROM student WHERE studentID=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> getStudent()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

public int addStudentAccount(
    int studentID,
    String username,
    String password
) {
    rows = 0;
    try {
        sql = "INSERT INTO student_account VALUES (?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        stmt.setString(2, username);
        stmt.setString(3, password);

        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> addStudentAccount()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}

public int updateStudentAccount(
    int studentID,
    String username,
    String password
) {
    rows = 0;
    try {
        sql = "UPDATE student_account SET username=?, password=? WHERE studentID=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setInt(3, studentID);

        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> updateStudentAccount()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}


public int deleteStudentAccount(int studentID) {
    rows = 0;
    try {
        sql = "DELETE FROM student_account WHERE studentID=" + studentID;
        PreparedStatement stmt = conn.prepareStatement(sql);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> deleteStudentAccount()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}


public ResultSet getStudentAccount(int studentID) {
    try {
        sql = "SELECT * FROM student_account WHERE studentID=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> getStudentAccount()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

public int addStudentKeyword(int studentID, int keywordID) {
    rows = 0;
    try {
        sql = "INSERT INTO student_keyword VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        stmt.setInt(2, keywordID);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> addStudentKeyword()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}

public int deleteStudentKeyword(int studentID, int keywordID) {
    rows = 0;
    try {
        sql = "DELETE FROM student_keyword WHERE studentID=? AND keywordID=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        stmt.setInt(2, keywordID);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> deleteStudentKeyword()");
        System.out.println(sqle);
        return 0;
    }
    return rows;
}

public ResultSet getKeywordsForStudent(int studentID) {
    try {
        sql = "SELECT keywordID FROM student_keyword WHERE studentID=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> getKeywordsForStudent()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

public ResultSet getAllKeywords() {
    try {
        sql = "SELECT * FROM keyword";
        PreparedStatement stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> getAllKeywords()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

public ResultSet searchKeyword(String interest) {
    try {
        sql = "SELECT * FROM keyword WHERE interest LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + interest + "%");
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> searchKeyword()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

public ResultSet findMatchingFaculty(int studentID) {
    try {
        sql = "SELECT DISTINCT professor.* " +
              "FROM professor " +
              "INNER JOIN facultykeyword USING (professorID) " +
              "INNER JOIN student_keyword USING (keywordID) " +
              "WHERE student_keyword.studentID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentID);
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> findMatchingFaculty()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

public ResultSet searchFacultyByKeyword(String interest) {
    try {
        sql = "SELECT DISTINCT professor.* " +
              "FROM professor " +
              "INNER JOIN facultykeyword USING (professorID) " +
              "INNER JOIN keyword USING (keywordID) " +
              "WHERE interest LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + interest + "%");
        rs = stmt.executeQuery();
    } catch (SQLException sqle) {
        System.out.println("SQL ERROR ---> searchFacultyByKeyword()");
        System.out.println(sqle);
        return null;
    }
    return rs;
}

}

