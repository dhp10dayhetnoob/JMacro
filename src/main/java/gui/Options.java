package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import input.KeyEventConverter;
import main.Recorder;

public class Options {
	private MainFrame parent;
	
	public Options(MainFrame parent) {
		this.parent = parent;
		
		// Create the popup menu
        JPopupMenu optionsMenu = new JPopupMenu();

        // Create a checkbox menu item for continuous playback
        JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem("Continuous Playback");
        checkBoxMenuItem.setSelected(false); // Default state
        checkBoxMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = checkBoxMenuItem.isSelected();
                parent.parent.setContinousPlayback(isSelected);
            }
        });
        
        JCheckBoxMenuItem alwaysOnTop = new JCheckBoxMenuItem("Always on Top");
        alwaysOnTop.setSelected(true); // Default state
        alwaysOnTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = alwaysOnTop.isSelected();
                parent.frame.setAlwaysOnTop(isSelected);
            }
        });
        
        JCheckBoxMenuItem stickToTop = new JCheckBoxMenuItem("Stick to Top");
        stickToTop.setSelected(true);
        stickToTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = stickToTop.isSelected();
                parent.frame.dispose();
                parent.frame.setUndecorated(isSelected);
                parent.frame.pack();
                
                if (isSelected) {
                	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int frameWidth = parent.frame.getWidth();
                    int xPos = (screenSize.width / 2) - (frameWidth / 2); // Center horizontally
                    parent.frame.setLocation(xPos, 0); // Stick to the top of the screen
                }
                
                parent.frame.setVisible(true);
            }
        });

        // Add the checkbox item to the popup menu
        optionsMenu.add(checkBoxMenuItem);
        optionsMenu.add(alwaysOnTop);
        optionsMenu.add(stickToTop);

        // Add menu items for setting keybinds
        JMenuItem setRecordHotkey = new JMenuItem("Set Record Hotkey");
        JMenuItem setPlaybackHotkey = new JMenuItem("Set Playback Hotkey");

        // Add action listeners for each hotkey setting
        setRecordHotkey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNewHotkey(true);  // true for Record Hotkey
            }
        });

        setPlaybackHotkey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNewHotkey(false); // false for Playback Hotkey
            }
        });

        // Add the keybind options to the popup menu
        optionsMenu.add(setRecordHotkey);
        optionsMenu.add(setPlaybackHotkey);

        // Add a mouse listener to the "Options" label to show the popup menu when clicked
        parent.labelOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show the popup menu at the location of the click
                optionsMenu.show(parent.labelOptions, e.getX(), e.getY());
            }
        });
	}
	
	private void selectNewHotkey(boolean isRecordHotkey) {
		parent.interrupt = true;
    	
        JDialog keybindDialog = new JDialog((JFrame) null, "Press a Key", true);
        JLabel instructionLabel = new JLabel("Press a key to set as " + (isRecordHotkey ? "Record" : "Playback") + " hotkey");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        keybindDialog.setSize(300, 100);
        keybindDialog.setLayout(new BorderLayout());
        keybindDialog.add(instructionLabel, BorderLayout.CENTER);

        // Add a key listener to capture the next key press
        keybindDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedKey = KeyEventConverter.getNativeByKeyEvent(e.getKeyCode());
                if (selectedKey == -1) {
                	JOptionPane.showMessageDialog(null, "Invalid Input!");
                	return;
                }
                
                if (isRecordHotkey) {
                	if (selectedKey == Recorder.PLAYBACK_HOTKEY) {
                		JOptionPane.showMessageDialog(null, "Keybind already used!");
                		return;
                	}
                	
                    Recorder.RECORD_HOTKEY = selectedKey; // Update the record hotkey
                    parent.labelRecording.setText(String.format(MainFrame.TOPBAR_FORMAT, "Record", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                } else {
                	if (selectedKey == Recorder.RECORD_HOTKEY) {
                		JOptionPane.showMessageDialog(null, "Keybind already used!");
                		return;
                	}
                	
                    Recorder.PLAYBACK_HOTKEY = selectedKey; // Update the playback hotkey
                    parent.labelPlayback.setText(String.format(MainFrame.TOPBAR_FORMAT, "Play/Stop", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                }
                
                parent.interrupt = false;
                keybindDialog.dispose(); // Close the dialog after key is selected
            }
        });

        keybindDialog.setFocusable(true);
        keybindDialog.setLocationRelativeTo(null); // Center the dialog on screen
        keybindDialog.setVisible(true); // Show the dialog and wait for input
    }
}
