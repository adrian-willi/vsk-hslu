/**
 * Game of Life v1.4 Standalone version The standalone version extends the
 * applet version. Copyright 1996-2004 Edwin Martin <edwin@bitstorm.nl>
 *
 * @author Edwin Martin
 *
 */
package org.bitstorm.gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.bitstorm.util.AlertBox;
import org.bitstorm.util.EasyFile;
import org.bitstorm.util.LineEnumerator;

import ch.hslu.vsk.logger.api.Logger;
import ch.hslu.vsk.logger.api.LoggerSetup;
import ch.hslu.vsk.logger.api.LoggerSetupFactory;

/**
 * Turns GameOfLife applet into application. It adds a menu, a window,
 * drag-n-drop etc. It can be run stand alone.
 *
 * @author Edwin Martin
 */
public final class StandaloneGameOfLife extends GameOfLife {

    private static LoggerSetup loggerSetup;
    private static Logger log;

    private static final long serialVersionUID = 1L;

    private Frame appletFrame;
    private String[] args;
    private GameOfLifeGridIO gridIO;

    public static void main(final String[] args) {
        initLoggerSetup();
        log = loggerSetup.getLogger("StandaloneGameOfLife");

        // TODO remove sample logging
        log.error("Logging level ERROR");
        log.warn("Logging level WARN");
        log.info("Logging level INFO");
        log.debug("Logging level DEBUG");

        StandaloneGameOfLife gameOfLife = new StandaloneGameOfLife();
        gameOfLife.args = args;
        new AppletFrame("Game of Life", gameOfLife);
    }

    /**
     * Initialize LoggerSetup - get Instance from LoggerSetupFactory.
     *
     * Reads the desired logger Implementation from configuration and get an
     * instance of the loggerSetup implementation.
     *
     */
    public static void initLoggerSetup() {
        if (loggerSetup == null) {
            try {
                InputStream input = StandaloneGameOfLife.class.getClassLoader()
                        .getResourceAsStream("ch/hslu/vsk/config.properties");
                Properties prop = new Properties();
                prop.load(input);

                String fqdn = prop.getProperty("LoggerImplementationURL");
                // TODO Remove sout as soon as we have a stable build
                System.out.println("Loading Logger Implementation:" + fqdn);
                loggerSetup = LoggerSetupFactory.getLoggerSetup(fqdn);
            } catch (Exception e) {
                System.err
                        .println("There was an Error loading or starting the Logging implementation\n" + e.getCause());
            }
        }
    }

    /**
     * returns the one an only LoggerSetup Instance.
     *
     * @return loggerSetup instance
     */
    public static LoggerSetup getLoggerSetup() {
        return loggerSetup;
    }

    /**
     * Initialize UI.
     *
     * @param parent Parent frame.
     * @see java.applet.Applet#init()
     */
    public void init(final Frame parent) {
        log.info("Start UI");
        appletFrame = parent;
        getParams();

        // set background colour
        setBackground(new Color(0x999999));

        // create StandAloneGameOfLifeGrid
        gameOfLifeGrid = new GameOfLifeGrid(cellCols, cellRows);
        gridIO = new GameOfLifeGridIO(gameOfLifeGrid);

        // create GameOfLifeCanvas
        gameOfLifeCanvas = new CellGridCanvas(gameOfLifeGrid, cellSize);

        try {
            // Make GameOfLifeCanvas a drop target
            log.debug("Game of Live as DropTarget");
            new DropTarget(gameOfLifeCanvas, DnDConstants.ACTION_COPY_OR_MOVE, new MyDropListener());
        } catch (NoClassDefFoundError e) {
            // Ignore. Older Java version don't support dnd
            log.error("Error while creating DropTarge" + e);
        }

        // create GameOfLifeControls
        controls = new GameOfLifeControls();
        controls.addGameOfLifeControlsListener(this);

        // put it all together
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints canvasContraints = new GridBagConstraints();
        setLayout(gridbag);
        canvasContraints.fill = GridBagConstraints.BOTH;
        canvasContraints.weightx = 1;
        canvasContraints.weighty = 1;
        canvasContraints.gridx = GridBagConstraints.REMAINDER;
        canvasContraints.gridy = 0;
        canvasContraints.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(gameOfLifeCanvas, canvasContraints);
        add(gameOfLifeCanvas);
        GridBagConstraints controlsContraints = new GridBagConstraints();
        canvasContraints.gridx = GridBagConstraints.REMAINDER;
        canvasContraints.gridy = 1;
        controlsContraints.gridx = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(controls, controlsContraints);
        add(controls);
        setVisible(true);
        validate();
        log.info("End UI");
    }

    /**
     * Set the shape.
     *
     * This is not done in init(), because the window resize in
     * GameOfLifeGridIO.setShape(Shape) needs a fully opened window to do new size
     * calculations.
     */
    public void readShape() {
        if (args.length > 0) {
            gridIO.openShape(args[0]);
            reset();
        } else {
            try {
                setShape(ShapeCollection.getShapeByName("Glider"));
            } catch (ShapeException e) {
                log.error("Error setting Shape" + e);
            }
        }
    }

