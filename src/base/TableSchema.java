/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class that stores the schema of Sqlite tables
 * @author abo0ody
 */
public class TableSchema {
    ArrayList<String> ColumnNames;
    ArrayList<String> ColumnTypes;
    ArrayList<Boolean> CanBeNull;
    ArrayList<String> DefaultValues; 
    /**
     * Gets the table schema from the table_info PRAGMA result set
     * @param SchemaResultSet The result set produced from the query "PRAGMA table_info('TableName')"
     */
    public TableSchema(ResultSet SchemaResultSet){
        
    }
}
