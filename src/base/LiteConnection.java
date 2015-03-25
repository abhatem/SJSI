package base;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;

/**
 * Class with Sqlite connection functionality
 * @author abo0ody
 */

public class LiteConnection {
    private  Connection conn = null;
    private Exception exp = null;
    
    /**
    * Connect to a Database
    * @param DatabaseName name of the database
    * @return Boolean to indicate whether the operation succeeded or not.
    */ 
    public Boolean Connect(String DatabaseName) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + DatabaseName);
        } catch (Exception e) {
            this.exp = e;
            return false;
        }
        return true;
    }
    
    /**
    * @return Connection Object
    */   
    public Connection getConnection() {
        return this.conn;
    }
    
    /**
    * @return Exception Object
    */
    public Exception getException() {
        return this.exp;
    }
}
