/*
 * Easily show an alert.
 * Copyright 2003 Edwin Martin <edwin@bitstorm.org>
 *
 */
package org.bitstorm.util;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import org.bitstorm.gameoflife.StandaloneGameOfLife;

import ch.hslu.vsk.logger.api.Logger;

/**
 * AlertBox shows a message besides a warning symbol.
 *
 * @author Edwin Martin
 *
 */
public final class AlertBox extends Dialog {

    private final Logger log = StandaloneGameOfLife.getLoggerSetup().getLogger("AlertBox");

    private static final long serialVersionUID = 1L;

    private final Button okButton;

    /**
     * Contructs a AlertBox.
     *
     * Use the newline character '\n' in the message to seperate lines.
     *
     * @param parent  parent frame
     * @param title   title of the dialog box
     * @param message the message to show
     */
    public AlertBox(final Frame parent, final String title, final String message) {
        super(parent, title, false);
        log.debug("New AlertBox - " + title);

        Image alertImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("alert.gif"));
        ImageComponent alertImageComponent = new ImageComponent(alertImage);
        okButton = new Button(" OK ");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                close();
            }
        });
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        StringTokenizer st = new StringTokenizer(message, "\n");
        Panel messagePanel = new Panel(new GridLayout(st.countTokens(), 1));
        log.debug("add Tokens from String to messagePannel");
        while (st.hasMoreTokens()) {
            messagePanel.add(new Label(st.nextToken()));
        }
        add("West", alertImageComponent);
        add("Center", messagePanel);
        add("South", buttonPanel);
        enableEvents(Event.WINDOW_DESTROY);
        setResizable(false);
        setModal(true);
        pack();
        Point p = parent.getLocation();
        Dimension dim = parent.getSize();
        setLocation(p.x + dim.width / 2 - 150, p.y + dim.height / 2 - 75);
        setVisible(true);
    }

    /**
     * Close dialog box.
     */
    private void close() {
        this.setVisible(true);
        this.dispose();
        log.debug("Close AlertBox");
    }

    /**
     * Process close window button.
     *
     * @see java.awt.Component#processEvent(java.awt.AWTEvent)
     */
    @Override
    public void processEvent(final AWTEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            close();
        }
    }

}
