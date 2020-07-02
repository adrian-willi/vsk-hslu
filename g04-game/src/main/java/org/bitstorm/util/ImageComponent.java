/*
 * Easily add images to your UI
 * Copyright 2003 Edwin Martin <edwin@bitstorm.org>
 *
 */
package org.bitstorm.util;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.ImageObserver;

import org.bitstorm.gameoflife.StandaloneGameOfLife;

import ch.hslu.vsk.logger.api.Logger;

/**
 * A component to add a image to (e.g.) a panel.
 *
 * Supports animated GIF's.
 *
 * @author Edwin Martin
 */
public final class ImageComponent extends Canvas implements ImageObserver {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("ImageComponent");
    private static final long serialVersionUID = 1L;

    private Image image;

    /**
     * Constucts a ImageComponent.
     *
     * This constructor uses the MediaTracker to get the width and height of the
     * image, so the constructor has to wait for the image to load. This is not a
     * good idea when getting images over a slow network. There is a timeout of 3
     * sec.
     *
     * Maybe one time the MediaTracker can be replaced, so it returns when the width
     * and height are known, not when the whole image is loaded. Then the
     * constructor will be much faster and better suited for loading images over a
     * slow connection.
     *
     * @param image the image to show
     */
    public ImageComponent(final Image image) {
        log.debug("New Image");
        this.image = image;
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
            // Wait max 3 sec
            tracker.waitForID(0, 3000);
        } catch (InterruptedException e) {
            log.error("Failed getting dimensions of Image" + e);
        }
    }

    /**
     * Draw the image.
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g) {
        log.debug("Paint graphics");
        // TODO Check how often this method gets called, probably a bad place for a log
        // statement ;)
        g.drawImage(image, 0, 0, this);
    }

    /**
     * Returns preferred size. At the first pack()ing of the Window, the image might
     * nog be completely read and getPreferredSize() might return the wrong size.
     * imageUpdate() corrects this.
     *
     * @return preferred size.
     * @see java.awt.Component#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        log.debug("return prefered size");
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    /**
     * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int,
     *      int, int)
     */
    @Override
    public boolean imageUpdate(final Image img, final int infoflags, final int x, final int y, final int width,
            final int height) {
        boolean isImageRead = (infoflags & ImageObserver.ALLBITS) != 0;
        log.debug("repaint image");
        repaint();
        return !isImageRead; // return true while image not completely read
    }

}
