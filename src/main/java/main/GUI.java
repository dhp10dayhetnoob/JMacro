package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import input.InputObject;
import input.KeyEventConverter;

public class GUI implements NativeKeyListener {
	private static final String TOPBAR_FORMAT = "<html>%s<br><h1 style=\"text-align:center\"><b>%s</b></h1></html>";
	
	private List<EventListener> listeners = new ArrayList<EventListener>();
	private boolean recording;
	private boolean playing;
	private boolean interrupt;
	
	private JLabel labelRecording;
	private JLabel labelPlayback;
	private JLabel labelOptions;
	private JCheckBoxMenuItem checkBoxMenuItem;
	private JFrame frame;
	
	private Recorder parent;
	
    public GUI(Recorder parent) {
    	this.interrupt = false;
    	this.parent = parent;
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    public void setRecording(boolean enabled, boolean authorative) {
    	recording = enabled;
    	
    	if (enabled) {
    		labelRecording.setForeground(Color.RED);
    	} else {
    		labelRecording.setForeground(Color.WHITE);
    	}

    	for (EventListener listener : listeners) {
            listener.onEventTriggered(1, recording, authorative);
        }
    }
    
    public void setPlaying(boolean enabled, boolean authorative) {
    	playing = enabled;
    	
    	if (enabled) {
    		labelPlayback.setForeground(Color.RED);
    	} else {
    		labelPlayback.setForeground(Color.WHITE);
    	}
    	
    	for (EventListener listener : listeners) {
            listener.onEventTriggered(2, playing, authorative);
        }
    }
    
    public boolean isRecording() {
    	return recording;
    }
    
    public boolean isPlaying() {
    	return playing;
    }
    
    public void addListener(EventListener listener) {
    	listeners.add(listener);
    }

    private void createAndShowGUI() {
        // Create the JFrame (window) without borders
        frame = new JFrame("Top Bar");
        frame.setUndecorated(true); // Removes window borders
        frame.setAlwaysOnTop(true); // Keep it always on top of other windows
        frame.setResizable(false); // Not resizable

        // Create the top bar panel with horizontal layout
        JPanel topBarPanel = new JPanel();
        topBarPanel.setLayout(new BoxLayout(topBarPanel, BoxLayout.X_AXIS)); // Horizontal BoxLayout
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding
        topBarPanel.setBackground(new Color(0, 0, 0, 255)); // Semi-transparent black background

        // Create labels (or buttons) for "A", "B", "C", "D"
        labelRecording = new JLabel(String.format(TOPBAR_FORMAT, "Record", "F1"));
        labelPlayback = new JLabel(String.format(TOPBAR_FORMAT, "Play/Stop", "F2"));
        labelOptions = new JLabel("Options"); // Change label to reflect that it's clickable
        JLabel btnExport = new JLabel("Export");
        JLabel btnImport = new JLabel("Import");
        
        // Customize label appearance
        labelRecording.setForeground(Color.WHITE);
        labelPlayback.setForeground(Color.WHITE);
        labelOptions.setForeground(Color.WHITE);
        btnExport.setForeground(Color.WHITE);
        btnImport.setForeground(Color.WHITE);

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        labelRecording.setFont(labelFont);
        labelPlayback.setFont(labelFont);
        labelOptions.setFont(labelFont);
        btnExport.setFont(labelFont);
        btnImport.setFont(labelFont);

        // Add elements to the panel with spacing
        topBarPanel.add(labelRecording);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(labelPlayback);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(labelOptions);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(btnExport);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(btnImport);

        // Add the top bar to the frame
        frame.getContentPane().add(topBarPanel);
        frame.pack(); // Size the window to fit the content

        // Set the frame at the top-center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = frame.getWidth();
        int xPos = (screenSize.width / 2) - (frameWidth / 2); // Center horizontally
        frame.setLocation(xPos, 0); // Stick to the top of the screen
        
        // Add action listeners for Import/Export
        btnExport.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
        		exportRecording();
            }
        });

     // Add action listeners for Import/Export
        btnImport.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
        		 importRecording();
            }
        });

        addOptionsMenu();
        
        // Make the frame visible
        frame.setVisible(true);
    }
    
    private void addOptionsMenu() {
        // Create the popup menu
        JPopupMenu optionsMenu = new JPopupMenu();

        // Create a checkbox menu item for continuous playback
        checkBoxMenuItem = new JCheckBoxMenuItem("Continuous Playback");
        checkBoxMenuItem.setSelected(false); // Default state
        checkBoxMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = checkBoxMenuItem.isSelected();
                parent.setContinousPlayback(isSelected);
            }
        });
        
        JCheckBoxMenuItem alwaysOnTop = new JCheckBoxMenuItem("Always on Top");
        alwaysOnTop.setSelected(true); // Default state
        alwaysOnTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = alwaysOnTop.isSelected();
                frame.setAlwaysOnTop(isSelected);
            }
        });

        // Add the checkbox item to the popup menu
        optionsMenu.add(checkBoxMenuItem);
        optionsMenu.add(alwaysOnTop);

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
        labelOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show the popup menu at the location of the click
                optionsMenu.show(labelOptions, e.getX(), e.getY());
            }
        });
    }

    private void selectNewHotkey(boolean isRecordHotkey) {
    	interrupt = true;
    	
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
                    labelRecording.setText(String.format(TOPBAR_FORMAT, "Record", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                    labelRecording.revalidate();
                    labelRecording.repaint();
                    labelRecording.getParent().revalidate();
                    labelRecording.getParent().repaint();
                } else {
                	if (selectedKey == Recorder.RECORD_HOTKEY) {
                		JOptionPane.showMessageDialog(null, "Keybind already used!");
                		return;
                	}
                	
                    Recorder.PLAYBACK_HOTKEY = selectedKey; // Update the playback hotkey
                    labelPlayback.setText(String.format(TOPBAR_FORMAT, "Play/Stop", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                    labelPlayback.revalidate();
                    labelPlayback.repaint();
                    labelPlayback.getParent().revalidate();
                    labelPlayback.getParent().repaint();
                }
                keybindDialog.dispose(); // Close the dialog after key is selected
                
                interrupt = false;
            }
        });

        keybindDialog.setFocusable(true);
        keybindDialog.setLocationRelativeTo(null); // Center the dialog on screen
        keybindDialog.setVisible(true); // Show the dialog and wait for input
    }
    
    private void exportRecording() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(parent.loggedRecording);
                JOptionPane.showMessageDialog(null, "Export Successful!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Export Failed!");
            }
        }
    }

    @SuppressWarnings("unchecked")
	private void importRecording() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                parent.loggedRecording = (ArrayList<InputObject>) ois.readObject();
                JOptionPane.showMessageDialog(null, "Import Successful!");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Import Failed!");
            }
        }
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    	if (interrupt == true) {
    		return;
    	}
    	
    	if (e.getKeyCode() == Recorder.RECORD_HOTKEY) {
    		setRecording(!recording, false);
    	} else if (e.getKeyCode() == Recorder.PLAYBACK_HOTKEY) {
    		setPlaying(!playing, false);
    	}
    }
}