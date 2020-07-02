/**
 * Copyright 1996-2004 Edwin Martin <edwin@bitstorm.nl>
 *
 * @author Edwin Martin
 */
package org.bitstorm.gameoflife;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Hashtable;

import ch.hslu.vsk.logger.api.Logger;

/**
 * Contains the cellgrid, the current shape and the Game Of Life algorithm that
 * changes it.
 *
 * @author Edwin Martin
 */
public final class GameOfLifeGrid implements CellGrid {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("GameOfLifeGrid");

    private int numberOfAIOBExceptions = 0;
    private int cellRows;
    private int cellCols;
    private int generations;

    /**
     * Contains the current, living shape. It's implemented as a hashtable. Tests
     * showed this is 70% faster than Vector.
     */
    private final Hashtable<Cell, Cell> currentShape;
    private final Hashtable<Cell, Cell> nextShape;
    /**
     * Every cell on the grid is a Cell object. This object can become quite large.
     */
    private Cell[][] grid;

    /**
     * Contructs a GameOfLifeGrid.
     *
     * @param cellCols number of columns
     * @param cellRows number of rows
     */
    public GameOfLifeGrid(final int cellCols, final int cellRows) {
        this.cellCols = cellCols;
        this.cellRows = cellRows;
        currentShape = new Hashtable<Cell, Cell>();
        nextShape = new Hashtable<Cell, Cell>();

        grid = new Cell[cellCols][cellRows];
        for (int c = 0; c < cellCols; c++) {
            for (int r = 0; r < cellRows; r++) {
                grid[c][r] = new Cell(c, r);
            }
        }
        log.debug("Grid created");
    }

    /**
     * Clears grid.
     */
    @Override
    public synchronized void clear() {
        generations = 0;
        currentShape.clear();
        nextShape.clear();
        log.debug("Grid cleared");
    }

    /**
     * Create next generation of shape.
     */
    public synchronized void next() {
        Cell cell;
        int col, row;
        Enumeration<Cell> enumShape;

        generations++;
        nextShape.clear();

        // Reset cells
        enumShape = currentShape.keys();
        while (enumShape.hasMoreElements()) {
            cell = enumShape.nextElement();
            cell.setNeighbour((byte) 0);
        }
        // Add neighbours
        // You can't walk through an hashtable and also add elements. Took me a couple
        // of ours to figure out. Argh!
        // That's why we have a hashNew hashtable.
        enumShape = currentShape.keys();
        while (enumShape.hasMoreElements()) {
            cell = enumShape.nextElement();
            col = cell.getCol();
            row = cell.getRow();
            addNeighbour(col - 1, row - 1);
            addNeighbour(col, row - 1);
            addNeighbour(col + 1, row - 1);
            addNeighbour(col - 1, row);
            addNeighbour(col + 1, row);
            addNeighbour(col - 1, row + 1);
            addNeighbour(col, row + 1);
            addNeighbour(col + 1, row + 1);
        }

        // Bury the dead
        // We are walking through an enum from we are also removing elements. Can be
        // tricky.
        enumShape = currentShape.keys();
        while (enumShape.hasMoreElements()) {
            cell = enumShape.nextElement();
            // Here is the Game Of Life rule (1):
            if (cell.getNeighbour() != 3 && cell.getNeighbour() != 2) {
                currentShape.remove(cell);
            }
        }
        // Bring out the new borns
        enumShape = nextShape.keys();
        while (enumShape.hasMoreElements()) {
            cell = enumShape.nextElement();
            // Here is the Game Of Life rule (2):
            if (cell.getNeighbour() == 3) {
                setCell(cell.getCol(), cell.getRow(), true);
            }
        }
        // Generates to many logs
        // TODO if we need a lot if logs - uncomment the following line
        // log.debug("next generation has been born");
    }

    /**
     * Adds a new neighbour to a cell.
     *
     * @param col Cell-column
     * @param row Cell-row
     */
    public synchronized void addNeighbour(final int col, final int row) {
        try {
            Cell cell = nextShape.get(grid[col][row]);
            if (cell == null) {
                // Cell is not in hashtable, then add it
                Cell c = grid[col][row];
                c.setNeighbour((byte) 1);
                nextShape.put(c, c);
            } else {
                // Else, increments neighbour count
                cell.setNeighbour((byte) (cell.getNeighbour() + 1));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Increase error count and provide some informations once in a while
            numberOfAIOBExceptions++;
            if (numberOfAIOBExceptions % 1000 == 0) {
                log.error("number of border hits by shapes reached " + numberOfAIOBExceptions);
            }
        }
    }

    /**
     * Get enumeration of Cell's.
     *
     * @see org.bitstorm.gameoflife.CellGrid#getEnum()
     */
    @Override
    public Enumeration<Cell> getEnum() {
        return currentShape.keys();
    }

    /**
     * Get value of cell.
     *
     * @param col x-coordinate of cell
     * @param row y-coordinate of cell
     * @return value of cell
     */
    @Override
    public synchronized boolean getCell(final int col, final int row) {
        try {
            return currentShape.containsKey(grid[col][row]);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Index out of Bounds: " + e);
        }
        return false;
    }

    /**
     * Set value of cell.
     *
     * @param col x-coordinate of cell
     * @param row y-coordinate of cell
     * @param c   value of cell
     */
    @Override
    public synchronized void setCell(final int col, final int row, final boolean c) {
        try {
            Cell cell = grid[col][row];
            if (c) {
                currentShape.put(cell, cell);
            } else {
                currentShape.remove(cell);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Index out of Bounds: " + e);
        }
    }

    /**
     * Get number of generations.
     *
     * @return number of generations
     */
    public int getGenerations() {
        return generations;
    }

    /**
     * Get dimension of grid.
     *
     * @return dimension of grid
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(cellCols, cellRows);
    }

    /**
     * Resize grid. Reuse existing cells.
     *
     * @see org.bitstorm.gameoflife.CellGrid#resize(int, int)
     */
    @Override
    public synchronized void resize(final int cellColsNew, final int cellRowsNew) {
        if (cellCols == cellColsNew && cellRows == cellRowsNew) {
            return; // Not really a resize
        }
        // Create a new grid, reusing existing Cell's
        Cell[][] gridNew = new Cell[cellColsNew][cellRowsNew];
        for (int c = 0; c < cellColsNew; c++) {
            for (int r = 0; r < cellRowsNew; r++) {
                if (c < cellCols && r < cellRows) {
                    gridNew[c][r] = grid[c][r];
                } else {
                    gridNew[c][r] = new Cell(c, r);
                }
            }
        }

        // Copy existing shape to center of new shape
        int colOffset = (cellColsNew - cellCols) / 2;
        int rowOffset = (cellRowsNew - cellRows) / 2;
        Cell cell;
        Enumeration<Cell> enumShape;
        nextShape.clear();
        enumShape = currentShape.keys();
        while (enumShape.hasMoreElements()) {
            cell = enumShape.nextElement();
            int colNew = cell.getCol() + colOffset;
            int rowNew = cell.getRow() + rowOffset;
            try {
                nextShape.put(gridNew[colNew][rowNew], gridNew[colNew][rowNew]);
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("Index out of Bounds: " + e);
            }
        }

        // Copy new grid and hashtable to working grid/hashtable
        grid = gridNew;
        currentShape.clear();
        enumShape = nextShape.keys();
        while (enumShape.hasMoreElements()) {
            cell = enumShape.nextElement();
            currentShape.put(cell, cell);
        }

        cellCols = cellColsNew;
        cellRows = cellRowsNew;
    }
}
