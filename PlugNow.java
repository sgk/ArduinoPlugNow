/*
 * Copyright (c) 2012
 * by Shigeru KANEMOTO at SWITCHSCIENCE.
 * All rights reserved.
 */

package com.ppona.plugnow;

import com.ppona.plugnow.I18n;
import static com.ppona.plugnow.I18n._;

import processing.app.Editor;
import processing.app.tools.Tool;
import processing.app.SerialMonitor;
import processing.app.Preferences;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import gnu.io.*;

public class PlugNow implements Tool {
  Editor editor;

  public void init(Editor editor) {
    this.editor = editor;
  }

  public String getMenuTitle() {
    return _("Plug Now...");
  }

  public void run() {
    handlePlugNow();
  }

  protected void handlePlugNow() {
    final JLabel portlabel = new JLabel(_("(not yet detected)"));
    JLabel[] labels = {
      new JLabel(_("Plug Arduino to PC now and click OK to select the serial port.")),
      new JLabel(_("Detected port:")),
      portlabel
    };

    JOptionPane pane = new JOptionPane(
      labels,
      JOptionPane.QUESTION_MESSAGE,
      JOptionPane.OK_CANCEL_OPTION
    );
    pane.setOptions(new String[] { _("OK"), _("Cancel") });
    pane.setInitialValue(_("Cancel"));
    final JDialog dialog = pane.createDialog(editor, _("Plug Now"));
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    final Vector ports = getSerialPortNames();

    //XXX You may want to disable "OK" button while no port is detected.
    // But, JOptionPane seems not provide any method to access buttons.
    // If you want to do that, you have to write your own version of dialog
    // using JFrame.
    final javax.swing.Timer timer = new javax.swing.Timer(
      300,	// 300 msec
      new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  Vector newports = getSerialPortNames();
	  if (newports.indexOf(portlabel.getText()) < 0)
	    portlabel.setText(_("(not yet detected)"));

	  Iterator it = newports.iterator();
	  while (it.hasNext()) {
	    String portname = (String)it.next();
	    //XXX not efficient
	    if (ports.indexOf(portname) < 0) {
	      portlabel.setText(portname);
	      break;
	    }
	  }

	  ports.clear();
	  ports.addAll(newports);
	}
      }
    );

    timer.start();
    dialog.setVisible(true);
    timer.stop();

    if (pane.getValue() == _("OK")) {
      String portname = portlabel.getText();
      if (portname != _("(not yet detected)")) {
	System.out.println(
	  I18n.format(_("Serial port \"{0}\" selected."), portname)
	);
	Preferences.set("serial.port", portname);
	//TODO hack needed to implement these lines.
	//serialMonitor.closeSerialPort();
	//serialMonitor.setVisible(false);
	//serialMonitor = new SerialMonitor(Preferences.get("serial.port"));
	//editor.onBoardOrPortChange();
      }
    }
  }

  private Vector getSerialPortNames() {
    Vector ports = new Vector();
    try {
      Enumeration elem = CommPortIdentifier.getPortIdentifiers();
      while (elem.hasMoreElements()) {
	CommPortIdentifier id = (CommPortIdentifier)elem.nextElement();
	if (id.getPortType() == CommPortIdentifier.PORT_SERIAL)
	  ports.addElement(id.getName());
      }
    }
    catch (Exception exception) {
      System.out.println(_("error retrieving port list"));
      exception.printStackTrace();
    }
    return ports;
  }
}
