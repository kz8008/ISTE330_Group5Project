import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainDataLayer3 {
    private Connection conn;
    private ResultSet rs;
    private String sql;
    private int rows;
    
    final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    public MainDataLayer3() {}

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
    String interest
) {
    rows = 0;
    try {
        sql = "INSERT INTO abstract VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, getNextKeywordID());
        stmt.setString(2, interest);
        // System.out.println("Command: " + stmt);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
        
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

public int getNextKeywordID() {
    int next =0;
    try {
        sql = "SELECT MAX(keywordID) FROM keyword";
        PreparedStatement stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                // increment the biggest
                next = rs.getInt("max_id") + 1;
            }
    } catch (SQLException sqle) {
        
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

public int searchStudentByInterest(String interest) {
    rows = 0;
    try {
        sql = "SELECT * FROM student INNER JOIN studentkeyword USING(studentID) INNER JOIN keyword USING (keywordID) WHERE keyword.interest = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, interest);
        rows = stmt.executeUpdate();
    } catch (SQLException sqle) {
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

}

