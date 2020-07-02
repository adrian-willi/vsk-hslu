/**
 * Copyright 1996-2004 Edwin Martin <edwin@bitstorm.nl>
 *
 * @author Edwin Martin
 */
package org.bitstorm.gameoflife;

import java.awt.Dimension;
import java.util.Enumeration;

import ch.hslu.vsk.logger.api.Logger;

/**
 * Shape contains data of one (predefined) shape.
 *
 * @author Edwin Martin
 */
public final class Shape {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("Shape");

    private final String name;
    private final int[][] shape;

    /**
     * Constructa a Shape.
     *
     * @param name  name of shape
     * @param shape shape data
     */
    public Shape(final String name, final int[][] shape) {
        this.name = name;
        this.shape = shape;
    }

    /**
     * Get dimension of shape.
     *
     * @return dimension of the shape in cells
     */
    public Dimension getDimension() {
        int shapeWidth = 0;
        int shapeHeight = 0;
        for (int cell = 0; cell < shape.length; cell++) {
            if (shape[cell][0] > shapeWidth) {
                shapeWidth = shape[cell][0];
            }
            if (shape[cell][1] > shapeHeight) {
                shapeHeight = shape[cell][1];
            }
        }
        shapeWidth++;
        shapeHeight++;
        return new Dimension(shapeWidth, shapeHeight);
    }

    /**
     * Get name of shape.
     *
     * @return name of shape
     */
    public String getName() {
        return name;
    }

    /**
     * Get shape data. Hide the shape implementation. Returns a anonymous Enumerator
     * object.
     *
     * @return enumerated shape data
     */
    public Enumeration getCells() {
        return new Enumeration() {
            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < shape.length;
            }

            @Override
            public Object nextElement() {
                return shape[index++];
            }
        };
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String msg = name + " (" + shape.length + " cell";
        if (shape.length != 1) {
            msg = msg + "s";
        }
        log.debug(msg);
        return msg;
    }
}
