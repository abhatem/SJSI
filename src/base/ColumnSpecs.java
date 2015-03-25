/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

/**
 * Used to store column specification.
 * A good example use case would be as a building block for the #TableSchema class
 * @author abo0ody
 */
public class ColumnSpecs {
    public Integer columnIndex;
    public String columnName;
    public String columnType;
    public Boolean canBeNull;
    public String defaultValue;
    public Boolean primaryKey;
}
