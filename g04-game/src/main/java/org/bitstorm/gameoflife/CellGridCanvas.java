/**
 * Copyright 1996-2004 Edwin Martin <edwin@bitstorm.nl>
 *
 * @author Edwin Martin
 */
package org.bitstorm.gameoflife;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;

import ch.hslu.vsk.logger.api.Logger;

/**
 * Subclass of Canvas, which makes the cellgrid visible. Communicates via
 * CellGrid interface.
 *
 * @author Edwin Martin
 */
public final class CellGridCanvas extends Canvas {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("CellGridCanvas");

    private static final long serialVersionUID = 1L;

    private boolean cellUnderMouse;
    /**
     * Image for double buffering, to prevent flickering.
     */
    private Image offScreenImage;
    private Graphics offScreenGraphics;
    private Image offScreenImageDrawed;
    /**
     * Image, containing the drawed grid.
     */
    private Graphics offScreenGraphicsDrawed;
    private int cellSize;
    private final CellGrid cellGrid;
    private int newCellSize;
    private Shape newShape;

    /**
     * Constructs a CellGridCanvas.
     *
     * @param cellGrid the GoL cellgrid
     * @param cellSize size of cell in pixels
     */
    public CellGridCanvas(final CellGrid cellGrid, final int cellSize) {
        this.cellGrid = cellGrid;
        this.cellSize = cellSize;

        setBackground(new Color(0x999999));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                draw(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                saveCellUnderMouse(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                draw(e.getX(), e.getY());
            }
        });
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(final ComponentEvent e) {
                resized();
                repaint();
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
            }

            @Override
            public void componentHidden(final ComponentEvent e) {
            }

            @Override
            public void componentShown(final ComponentEvent e) {
            }
        });

    }

    /**
     * Set cell size (zoom factor).
     *
     * @param cellSize Size of cell in pixels
     */
    public void setCellSize(final int cellSize) {
        this.cellSize = cellSize;
        resized();
        repaint();
    }

    /**
     * The grid is resized (by window resize or zooming). Also apply post-resize
     * properties when necessary
     */
    public void resized() {
        if (newCellSize != 0) {
            cellSize = newCellSize;
            newCellSize = 0;
        }
        Dimension canvasDim = this.getSize();
        offScreenImage = null;
        offScreenImageDrawed = null;
        cellGrid.resize(canvasDim.width / cellSize, canvasDim.height / cellSize);
        if (newShape != null) {
            try {
                setShape(newShape);
                log.info("Grid resized" + newShape);
            } catch (ShapeException e) {
                log.error("Could not resize shape: " + e);
            }
        }

    }

    /**
     * Remember state of cell for drawing.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void saveCellUnderMouse(final int x, final int y) {
        try {
            cellUnderMouse = cellGrid.getCell(x / cellSize, y / cellSize);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Index out of Bound: " + e);

        }
    }

    /**
     * Draw in this cell.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void draw(final int x, final int y) {
        try {
            cellGrid.setCell(x / cellSize, y / cellSize, !cellUnderMouse);
            repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Index out of Bound: " + e);

        }
    }

    /**
     * Use double buffering.
     *
     * @see java.awt.Component#update(java.awt.Graphics)
     */
    @Override
    public void update(final Graphics g) {
        Dimension d = getSize();
        if (offScreenImage == null) {
            offScreenImage = createImage(d.width, d.height);
            offScreenGraphics = offScreenImage.getGraphics();
        }
        paint(offScreenGraphics);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    /**
     * Draw this generation.
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g) {
        // Draw grid on background image, which is faster
        if (offScreenImageDrawed == null) {
            Dimension dim = cellGrid.getDimension();
            Dimension d = getSize();
            offScreenImageDrawed = createImage(d.width, d.height);
            offScreenGraphicsDrawed = offScreenImageDrawed.getGraphics();
            // draw background (MSIE doesn't do that)
            offScreenGraphicsDrawed.setColor(getBackground());
            offScreenGraphicsDrawed.fillRect(0, 0, d.width, d.height);
            offScreenGraphicsDrawed.setColor(Color.gray);
            offScreenGraphicsDrawed.fillRect(0, 0, cellSize * dim.width - 1, cellSize * dim.height - 1);
            offScreenGraphicsDrawed.setColor(getBackground());
            for (int x = 1; x < dim.width; x++) {
                offScreenGraphicsDrawed.drawLine(x * cellSize - 1, 0, x * cellSize - 1, cellSize * dim.height - 1);
            }
            for (int y = 1; y < dim.height; y++) {
                offScreenGraphicsDrawed.drawLine(0, y * cellSize - 1, cellSize * dim.width - 1, y * cellSize - 1);
            }
        }
        g.drawImage(offScreenImageDrawed, 0, 0, null);
        // draw populated cells
        g.setColor(Color.yellow);
        Enumeration enumGrid = cellGrid.getEnum();
        Cell c;
        while (enumGrid.hasMoreElements()) {
            c = (Cell) enumGrid.nextElement();
            g.fillRect(c.getCol() * cellSize, c.getRow() * cellSize, cellSize - 1, cellSize - 1);
        }
    }

    /**
     * This is the preferred size.
     *
     * @see java.awt.Component#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = cellGrid.getDimension();
        return new Dimension(cellSize * dim.width, cellSize * dim.height);
    }

    /**
     * This is the minimum size (size of one cell).
     *
     * @see java.awt.Component#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(cellSize, cellSize);
    }

    /**
     * Settings to appy after a window-resize.
     *
     * @param newShape    new shape
     * @param newCellSize new cellSize
     */
    public void setAfterWindowResize(final Shape newShape, final int newCellSize) {
        this.newShape = newShape;
        this.newCellSize = newCellSize;
    }

    /**
     * Draws shape in grid.
     *
     * @param shape name of shape
     * @throws ShapeException if the shape does not fit on the canvas
     */
    public synchronized void setShape(final Shape shape) throws ShapeException {
        int xOffset;
        int yOffset;
        Dimension dimShape;
        Dimension dimGrid;

        // get shape properties
        // shapeGrid = shape.getShape();
        dimShape = shape.getDimension();
        dimGrid = cellGrid.getDimension();

        if (dimShape.width > dimGrid.width || dimShape.height > dimGrid.height) {
            String msg = "Shape doesn't fit on canvas (grid: " + dimGrid.width + "x" + dimGrid.height + ", shape: "
                    + dimShape.width + "x" + dimShape.height + ")";
            log.debug(msg);
            throw new ShapeException(msg);
        }
        // center the shape
        xOffset = (dimGrid.width - dimShape.width) / 2;
        yOffset = (dimGrid.height - dimShape.height) / 2;
        cellGrid.clear();

        // draw shape
        Enumeration cells = shape.getCells();
        while (cells.hasMoreElements()) {
            int[] cell = (int[]) cells.nextElement();
            cellGrid.setCell(xOffset + cell[0], yOffset + cell[1], true);
        }

    }
}
