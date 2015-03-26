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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 * Handles Table operations
 * @author abo0ody
 */
public class TableOperations {
    private Connection conn = null;
    private DatabaseMetaData metaData = null;
    private String databaseName = null;
    private Statement stmt = null;
    private Exception exp = null;
    
    /**
     * Constructor
     * @param liteConn
     * @throws SQLException
     */
    public TableOperations(LiteConnection liteConn) throws SQLException
    {
        this.conn = liteConn.getConnection();
        metaData = liteConn.getMetaData();
        databaseName = liteConn.getDatabaseName();
    }
    
    /**
     * Dummy constructor
     */
    public TableOperations() 
    {
        
    }
    
    /**
     * Creates a table with a specified name and specified fields.
     * @param TableName
     * @param fields
     * @throws java.sql.SQLException
     */
    public void createTable(String TableName, ArrayList<String> fields) throws SQLException
    {
        String sql = "CREATE TABLE " + TableName + "(";
        for(int i = 0; i < fields.size(); i++) {
            sql += fields.get(i);
            if(i != fields.size() -1) {
                sql += ", ";
            }
        }
        sql += ")";
        stmt = this.conn.createStatement();
        stmt.execute(sql);
        stmt.close();
            
    }
    
    public void createTable(String TableName, TableSchema schema) throws SQLException
    {
        createTable(TableName, schema.toStringList());
    }
    
    /**
     * Deletes a table with the specified name if it exists.
     * @param name
     * @throws java.sql.SQLException
     */
    public void deleteTable(String name) throws SQLException
    {
        String sql = "DROP TABLE IF EXISTS " + name;
        stmt = this.conn.createStatement();
        stmt.execute(sql);
        stmt.close();
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
     * @throws java.sql.SQLException
     */
    public ResultSet getTables() throws SQLException {
        ResultSet rs;
        rs = this.metaData.getTables(null, null, null, null);
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
            TableOperations tb = new TableOperations(conn);
            System.out.println(tb.databaseName);
            ArrayList<String> fields = new ArrayList<String>();
            fields.add("id INT PRIMARY KEY");
            fields.add("sometext TEXT NOT NULL");
            fields.add("num INT AUTO INCREMENT");
            tb.deleteTable("sometable");
            tb.createTable("sometable", fields);
            try (ResultSet rs = tb.getTableSchema("sometable")) {
                while(rs.next()) {
                    System.out.println(rs.getString(4));
                }
            }
            
            TableSchema schema = new TableSchema(tb.getTableSchema("sometable"));
            tb.deleteTable("sometable2");
            tb.createTable("sometable2", schema);
            System.out.println("---------------------------------------------");
            try (ResultSet rs = tb.getTableSchema("sometable2")) {
                while(rs.next()) {
                    System.out.println(rs.getString(2));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            
            System.out.println("Table exists: " + tb.TableExists("sometable2"));
        } catch (SQLException | ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
        
    }
    
}
