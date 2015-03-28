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
package base.utils;

import java.util.ArrayList;

/**
 * Class with utility functions for handling strings
 * @author abo0ody
 */
public class StringUtils {
    
    /**
     * Counts the matches of an occurence in a string.
     * @param str The string to count in
     * @param occurence The occurence to look for
     * @return The number of occurences
     */
    public static int countMatches(String str, String occurence)
    {
        return str.length() - str.replace(occurence, "").length();
    }
    
    public static ArrayList<String> breakString(String str, String occurence) throws ValuesException
    {
        //if(str.startsWith(";")) str = "null" + str;
        ArrayList<String> vals = new ArrayList<>();
        int colonMatches = StringUtils.countMatches(str, ";");
        int indexofcolon = 0;
        String trimmedStr = str.trim();
        for(int i = 0; i < colonMatches; i++){
//            if(!trimmedStr.startsWith(";"))
//                indexofcolon = trimmedStr.indexOf(';', indexofcolon+1);
//            else indexofcolon = 0;
//            
//            if(indexofcolon > 0) {
//                vals.add(trimmedStr.substring(0, indexofcolon).trim());
//                trimmedStr = trimmedStr.substring(indexofcolon+1);
//            } else if (indexofcolon <= 0) {
//                vals.add("");
//                trimmedStr = trimmedStr.substring(indexofcolon+1);
//            }
            if(!str.startsWith(";"))
                indexofcolon = str.indexOf(';', indexofcolon+1);
            else indexofcolon = str.indexOf(';', indexofcolon);
            if(indexofcolon > 0) {
                //if((str.charAt(indexofcolon-1) != '\\') || ((str.charAt(indexofcolon-1) == '\\') && (str.charAt(indexofcolon-2) == '\\'))) {
                    vals.add(str.substring(0, indexofcolon).trim());
                    str = str.substring(indexofcolon+1);
                //}
            } else if (indexofcolon <= 0) {
                vals.add("");
                str = str.substring(indexofcolon+1);
            }
            
        }
        vals.add(str.trim());
        return vals;
    }
}
