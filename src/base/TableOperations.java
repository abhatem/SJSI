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

import base.utils.ReadException;
import base.utils.ValuesException;
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
class TableOperations {
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
     * Writes a table to the database a specified name and specified fields.
     * @param TableName The name of the Table
     * @param fields ArrayList<string> object with every element representing a field
     * @throws java.sql.SQLException
     */
    public void writeTable(String TableName, ArrayList<String> fields) throws SQLException
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
        stmt.execute(sql.trim());
        stmt.close();
            
    }
    
    /**
     * Writes a table to the database
     * @param TableName name of the table to be created 
     * @param schema the schema of the table to be written
     * @throws SQLException 
     */
    public void writeTable(String TableName, TableSchema schema) throws SQLException
    {
        writeTable(TableName, schema.toStringList());
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
    
    
    /**
     * inserts a new row to the table
     * @param tableName
     * @param schema
     * @param values
     * @throws SQLException
     * @throws ValuesException 
     */
    public void insertRow(Table tab, ArrayList<String> values) 
            throws SQLException, ValuesException
    {
        if(values.size() != tab.tableSchema.getColumnSize()) throw new ValuesException("Values and column count do not match.");
        this.stmt = this.conn.createStatement();
        String sql = "INSEERT INTO " + tab.tableName + "(";
        
        for(int i = 0; i < tab.tableSchema.getColumnSize(); i++)
        {
            sql += tab.tableSchema.getColumn(i).columnName;
            if(i != tab.tableSchema.getColumnSize()-1) sql += ",";
        }
        sql += ") VALUES(";
        for(int i = 0; i < values.size(); i++)
        {
            sql += values.get(i);
            if(i != values.size()-1) sql += ",";
        }
        sql += ");";
        stmt.executeUpdate(sql);
    }
    
    /**
     * 
     * @param tab The table object
     * @param i The index of the row
     * @return Returns an ArrayList of strings that has one element for every column
     * @throws SQLException 
     */
    public ArrayList<String> getRow(Table tab, int i) throws SQLException
    {
        ArrayList<ArrayList<String>> rowData = tab.getRowData();
        return rowData.get(i);
    }
    
    public ArrayList<ArrayList<String>> readAllRowData(Table tab) throws SQLException, ReadException
    {
        ArrayList<ArrayList<String>> rowData = new ArrayList<>();
        ArrayList<String> currentRow;
        this.stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tab.getTableName());
        int i = 0;
        if(tab.getTableSchema().getColumnSize() != rs.getMetaData().getColumnCount()) throw new ReadException("The table object and the table in the database do not match.");
        while(rs.next()) {
            currentRow = new ArrayList();
            for(int j = 0; j < rs.getMetaData().getColumnCount(); j++ ) {
                currentRow.add(rs.getString(j));
            }
            rowData.add(currentRow);
        }
        return rowData;
    }

    
    
    public static void main(String[] args) {
        LiteConnection conn = new LiteConnection();
        
        try {
            conn.Connect("test.db");
            TableOperations tb = new TableOperations(conn);
            System.out.println(tb.databaseName);
            ArrayList<String> fields = new ArrayList<>();
            fields.add("id INT PRIMARY KEY");
            fields.add("sometext TEXT NOT NULL");
            fields.add("num INT AUTO INCREMENT");
            tb.deleteTable("sometable");
            tb.writeTable("sometable", fields);
            try (ResultSet rs = tb.getTableSchema("sometable")) {
                while(rs.next()) {
                    System.out.println(rs.getString(4));
                }
            }
            Table tab = new Table();
            tab.insertRow("hello\\; hi; what");
            TableSchema schema = new TableSchema(tb.getTableSchema("sometable"));
            tb.deleteTable("sometable2");
            tb.writeTable("sometable2", schema);
            System.out.println("---------------------------------------------");
            try (ResultSet rs = tb.getTableSchema("sometable2")) {
                while(rs.next()) {
                    System.out.println(rs.getString(2));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            
            System.out.println("Table exists: " + tb.TableExists("sometable2"));
        } catch (SQLException | ValuesException | ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
        
    }
    
}
