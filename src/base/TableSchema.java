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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class that stores the schema of Sqlite tables
 * @author abo0ody
 */
public class TableSchema {
    ArrayList<ColumnSpecs> columnSpecs;
    /**
     * Gets the table schema from the table_info PRAGMA result set
     * @param SchemaResultSet The result set produced from the query "PRAGMA table_info('TableName')"
     * @throws SQLException
     */
    public TableSchema(ResultSet SchemaResultSet) throws SQLException
    {
        this.columnSpecs = new ArrayList<>();
        createFromResultSet(SchemaResultSet);
    }
    
    /**
     * 
     * @param liteConn Database connection
     * @param TableName Name of the table for the schema
     * @throws SQLException 
     */
    public TableSchema(LiteConnection liteConn, String TableName) throws SQLException
    {
        this.columnSpecs = new ArrayList<>();
        String sql = "PRAGMA table_info(" + TableName + ")";
        Statement stmt = liteConn.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        this.createFromResultSet(rs);
        rs.close();
        stmt.close();
    }
    
    /**
     * Creates a TableSchema Object from specified ColumnSpecs
     * @param cs 
     */
    public TableSchema(ArrayList<ColumnSpecs> cs)
    {
        this.columnSpecs = new ArrayList<>(cs);
    }
    
    /**
     * Dummy constructor
     */
    public TableSchema()
    {
        this.columnSpecs = new ArrayList<>();
        
    }
    
    /**
     * 
     * @return Returns number of columns - 1. Perfect for loops
     */
    public int getColumnSize() {
        return columnSpecs.size();
    }
    
    /**
     * 
     * @param Index Index of the column
     * @return Returns column specification based on index
     */
    public ColumnSpecs getColumn(int Index)
    {
        return columnSpecs.get(Index);
    }
    
    /**
     * Returns an ArrayList of strings, with every element being a string extracted
     * out of a column specification. Example:
     * toStringList.get(0) would contain something like "Id INT PRIMARY KEY"
     * @return An ArrayList of strings, with every element being a column specification 
     */
    public ArrayList<String> toStringList() 
    {
        int i = 0;
        String str;
        ColumnSpecs cs;
        ArrayList<String> als = new ArrayList<>();
        while(i < columnSpecs.size()) {
            str = new String();
            cs = columnSpecs.get(i);
            
            // first add the name
            str += cs.columnName + " ";
            
            // then add the type
            str += cs.columnType + " ";
            
            // then add the default value
            if(!cs.defaultValue.equals("null"))
                str += "DEFAULT" + cs.defaultValue + " ";
            
            //then see if the value can be null
            if(cs.cantBeNull)
                str += "NOT NULL ";
            
            if(cs.primaryKey)
                str += "PRIMARY KEY";
            
            als.add(str);
            i++;
        }
        return als;
    }
    
    /**
     * 
     * @param rs
     * @throws SQLException 
     */
    private void createFromResultSet(ResultSet rs) throws SQLException
    {
        ColumnSpecs columnSpec;
        while(rs.next()) {
            columnSpec = new ColumnSpecs();
            columnSpec.columnIndex = rs.getInt(1);
            columnSpec.columnName = rs.getString(2);
            columnSpec.columnType = rs.getString(3);
            columnSpec.cantBeNull = rs.getBoolean(4);
            columnSpec.defaultValue = rs.getString(5);
            if(columnSpec.defaultValue == null) columnSpec.defaultValue = "null";
            columnSpec.primaryKey = rs.getBoolean(6);
            if(columnSpec.primaryKey == null) columnSpec.primaryKey = false;
            this.columnSpecs.add(columnSpec);
        }
    }
}
