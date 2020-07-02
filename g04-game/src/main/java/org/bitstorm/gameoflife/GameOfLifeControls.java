/**
 * The control bar at the bottom. Is put in a seperate object, so it can be
 * replaced by another UI, e.g. on a J2ME phone. Copyright 1996-2004 Edwin
 * Martin <edwin@bitstorm.nl>
 *
 * @author Edwin Martin
 */
package org.bitstorm.gameoflife;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import ch.hslu.vsk.logger.api.Logger;

/**
 * GUI-controls of the Game of Life. It contains controls like Shape, zoom and
 * speed selector, next and start/stop-button. It is a seperate class, so it can
 * be replaced by another implementation for e.g. mobile phones or PDA's.
 * Communicates via the GameOfLifeControlsListener.
 *
 * @author Edwin Martin
 *
 */
public final class GameOfLifeControls extends Panel {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("GameOfLifeControls");

    private static final long serialVersionUID = 1L;

    private Label genLabel;
    private final String genLabelText = "Generations: ";
    private final String nextLabelText = "Next";
    private final String startLabelText = "Start";
    private final String stopLabelText = "Stop";
    public static final String SLOW = "Slow";
    public static final String FAST = "Fast";
    public static final String RAPID = "Rapid";
    public static final String HYPER = "Hyper";
    public static final String BIG = "Big";
    public static final String MEDIUM = "Medium";
    public static final String SMALL = "Small";
    public static final int SIZE_BIG = 11;
    public static final int SIZE_MEDIUM = 7;
    public static final int SIZE_SMALL = 3;
    private final Button startstopButton;
    private final Button nextButton;
    private final Vector<GameOfLifeControlsListener> listeners;
    private final Choice shapesChoice;
    private final Choice zoomChoice;

