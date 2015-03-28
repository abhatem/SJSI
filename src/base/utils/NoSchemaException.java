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

/**
 *
 * @author abo0ody
 */
public class NoSchemaException extends Exception {

    /**
     * Creates a new instance of <code>NoSchemaException</code> without detail
     * message.
     */
    public NoSchemaException() {
    }

    /**
     * Constructs an instance of <code>NoSchemaException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSchemaException(String msg) {
        super(msg);
    }
}
