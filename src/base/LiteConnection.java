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
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Class with Sqlite connection functionality
 * @author abo0ody
 */

public class LiteConnection {
    private Connection conn = null;
    private DatabaseMetaData metaData = null; 
    private Exception exp = null;
    private String DatabaseName;
    
    /**
    * Connect to a Database
    * @param DatabaseName name of the database
    * @return Connection object
    * @throws SQLException
    * @throws ClassNotFoundException
    */ 
    public Connection Connect(String DatabaseName) throws SQLException, ClassNotFoundException{  
        Class.forName("org.sqlite.JDBC");
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + DatabaseName);
        this.DatabaseName = DatabaseName;
        this.metaData = this.conn.getMetaData();
        return this.conn;
    }
    
    /**
    * @return Connection Object
    */   
    public Connection getConnection() {
        return this.conn;
    }
    
    /**
    * @return Exception Object
    */
    public Exception getException() {
        return this.exp;
    }
    
    /**
     * 
     * @return The name of the current database
     * @throws SQLException 
     */
    public String getDatabaseName() throws SQLException {
        return DatabaseName;
    }
    
    public DatabaseMetaData getMetaData(){
        return this.metaData;
    }
}
