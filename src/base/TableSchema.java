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
    ColumnSpecs columnSpecs = new ColumnSpecs();
    /**
     * Gets the table schema from the table_info PRAGMA result set
     * @param SchemaResultSet The result set produced from the query "PRAGMA table_info('TableName')"
     * @throws SQLException
     */
    public TableSchema(ResultSet SchemaResultSet) throws SQLException{
        while(SchemaResultSet.next()) {
            this.columnSpecs.columnIndex = SchemaResultSet.getInt(1);
            this.columnSpecs.columnName = SchemaResultSet.getString(2);
            this.columnSpecs.columnType = SchemaResultSet.getString(3);
            this.columnSpecs.canBeNull = SchemaResultSet.getBoolean(4);
            this.columnSpecs.defaultValue = SchemaResultSet.getString(5);
            this.columnSpecs.primaryKey = SchemaResultSet.getBoolean(6);
        }
    }
    
    public TableSchema(LiteConnection liteConn, String TableName) 
    {
        
    }
}
