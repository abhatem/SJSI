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
        String sql = "PRAGMA table_info(" + TableName + ")";
        Statement stmt = liteConn.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        this.createFromResultSet(rs);
        rs.close();
        stmt.close();
    }
    
    /**
     * Dummy constructor
     */
    public TableSchema()
    {
        
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
            columnSpec.canBeNull = rs.getBoolean(4);
            columnSpec.defaultValue = rs.getString(5);
            columnSpec.primaryKey = rs.getBoolean(6);
            this.columnSpecs.add(columnSpec);
        }
    }
}