    /**
     * Override method, called by applet.
     *
     * @see java.applet.Applet#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(final String parm) {
        return System.getProperty(parm);
    }

    /**
     * Shows an alert.
     *
     * @param s text to show
     */
    @Override
    public void alert(final String s) {
        new AlertBox(appletFrame, "Alert", s);
        log.debug("Alert generated" + s);
    }

    /**
     * Do not use showStatus() of the applet.
     *
     * @see java.applet.Applet#showStatus(java.lang.String)
     */
    @Override
    public void showStatus(final String s) {
        // do nothing
    }

    /**
     * get GameOfLifeGridIO.
     *
     * @return GameOfLifeGridIO object
     */
    protected GameOfLifeGridIO getGameOfLifeGridIO() {
        return gridIO;
    }

    /**
     * Handles drag and drops to the canvas.
     *
     * This class does handle the dropping of files and URL's to the canvas. The
     * code is based on the dnd-code from the book Professional Java Programming by
     * Brett Spell.
     *
     * @author Edwin Martin
     *
     */
    class MyDropListener implements DropTargetListener {

        private final DataFlavor urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL",
                "Game of Life URL");

        /**
         * The canvas only supports Files and URL's.
         *
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragEnter(final DropTargetDragEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || event.isDataFlavorSupported(urlFlavor)) {
                return;
            }
            event.rejectDrag();
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
         */
        @Override
        public void dragExit(final DropTargetEvent event) {
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragOver(final DropTargetDragEvent event) {
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dropActionChanged(final DropTargetDragEvent event) {
        }

        /**
         * The file or URL has been dropped.
         *
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        @Override
        public void drop(final DropTargetDropEvent event) {
            // important to first try urlFlavor
            if (event.isDataFlavorSupported(urlFlavor)) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable trans = event.getTransferable();
                    URL url = (URL) (trans.getTransferData(urlFlavor));
                    gridIO.openShape(url);
                    reset();
                    event.dropComplete(true);
                } catch (UnsupportedFlavorException | IOException e) {
                    event.dropComplete(false);
                    log.error("Event drop failed" + e);
                }
            } else if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable trans = event.getTransferable();
                    List<?> list = (List<?>) (trans.getTransferData(DataFlavor.javaFileListFlavor));
                    File droppedFile = (File) list.get(0); // More than one file -> get only first file
                    gridIO.openShape(droppedFile.getPath());
                    reset();
                    event.dropComplete(true);
                } catch (UnsupportedFlavorException | IOException e) {
                    event.dropComplete(false);
                    log.error("Event drop failed" + e);
                }
            }
        }
    }

    /**
     * File open and save operations for GameOfLifeGrid.
     */
    class GameOfLifeGridIO {

        private final String fileExtension = ".cells";
        private final GameOfLifeGrid grid;
        private String filename;

        /**
         * Contructor.
         *
         * @param grid grid to read/write files from/to
         */
        GameOfLifeGridIO(final GameOfLifeGrid grid) {
            this.grid = grid;
        }

        /**
         * Load shape from disk.
         */
        public void openShape() {
            openShape((String) null);
        }

        /**
         * Load shape from disk.
         *
         * @param filename filename to load shape from, or null when no filename given.
         */
        public void openShape(final String filename) {
            // Cope with different line endings ("\r\n", "\r", "\n")
            EasyFile file;
            try {
                if (filename != null) {
                    file = new EasyFile(filename);
                } else {
                    file = new EasyFile(appletFrame, "Open Game of Life file");
                }
                openShape(file);
            } catch (FileNotFoundException e) {
                new AlertBox(appletFrame, "File not found", "Couldn't open this file.\n" + e.getMessage());
                log.error("FileNotFound" + e);
            } catch (IOException e) {
                new AlertBox(appletFrame, "File read error", "Couldn't read this file.\n" + e.getMessage());
                log.error("FileReadError" + e);
            }
        }

        /**
         * Open shape from URL.
         *
         * @param url URL pointing to GameOfLife-file
         */
        public void openShape(final URL url) {
            // Cope with different line endings ("\r\n", "\r", "\n")
            EasyFile file;
            try {
                if (url != null) {
                    file = new EasyFile(url);
                    openShape(file);
                }
            } catch (FileNotFoundException e) {
                new AlertBox(appletFrame, "URL not found", "Couldn't open this URL.\n" + e.getMessage());
                log.error("URLNotFound" + e);
            } catch (IOException e) {
                new AlertBox(appletFrame, "URL read error", "Couldn't read this URL.\n" + e.getMessage());
                log.error("URLReadError" + e);
            }
        }

        /**
         * Use EasyFile object to read GameOfLife-file from.
         *
         * @param file EasyFile-object
         * @throws IOException IO-Exception.
         * @see org.bitstorm.util.EasyFile
         */
        public void openShape(final EasyFile file) throws IOException {
            Shape shape = makeShape(file.getFileName(), file.readText());
            setShape(shape);
        }

        /**
         * Set a shape and optionally resizes window.
         *
         * @param shape Shape to set
         */
        public void setShape(final Shape shape) {
            int width, height;
            Dimension shapeDim = shape.getDimension();
            Dimension gridDim = grid.getDimension();
            if (shapeDim.width > gridDim.width || shapeDim.height > gridDim.height) {
                // Window has to be made larger
                Toolkit toolkit = getToolkit();
                Dimension screenDim = toolkit.getScreenSize();
                Dimension frameDim = appletFrame.getSize();
                int cellSize = getCellSize();
                // Calculate new window size
                width = frameDim.width + cellSize * (shapeDim.width - gridDim.width);
                height = frameDim.height + cellSize * (shapeDim.height - gridDim.height);
                // Does it fit on the screen?
                if (width > screenDim.width || height > screenDim.height) {
                    // With current cellSize, it doesn't fit on the screen
                    // GameOfLifeControls.SIZE_SMALL corresponds with GameOfLifeControls.SMALL
                    int newCellSize = GameOfLifeControls.SIZE_SMALL;
                    width = frameDim.width + newCellSize * shapeDim.width - cellSize * gridDim.width;
                    height = frameDim.height + newCellSize * shapeDim.height - cellSize * gridDim.height;
                    // a little kludge to prevent de window from resizing twice
                    // setNewCellSize only has effect at the next resize
                    gameOfLifeCanvas.setAfterWindowResize(shape, newCellSize);
                    // The UI has to be adjusted, too
                    controls.setZoom(GameOfLifeControls.SMALL);
                } else {
                    // Now resize the window (and optionally set the new cellSize)
                    gameOfLifeCanvas.setAfterWindowResize(shape, cellSize);
                }
                if (width < 400) {
                    width = 400;
                }
                appletFrame.setSize(width, height);
                return;
            }
            try {
                gameOfLifeCanvas.setShape(shape);
            } catch (ShapeException e) {
                log.error("Error setting Shape" + e);
            }
        }

        /**
         * "Draw" the shape on the grid. (Okay, it's not really drawing). The lines of
         * text represent the cells of the shape.
         *
         * @param name name of shape.
         * @param text lines of text.
         * @return Form.
         */
        public Shape makeShape(final String name, final String text) {
            int col = 0;
            int row = 0;
            boolean cell;
            // Cope with different line endings ("\r\n", "\r", "\n")
            int[][] cellArray;
            Vector<int[]> cells = new Vector<>();

            if (text.length() == 0) {
                return null;
            }

            grid.clear();

            Enumeration enumLine = new LineEnumerator(text);
            while (enumLine.hasMoreElements()) {
                String line = (String) enumLine.nextElement();
                if (line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }

                char[] ca = line.toCharArray();
                for (col = 0; col < ca.length; col++) {
                    switch (ca[col]) {
                    case '*':
                    case 'O':
                    case 'o':
                    case 'X':
                    case 'x':
                    case '1':
                        cell = true;
                        break;
                    default:
                        cell = false;
                        break;
                    }
                    if (cell) {
                        cells.addElement(new int[] {col, row});
                    }
                }
                row++;
            }

            cellArray = new int[cells.size()][];
            for (int i = 0; i < cells.size(); i++) {
                cellArray[i] = cells.get(i);
            }
            return new Shape(name, cellArray);
        }

        /**
         * Write shape to disk.
         */
        public void saveShape() {
            int colEnd = 0;
            int rowEnd = 0;
            Dimension dim = grid.getDimension();
            int colStart = dim.width;
            int rowStart = dim.height;

            String lineSeperator = System.getProperty("line.separator");
            StringBuilder text = new StringBuilder("!Generator: Game of Life (http://www.bitstorm.org/gameoflife/)"
                    + lineSeperator + "!Variation: 23/3" + lineSeperator + "!" + lineSeperator);

            for (int row = 0; row < dim.height; row++) {
                for (int col = 0; col < dim.width; col++) {
                    if (grid.getCell(col, row)) {
                        if (row < rowStart) {
                            rowStart = row;
                        }
                        if (col < colStart) {
                            colStart = col;
                        }
                        if (row > rowEnd) {
                            rowEnd = row;
                        }
                        if (col > colEnd) {
                            colEnd = col;
                        }
                    }
                }
            }

            for (int row = rowStart; row <= rowEnd; row++) {
                for (int col = colStart; col <= colEnd; col++) {
                    text.append(grid.getCell(col, row) ? 'O' : '-');
                }
                text.append(lineSeperator);
            }
            EasyFile file;
            try {
                file = new EasyFile(appletFrame, "Save Game of Life file");
                file.setFileName(filename);
                file.setFileExtension(fileExtension);
                file.writeText(text.toString());
            } catch (FileNotFoundException e) {
                new AlertBox(appletFrame, "File error", "Couldn't open this file.\n" + e.getMessage());
                log.error("FileError" + e);
            } catch (IOException e) {
                new AlertBox(appletFrame, "File error", "Couldn't write to this file.\n" + e.getMessage());
                log.error("FileError" + e);
            }
        }
    }
}
