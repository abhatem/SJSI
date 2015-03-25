package base;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;
import java.util.ArrayList;

// for test purposes
import base.LiteConnection;

/**
 * Handles Table operations
 * @author abo0ody
 */
public class Tables {
    private final Connection conn;
    private final DatabaseMetaData MetaData;
    private final String DatabaseName;
    private Statement stmt;
    private Exception exp;
    
    /**
     * Constructor
     * @param conn Database connection
     * @throws SQLException
     */
    public Tables(Connection conn) throws SQLException
    {
        this.conn = conn;
        MetaData = conn.getMetaData();
        int LastColonIndex = MetaData.getURL().lastIndexOf(':');
        DatabaseName = MetaData.getURL().substring(LastColonIndex + 1);
    }
    
    /**
     * Creates a table with a specified name and specified fields.
     * @param name
     * @param fields
     * @return true if successful, false if not. see #getException
     */
    public Boolean createTable(String name, ArrayList<String> fields)
    {
        String sql = "CREATE TABLE " + name + "(";
        for(int i = 0; i < fields.size(); i++) {
            sql += fields.get(i);
            if(i != fields.size() -1) {
                sql += ", ";
            }
        }
        sql += ")";
        try {
            stmt = this.conn.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch(Exception e) {
            this.exp = e;
            return false;
        }
        return true;
    }
    
    /**
     * Deletes a table with the specified name if it exists.
     * @param name
     * @return true if successful, false if not. see #getException
     */
    public Boolean deleteTable(String name)
    {
        String sql = "DROP TABLE IF EXISTS " + name;
        try {
            stmt = this.conn.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (Exception e) {
            this.exp = e;
            return false;
        }
        return true;
    }
    
    /**
     * Exception object getter
     * @return Exception object, null in case of no exception
     */
    public Exception getExcpetion() 
    {
        return this.exp;
    }
    
    /**
     * 
     * @return The name of the current Database 
     */
    public String getDatabaseName() {
        return this.DatabaseName;
    }
    
    /**
     * 
     * @return ResultSet of the tables in the database
     */
    public ResultSet getTables() {
        ResultSet rs;
        try {
            rs = this.MetaData.getTables(null, null, null, null);
        } catch(Exception e) {
            this.exp = e;
            return null;
        }
        return rs;
    }
    
    /**
     * 
     * @param TableName
     * @return Returns whether a table exists in the database or not.
     * @throws SQLException 
     */
    public Boolean TableExists(String TableName) throws SQLException
    {
        ArrayList<String> TableNames = new ArrayList<String>();
        ResultSet rs = getTables();
        
        while (rs.next()) {
            TableNames.add(rs.getString("TABLE_NAME"));
        }
        rs.close(); 
        return TableNames.contains(TableName);
    }
    
    /**
     * see http://stackoverflow.com/questions/10026583/retrieving-table-column-names-in-sqlite
     * @param TableName
     * @return Returns the schema of the specified table
     * @throws SQLException if no table of the specified name exists
     */
    public ResultSet getTableSchema(String TableName) throws SQLException {
        String sql = "PRAGMA table_info(" + TableName + ")";
        this.stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        //stmt.close();
        return rs;
    }

    
    
    public static void main(String[] args) {
        LiteConnection conn = new LiteConnection();
        conn.Connect("test.db");
        try {
            Tables tb = new Tables(conn.getConnection());
            System.out.println(tb.DatabaseName);
            ArrayList<String> fields = new ArrayList<String>();
            fields.add("id INT PRIMARY KEY");
            fields.add("sometext TEXT");
            //if(!tb.createTable("sometable", fields)){
            //    System.err.println(tb.getExcpetion());
            //}
            System.out.println("Table exists: " + tb.TableExists("sometable"));
            ResultSet rs = tb.getTableSchema("sometable");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            rs.close();
            
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        
    }
    
}