    /**
     * Contructs the controls.
     */
    public GameOfLifeControls() {
        log.debug("Start construction controls");

        listeners = new Vector<GameOfLifeControlsListener>();

        // pulldown menu with shapes
        shapesChoice = new Choice();

        // Put names of shapes in menu
        Shape[] shapes = ShapeCollection.getShapes();
        for (Shape shape : shapes) {
            shapesChoice.addItem(shape.getName());
        }

        // when shape is selected
        shapesChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                shapeSelected((String) e.getItem());
            }
        });

        // pulldown menu with speeds
        Choice speedChoice = new Choice();

        // add speeds
        speedChoice.addItem(SLOW);
        speedChoice.addItem(FAST);
        speedChoice.addItem(RAPID);
        speedChoice.addItem(HYPER);

        // when item is selected
        speedChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                String arg = (String) e.getItem();
                if (null != arg) {
                    switch (arg) {
                    // slow
                    case SLOW:
                        speedChanged(1000);
                        break;
                    // fast
                    case FAST:
                        speedChanged(100);
                        break;
                    // rapid
                    case RAPID:
                        speedChanged(10);
                        break;
                    // hyperspeed
                    case HYPER:
                        speedChanged(1);
                        break;
                    // set default to slow
                    default:
                        speedChanged(1000);
                    }
                }
            }
        });

        // pulldown menu with speeds
        zoomChoice = new Choice();

        // add speeds
        zoomChoice.addItem(BIG);
        zoomChoice.addItem(MEDIUM);
        zoomChoice.addItem(SMALL);

        // when item is selected
        zoomChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                String arg = (String) e.getItem();
                if (null != arg) {
                    switch (arg) {
                    case BIG:
                        zoomChanged(SIZE_BIG);
                        break;
                    case MEDIUM:
                        zoomChanged(SIZE_MEDIUM);
                        break;
                    case SMALL:
                        zoomChanged(SIZE_SMALL);
                        break;
                    default:
                        zoomChanged(SIZE_MEDIUM);
                    }
                }
            }
        });

        // number of generations
        genLabel = new Label(genLabelText + "         ");

        // start and stop buttom
        startstopButton = new Button(startLabelText);

        // when start/stop button is clicked
        startstopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startStopButtonClicked();
            }
        });

        // next generation button
        nextButton = new Button(nextLabelText);

        // when next button is clicked
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                nextButtonClicked();
            }
        });

        // create panel with controls
        this.add(shapesChoice);
        this.add(nextButton);
        this.add(startstopButton);
        this.add(speedChoice);
        this.add(zoomChoice);
        this.add(genLabel);
        this.validate();

        log.debug("End construction controls");
    }

    /**
     * Add listener for this control.
     *
     * @param listener Listener object
     */
    public void addGameOfLifeControlsListener(final GameOfLifeControlsListener listener) {
        listeners.addElement(listener);
    }

    /**
     * Remove listener from this control.
     *
     * @param listener Listener object
     */
    public void removeGameOfLifeControlsListener(final GameOfLifeControlsListener listener) {
        listeners.removeElement(listener);
    }

    /**
     * Set the number of generations in the control bar.
     *
     * @param generations number of generations
     */
    public void setGeneration(final int generations) {
        genLabel.setText(genLabelText + generations + "         ");
    }

    /**
     * Start-button is activated.
     */
    public void start() {
        startstopButton.setLabel(stopLabelText);
        nextButton.setEnabled(false);
        shapesChoice.setEnabled(false);
    }

    /**
     * Stop-button is activated.
     */
    public void stop() {
        startstopButton.setLabel(startLabelText);
        nextButton.setEnabled(true);
        shapesChoice.setEnabled(true);
    }

    /**
     * Called when the start/stop-button is clicked. Notify event-listeners.
     */
    public void startStopButtonClicked() {
        GameOfLifeControlsEvent event = new GameOfLifeControlsEvent(this);
        for (Enumeration<GameOfLifeControlsListener> e = listeners.elements(); e.hasMoreElements();) {
            e.nextElement().startStopButtonClicked(event);
        }
        log.debug("Start/Stop button clicked");

    }

    /**
     * Called when the next-button is clicked. Notify event-listeners.
     */
    public void nextButtonClicked() {
        GameOfLifeControlsEvent event = new GameOfLifeControlsEvent(this);
        for (Enumeration<GameOfLifeControlsListener> e = listeners.elements(); e.hasMoreElements();) {
            e.nextElement().nextButtonClicked(event);
        }
        log.debug("Next button clicked");

    }

    /**
     * Called when a new speed from the speed pull down is selected. Notify
     * event-listeners.
     *
     * @param speed new speed.
     */
    public void speedChanged(final int speed) {
        GameOfLifeControlsEvent event = GameOfLifeControlsEvent.getSpeedChangedEvent(this, speed);
        for (Enumeration<GameOfLifeControlsListener> e = listeners.elements(); e.hasMoreElements();) {
            e.nextElement().speedChanged(event);
        }
        log.debug("Speed change - sleep: " + speed + "ms");

    }

    /**
     * Called when a new zoom from the zoom pull down is selected. Notify
     * event-listeners.
     *
     * @param zoom new zoom.
     */
    public void zoomChanged(final int zoom) {
        GameOfLifeControlsEvent event = GameOfLifeControlsEvent.getZoomChangedEvent(this, zoom);
        for (Enumeration<GameOfLifeControlsListener> e = listeners.elements(); e.hasMoreElements();) {
            e.nextElement().zoomChanged(event);
        }
        log.debug("Zoom level changed to " + zoom);

    }

    /**
     * Called when a new shape from the shape pull down is selected. Notify
     * event-listeners.
     *
     * @param shapeName new shape name.
     */
    public void shapeSelected(final String shapeName) {
        GameOfLifeControlsEvent event = GameOfLifeControlsEvent.getShapeSelectedEvent(this, shapeName);
        for (Enumeration<GameOfLifeControlsListener> e = listeners.elements(); e.hasMoreElements();) {
            e.nextElement().shapeSelected(event);
        }
        log.debug("Shape selected");

    }

    /**
     * Called when a new cell size from the zoom pull down is selected. Notify
     * event-listeners.
     *
     * @param n cell id.
     */
    public void setZoom(final String n) {
        zoomChoice.select(n);
    }

}
