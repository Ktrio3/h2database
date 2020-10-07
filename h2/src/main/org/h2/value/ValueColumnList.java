/*
 * Copyright 2004-2018 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.value;

import org.h2.engine.SysProperties;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.h2.value.ValueColumnName;
import java.util.ArrayList;

/**
 * Implementation of the VARCHAR data type.
 * It is also the base class for other ValueString* classes.
 */
public class ValueColumnList extends Value {

    /**
     * Empty string. Should not be used in places where empty string can be
     * treated as {@code NULL} depending on database mode.
     */
    public static final ValueColumnList EMPTY = new ValueColumnList("");

    /**
     * The string data.
     */
    protected final String value;

    public ArrayList<Value> valueList;

    protected ValueColumnList(String value) {
        this.value = value;
        this.valueList = new ArrayList<>();
    }

    @Override
    public String getSQL() {
        return StringUtils.quoteStringSQL(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueColumnList
                && value.equals(((ValueColumnList) other).value);
    }

    @Override
    public int compareTypeSafe(Value o, CompareMode mode) {
        return mode.compareString(value, ((ValueColumnList) o).value, false);
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public long getPrecision() {
        return value.length();
    }

    @Override
    public Object getObject() {
        return value;
    }

    @Override
    public void set(PreparedStatement prep, int parameterIndex)
            throws SQLException {
        prep.setString(parameterIndex, value);
    }

    @Override
    public int getDisplaySize() {
        return value.length();
    }

    @Override
    public int getMemory() {
        return value.length() * 2 + 48;
    }

    @Override
    public Value convertPrecision(long precision, boolean force) {
        if (precision == 0 || value.length() <= precision) {
            return this;
        }
        int p = MathUtils.convertLongToInt(precision);
        return getNew(value.substring(0, p));
    }

    @Override
    public int hashCode() {
        // TODO hash performance: could build a quicker hash
        // by hashing the size and a few characters
        return value.hashCode();

        // proposed code:
//        private int hash = 0;
//
//        public int hashCode() {
//            int h = hash;
//            if (h == 0) {
//                String s = value;
//                int l = s.length();
//                if (l > 0) {
//                    if (l < 16)
//                        h = s.hashCode();
//                    else {
//                        h = l;
//                        for (int i = 1; i <= l; i <<= 1)
//                            h = 31 *
//                                (31 * h + s.charAt(i - 1)) +
//                                s.charAt(l - i);
//                    }
//                    hash = h;
//                }
//            }
//            return h;
//        }

    }

    @Override
    public int getType() {
        return Value.STRING;
    }

    /**
     * Get or create a string value for the given string.
     *
     * @param s the string
     * @return the value
     */
    public static Value get(String s) {
        return get(s, false);
    }

    /**
     * Get or create a string value for the given string.
     *
     * @param s the string
     * @param treatEmptyStringsAsNull whether or not to treat empty strings as
     *            NULL
     * @return the value
     */
    public static Value get(String s, boolean treatEmptyStringsAsNull) {
        if (s.isEmpty()) {
            return treatEmptyStringsAsNull ? ValueNull.INSTANCE : EMPTY;
        }
        ValueColumnList obj = new ValueColumnList(StringUtils.cache(s));
        if (s.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        return Value.cache(obj);
        // this saves memory, but is really slow
        // return new ValueString(s.intern());
    }

    /**
     * Create a new String value of the current class.
     * This method is meant to be overridden by subclasses.
     *
     * @param s the string
     * @return the value
     */
    protected Value getNew(String s) {
        return ValueColumnList.get(s);
    }

}
