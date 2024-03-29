/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* ----------------------
 * ModifiableInteger.java
 * ----------------------
 *
 * (C) Copyright 2002-2004, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id: ModifiableInteger.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 2004-05-27 : Initial version (BN);
 *
 */
package org.jgrapht.util;

/**
 * The <code>ModifiableInteger</code> class wraps a value of the primitive type
 * <code>int</code> in an object, similarly to {@link java.lang.Integer}. An
 * object of type <code>ModifiableInteger</code> contains a single field whose
 * type is <code>int</code>.
 *
 * <p>Unlike <code>java.lang.Integer</code>, the int value which the
 * ModifiableInteger represents can be modified. It becomes useful when used
 * together with the collection framework. For example, if you want to have a
 * {@link java.util.List} of counters. You could use <code>Integer</code> but
 * that would have became wasteful and inefficient if you frequently had to
 * update the counters.</p>
 *
 * <p>WARNING: Because instances of this class are mutable, great care must be
 * exercised if used as keys of a {@link java.util.Map} or as values in a {@link
 * java.util.Set} in a manner that affects equals comparisons while the
 * instances are keys in the map (or values in the set). For more see
 * documentation of <code>Map</code> and <code>Set</code>.</p>
 *
 * @author Barak Naveh
 * @since May 27, 2004
 */
public class ModifiableInteger
    extends Number
    implements Comparable
{
    //~ Static fields/initializers ---------------------------------------------

    private static final long serialVersionUID = 3618698612851422261L;

    //~ Instance fields --------------------------------------------------------

    /**
     * The int value represented by this <code>ModifiableInteger</code>.
     */
    public int value;

    //~ Constructors -----------------------------------------------------------

    /**
     * <b>!!! DON'T USE - Use the {@link #ModifiableInteger(int)} constructor
     * instead !!!</b>
     *
     * <p>This constructor is for the use of java.beans.XMLDecoder
     * deserialization. The constructor is marked as 'deprecated' to indicate to
     * the programmer against using it by mistake.</p>
     *
     * @deprecated not really deprecated, just marked so to avoid mistaken use.
     */
    @Deprecated public ModifiableInteger()
    {
    }

    /**
     * Constructs a newly allocated <code>ModifiableInteger</code> object that
     * represents the specified <code>int</code> value.
     *
     * @param value the value to be represented by the <code>
     * ModifiableInteger</code> object.
     */
    public ModifiableInteger(int value)
    {
        this.value = value;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Sets a new value for this modifiable integer.
     *
     * @param value the new value to set.
     */
    public void setValue(int value)
    {
        this.value = value;
    }

    /**
     * Returns the value of this object, similarly to {@link #intValue()}. This
     * getter is NOT redundant. It is used for serialization by
     * java.beans.XMLEncoder.
     *
     * @return the value.
     */
    public int getValue()
    {
        return this.value;
    }

    /**
     * Adds one to the value of this modifiable integer.
     */
    public void increment()
    {
        this.value++;
    }

    /**
     * Subtracts one from the value of this modifiable integer.
     */
    public void decrement()
    {
        this.value--;
    }

    /**
     * Compares two <code>ModifiableInteger</code> objects numerically.
     *
     * @param anotherInteger the <code>ModifiableInteger</code> to be compared.
     *
     * @return the value <code>0</code> if this <code>ModifiableInteger</code>
     * is equal to the argument <code>ModifiableInteger</code>; a value less
     * than <code>0</code> if this <code>ModifiableInteger</code> is numerically
     * less than the argument <code>ModifiableInteger</code>; and a value
     * greater than <code>0</code> if this <code>ModifiableInteger</code> is
     * numerically greater than the argument <code>ModifiableInteger</code>
     * (signed comparison).
     */
    public int compareTo(ModifiableInteger anotherInteger)
    {
        int thisVal = this.value;
        int anotherVal = anotherInteger.value;

        return (thisVal < anotherVal) ? -1 : ((thisVal == anotherVal) ? 0 : 1);
    }

    /**
     * Compares this <code>ModifiableInteger</code> object to another object. If
     * the object is an <code>ModifiableInteger</code>, this function behaves
     * like <code>compareTo(Integer)</code>. Otherwise, it throws a <code>
     * ClassCastException</code> (as <code>ModifiableInteger</code> objects are
     * only comparable to other <code>ModifiableInteger</code> objects).
     *
     * @param o the <code>Object</code> to be compared.
     *
     * @return the value <code>0</code> if the argument is a <code>
     * ModifiableInteger</code> numerically equal to this <code>
     * ModifiableInteger</code>; a value less than <code>0</code> if the
     * argument is a <code>ModifiableInteger</code> numerically greater than
     * this <code>ModifiableInteger</code>; and a value greater than <code>
     * 0</code> if the argument is a <code>ModifiableInteger</code> numerically
     * less than this <code>ModifiableInteger</code>.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        return compareTo((ModifiableInteger) o);
    }

    /**
     * @see Number#doubleValue()
     */
    public double doubleValue()
    {
        return this.value;
    }

    /**
     * Compares this object to the specified object. The result is <code>
     * true</code> if and only if the argument is not <code>null</code> and is
     * an <code>ModifiableInteger</code> object that contains the same <code>
     * int</code> value as this object.
     *
     * @param o the object to compare with.
     *
     * @return <code>true</code> if the objects are the same; <code>false</code>
     * otherwise.
     */
    public boolean equals(Object o)
    {
        if (o instanceof ModifiableInteger) {
            return this.value == ((ModifiableInteger) o).value;
        }

        return false;
    }

    /**
     * @see Number#floatValue()
     */
    public float floatValue()
    {
        return this.value;
    }

    /**
     * Returns a hash code for this <code>ModifiableInteger</code>.
     *
     * @return a hash code value for this object, equal to the primitive <code>
     * int</code> value represented by this <code>ModifiableInteger</code>
     * object.
     */
    public int hashCode()
    {
        return this.value;
    }

    /**
     * @see Number#intValue()
     */
    public int intValue()
    {
        return this.value;
    }

    /**
     * @see Number#longValue()
     */
    public long longValue()
    {
        return this.value;
    }

    /**
     * Returns an <code>Integer</code> object representing this <code>
     * ModifiableInteger</code>'s value.
     *
     * @return an <code>Integer</code> representation of the value of this
     * object.
     */
    public Integer toInteger()
    {
        return Integer.valueOf(this.value);
    }

    /**
     * Returns a <code>String</code> object representing this <code>
     * ModifiableInteger</code>'s value. The value is converted to signed
     * decimal representation and returned as a string, exactly as if the
     * integer value were given as an argument to the {@link
     * java.lang.Integer#toString(int)} method.
     *
     * @return a string representation of the value of this object in
     * base&nbsp;10.
     */
    public String toString()
    {
        return String.valueOf(this.value);
    }
}

// End ModifiableInteger.java
