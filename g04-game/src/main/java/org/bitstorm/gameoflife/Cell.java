package org.bitstorm.gameoflife;

import ch.hslu.vsk.logger.api.Logger;

/**
 * Every cell in the grid is a Cell-object. So it must be as small as possible.
 * Because every cell is pre-generated, no cells have to be generated when the
 * Game Of Life playw. Whether a cell is alive or not, is not part of the
 * Cell-object.
 *
 * @author Edwin Martin
 *
 */
public final class Cell {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("Cell");

    /**
     * HASHFACTOR must be larger than the maximum number of columns. That is: the
     * max width of a monitor in pixels. It should also be smaller than 65536.
     * (sqrt(MAXINT)).
     */
    private static final int HASHFACTOR = 5000;

    private final short col;
    private final short row;

    /**
     * Number of neighbours of this cell. Determines the next state.
     */
    private byte neighbour; // Neighbour is International English

    /**
     * Constructor.
     *
     * @param col column of cell.
     * @param row row or cell.
     */
    public Cell(final int col, final int row) {
        this.col = (short) col;
        this.row = (short) row;
        neighbour = 0;
    }

    /**
     * Compare cell-objects for use in hashtables.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Cell)) {
            return false;
        }
        return col == ((Cell) o).col && row == ((Cell) o).row;
    }

    /**
     * Calculate hash for use in hashtables.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HASHFACTOR * row + col;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String msg = "Cell at (" + col + ", " + row + ") with " + neighbour + " neighbour";
        if (neighbour == 1) {
            msg = msg + "s";
        }
        log.debug(msg);
        return msg;
    }

    /**
     * Liefert neighbour zurück.
     *
     * @return Liefert neighbour.
     */
    public byte getNeighbour() {
        return neighbour;
    }

    /**
     * Setzt das Attribut neighbour.
     *
     * @param neighbour Wert für neighbour.
     */
    public void setNeighbour(final byte neighbour) {
        this.neighbour = neighbour;
    }

    /**
     * Liefert col zurück.
     *
     * @return Liefert col.
     */
    public short getCol() {
        return col;
    }

    /**
     * Liefert row zurück.
     *
     * @return Liefert row.
     */
    public short getRow() {
        return row;
    }
}
