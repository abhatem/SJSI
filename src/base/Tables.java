/* 
 * Copyright (C) 2015 abo0ody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package base;

import java.sql.*;
import java.util.ArrayList;


/**
 * Handles Table operations
 * @author abo0ody
 */
public class Tables {
    private final Connection conn;
    private final DatabaseMetaData metaData;
    private final String databaseName;
    private Statement stmt;
    private Exception exp;
    
    /**
     * Constructor
     * @param conn Database connection
     * @throws SQLException
     */
    public Tables(LiteConnection liteConn) throws SQLException
    {
        this.conn = liteConn.getConnection();
        metaData = liteConn.getMetaData();
        databaseName = liteConn.getDatabaseName();
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
        return this.databaseName;
    }
    
    /**
     * 
     * @return ResultSet of the tables in the database
     */
    public ResultSet getTables() {
        ResultSet rs;
        try {
            rs = this.metaData.getTables(null, null, null, null);
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
        
        try {
            conn.Connect("test.db");
            Tables tb = new Tables(conn);
            System.out.println(tb.databaseName);
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
