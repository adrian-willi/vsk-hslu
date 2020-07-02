/*
 * Enumerates over a string containing a text file, returning lines.
 */
package org.bitstorm.util;

import java.util.Enumeration;

import org.bitstorm.gameoflife.StandaloneGameOfLife;

import ch.hslu.vsk.logger.api.Logger;

/**
 * Enumerates over a string containing a text file, returning lines. Line
 * endings in the text can be "\r\n", "\r" or "\n".
 *
 * @author Edwin Martin
 */
// But nothing beats Python ;-)
// for line in file("file.txt"):
// # Process line
public final class LineEnumerator implements Enumeration {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("LineEnumerator");

    private final String s;
    private final String separator;
    private final String cr = "\r";
    private final String lf = "\n";
    private final String crlf = "\r\n";
    private int offset;
    private int eolOffset;

    /**
     * Constructs a TextEnumerator.
     *
     * @param s String with text
     */
    public LineEnumerator(final String s) {
        log.debug("Create LineEnumerator");
        this.s = s;
        // find out the seperator
        if (s.contains(cr)) {
            if (s.contains(crlf)) {
                separator = crlf;
            } else {
                separator = cr;
            }
        } else {
            separator = lf;
        }
        eolOffset = -separator.length();
    }

    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    @Override
    public boolean hasMoreElements() {
        return eolOffset != s.length();
    }

    /**
     * When the "last line" ends with a return, the next empty line will also be
     * returned, as it should. Returned lines do not end with return chars (LF, CR
     * or CRLF).
     *
     * @see java.util.Enumeration#nextElement()
     */
    @Override
    public Object nextElement() {
        log.debug("Get next line");
        // skip to next line
        offset = eolOffset + separator.length();
        // find the next seperator
        eolOffset = s.indexOf(separator, offset);
        // not found, set to last char (the last line doesn't need have a \n or \r)
        if (eolOffset == -1) {
            log.debug("Get last line");
            eolOffset = s.length();
        }
        return s.substring(offset, eolOffset);
    }
}
