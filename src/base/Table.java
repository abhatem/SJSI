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

import java.sql.SQLException;

/**
 * Class responsible for handling tables
 * @author abo0ody
 */
public class Table extends TableOperations {
    
    TableSchema tableSchema = null;
    String tableName = null;
    /**
     * Constructs a table object.
     * @param liteConn
     * @throws SQLException 
     */
    public Table(LiteConnection liteConn, String TableName) throws SQLException
    {
        //Note: liteConn is not supposed to be stored in this class because all the
        // operations will be handled by the parent class (TableOperations)
        super(liteConn);
        this.tableName = TableName;
        this.tableSchema = new TableSchema(liteConn, this.tableName);
    }
    
    /**
     * Creates Table object and writes it into database
     * @param liteConn Database connection
     * @param TableName Name of the table
     * @param ts Table schema
     * @throws SQLException 
     */
    public Table(LiteConnection liteConn, String TableName, TableSchema ts) throws SQLException
    {
        super(liteConn);
        this.tableName = TableName;
        this.tableSchema = ts;
        // write table
    }
    
    /**
     * Dummy constructor
     */
    public Table() {
        super();
    }
}
