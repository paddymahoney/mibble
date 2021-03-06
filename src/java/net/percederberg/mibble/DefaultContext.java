/*
 * DefaultContext.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2004-2013 Per Cederberg. All rights reserved.
 */

package net.percederberg.mibble;

import java.util.HashMap;

import net.percederberg.mibble.type.ObjectIdentifierType;
import net.percederberg.mibble.value.ObjectIdentifierValue;

/**
 * A default MIB context.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  2.10
 * @since    2.0
 */
class DefaultContext implements MibContext {

    /**
     * The root "ccitt" symbol name.
     */
    public static final String CCITT = "ccitt";

    /**
     * The root "iso" symbol name.
     */
    public static final String ISO = "iso";

    /**
     * The root "joint-iso-ccitt" symbol name.
     */
    public static final String JOINT_ISO_CCITT = "joint-iso-ccitt";

    /**
     * The map of default symbols.
     */
    private HashMap<String,MibSymbol> symbols = new HashMap<String,MibSymbol>();

    /**
     * Creates a new default context.
     */
    DefaultContext() {
        initialize();
    }

    /**
     * Initializes this context by creating all default symbols.
     */
    private void initialize() {
        symbols.put(CCITT, createRootOid(CCITT, 0));
        symbols.put(ISO, createRootOid(ISO, 1));
        symbols.put(JOINT_ISO_CCITT, createRootOid(JOINT_ISO_CCITT, 2));
    }

    /**
     * Create a root object identifier symbol.
     *
     * @param name           the symbol name
     * @param value          the oid value
     *
     * @return the new object identifier symbol
     */
    private MibValueSymbol createRootOid(String name, int value) {
        ObjectIdentifierValue oid = new ObjectIdentifierValue(name, value);
        MibValueSymbol sym = new MibValueSymbol(new FileLocation(null, -1, -1),
                                                null,
                                                name,
                                                new ObjectIdentifierType(),
                                                oid);
        oid.setSymbol(sym);
        return sym;
    }

    /**
     * Searches for a named MIB symbol. This method may search outside
     * the normal (or strict) scope, thereby allowing a form of
     * relaxed search. Note that the results from the normal and
     * expanded search may not be identical, due to the context
     * chaining and the same symbol name appearing in various
     * contexts.<p>
     *
     * <strong>NOTE:</strong> This is an internal method that should
     * only be called by the MIB loader.
     *
     * @param name           the symbol name
     * @param expanded       the expanded scope flag
     *
     * @return the MIB symbol, or null if not found
     *
     * @since 2.4
     */
    public MibSymbol findSymbol(String name, boolean expanded) {
        return symbols.get(name);
    }

    /**
     * Searches the OID tree for the best matching value. The
     * returned OID value will be the longest matching OID value, but
     * doesn't have to be an exact match. The search requires the
     * full numeric OID value (from the root).
     *
     * @param oid            the numeric OID string to search for
     *
     * @return the best matching OID value, or
     *         null if no partial match was found
     *
     * @since 2.10
     */
    public ObjectIdentifierValue findOid(String oid) {
        MibValue value = ((MibValueSymbol) symbols.get(ISO)).getValue();
        ObjectIdentifierValue match = ((ObjectIdentifierValue) value).find(oid);
        if (match == null) {
            value = ((MibValueSymbol) symbols.get(CCITT)).getValue();
            match = ((ObjectIdentifierValue) value).find(oid);
        }
        if (match == null) {
            value = ((MibValueSymbol) symbols.get(JOINT_ISO_CCITT)).getValue();
            match = ((ObjectIdentifierValue) value).find(oid);
        }
        return match;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "<defaults>";
    }
}
