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
     * @param fields ArrayList of strings object with every element representing a field
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
        sql += ");";
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
        this.stmt = this.conn.createStatement();
        this.stmt.execute(sql);
        this.stmt.close();
    }
    
    public void deleteTable(Table tab) throws SQLException
    {
        String sql = "DROP TABLE IF EXISTS " + tab.getTableName();
        this.stmt = this.conn.createStatement();
        this.stmt.execute(sql);
        this.stmt.close();
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
     * @param tab Table object to insert data into 
     * @param values Every value must be in string format within an ArrayList of strings
     * @throws SQLException
     * @throws ValuesException 
     */
    public void insertRow(Table tab, ArrayList<String> values) 
            throws SQLException, ValuesException
    {
        if(values.size() != tab.tableSchema.getColumnSize()) throw new ValuesException("Values and column count do not match.");
        this.stmt = this.conn.createStatement();
        String sql = "INSERT INTO " + tab.tableName + "(";
        
        for(int i = 0; i < tab.tableSchema.getColumnSize(); i++)
        {
            sql += tab.tableSchema.getColumn(i).columnName;
            if(i != tab.tableSchema.getColumnSize()-1) sql += ",";
        }
        sql += ") VALUES(";
        for(int i = 0; i < values.size(); i++)
        {
            
            if(tab.getTableSchema().getColumn(i).columnType.equals("TEXT")){
                sql += "\""  + values.get(i) + "\"";
            } else {
                if(values.get(i).equals("")) values.set(i, "NULL");
                sql += values.get(i);
            }
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
    
    /**
     * 
     * @param tab
     * @return
     * @throws SQLException
     * @throws ReadException 
     */
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
            for(int j = 1; j < rs.getMetaData().getColumnCount()+1; j++ ) {
                currentRow.add(rs.getString(j));
            }
            rowData.add(currentRow);
        }
        this.stmt.close();
        rs.close();
        return rowData;
    }
    
    /**
     * Reads a single row from the database and puts every columns value into an element of the returned ArrayList in string format.
     * @param tab The table to read from.
     * @param rowIndex the index of the row.
     * @return ArrayList of strings with every element being a column's value.
     * @throws SQLException 
     * @throws ReadException if given table and table in the database do not match 
     */
    public ArrayList<String> readRow(Table tab, int rowIndex) throws SQLException, ReadException
    {
        this.stmt = this.conn.createStatement();
        String sql = "SELECT * FROM " + tab.tableName + " LIMIT 1 OFFSET " + rowIndex;
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<String> returnData = new ArrayList<>();
        if(tab.getTableSchema().getColumnSize() != rs.getMetaData().getColumnCount()) throw new ReadException("The table object and the table in the database do not match.");
        rs.next(); // don't need to loop since all I'm getting is one row
        for(int i = 1; i < rs.getMetaData().getColumnCount()+1; i++) {
            returnData.add(rs.getString(i));
        }
        this.stmt.close();
        rs.close();
        return returnData;
    }
    
    /**
     * Gets the row values of multiple columns
     * @param tab the table to read from
     * @param columnNames an ArrayList of strings containing the column names
     * @return an 
     * @throws SQLException
     * @throws ReadException 
     */
//    public ArrayList<ArrayList<String>> readMultipleColumnsValues(Table tab, ArrayList<String> columnNames) throws SQLException, ReadException
//    {
//        
//        ArrayList<ArrayList<String>> returnData = new ArrayList<>();
//        ArrayList<String> currentRowData = new ArrayList<>();
//        String columnNamesString = new String(); // column names in one string
//        for(int i = 0; i < columnNames.size(); i++ ) { 
//            columnNamesString += columnNames.get(i);
//            if(i != columnNames.size() -1){
//                columnNamesString += ", ";
//            }
//        }
//        this.stmt = this.conn.createStatement();
//        ResultSet rs = this.stmt.executeQuery("SELECT " + columnNamesString + " FROM " + tab.tableName);
//        if(columnNames.size() != rs.getMetaData().getColumnCount()) throw new ReadException("The table object and the table in the database do not match.");
//        while(rs.next()) {
//            for(int i = 1; i < rs.getMetaData().getColumnCount()+1; i++ ) {
//                currentRowData.add(rs.getString(i));
//            }
//            returnData.add(currentRowData);
//        }
//        this.stmt.close();
//        rs.close();
//        return returnData;
//    }
    
    /**
     * Reads all the row values of a single column.
     * @param tab The table to be read from
     * @param columnName The name of the column
     * @return ArrayList of strings with every element being a row value from the specified column
     * @throws SQLException
     * @throws ReadException 
     */
    public ArrayList<String> readColumnValues(Table tab, String columnName) throws SQLException, ReadException
    {
        ArrayList<String> returnData = new ArrayList<>();
        this.stmt = this.conn.createStatement();
        ResultSet rs = this.stmt.executeQuery("SELECT " + columnName + " FROM " + tab.tableName);
        while(rs.next()) {
            returnData.add(rs.getString(1));
        }
        this.stmt.close();
        rs.close();
        return returnData;
    }

    
    
    public static void main(String[] args) throws SQLException, ReadException, ValuesException, ClassNotFoundException {
        LiteConnection conn = new LiteConnection();
        
        
            conn.Connect("test.db");
            TableOperations tb = new TableOperations(conn);
            System.out.println(tb.databaseName);
            ArrayList<String> fields = new ArrayList<>();
            fields.add("id INTEGER PRIMARY KEY AUTOINCREMENT");
            fields.add("sometext TEXT NOT NULL");
            fields.add("num INTEGER");
            tb.deleteTable("sometable");
            tb.writeTable("sometable", fields);
            try (ResultSet rs = tb.getTableSchema("sometable")) {
                while(rs.next()) {
                    System.out.println(rs.getString(4));
                }
            }
            Table tab = new Table(conn, "sometable2");
            TableSchema schema = new TableSchema(tb.getTableSchema("sometable"));
            tab.writeTable("sometable2", schema);
            System.out.println("---------------------------------------------");
            try (ResultSet rs = tab.getTableSchema("sometable2")) {
                while(rs.next()) {
                    System.out.println(rs.getString(2));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            tab.insertRow(";what the fuck; ");
            tab.insertRow(";;");
            
            tab.insertRow("10;text goes here.!2@;29382341141242189273859273987");
            System.out.println(tab.readRow(0));
            System.out.println(tab.readRow(1));
            System.out.println(tab.readRow(2));
            
            tab.deleteTable();
            System.out.println("Table exists: " + tb.TableExists("sometable2"));
        
    }
    
}
