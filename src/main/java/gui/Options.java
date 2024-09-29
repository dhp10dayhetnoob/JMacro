package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import input.KeyEventConverter;
import main.InputSimulator;
import main.Recorder;

public class Options {
    private MainFrame parent;
    private JTextField sizeField; // Single text field for size and resolution

    public Options(MainFrame parent) {
        this.parent = parent;

        // Create the popup menu with styling
        JPopupMenu optionsMenu = new JPopupMenu();
        optionsMenu.setBackground(new Color(0, 0, 0, 255)); // Match background color
        optionsMenu.setBorder(new LineBorder(Color.WHITE, 1)); // Remove default border

        Font menuFont = new Font("Arial", Font.PLAIN, 16); // Match the font

        // Continuous Playback Checkbox
        JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem("Continuous Playback");
        checkBoxMenuItem.setSelected(false); // Default state
        checkBoxMenuItem.setBackground(new Color(0, 0, 0, 255)); // Match background color
        checkBoxMenuItem.setForeground(Color.WHITE); // Match text color
        checkBoxMenuItem.setFont(menuFont); // Match font
        checkBoxMenuItem.setBorder(new EmptyBorder(5, 10, 5, 10)); // Add padding
        checkBoxMenuItem.addActionListener(e -> {
            boolean isSelected = checkBoxMenuItem.isSelected();
            parent.parent.setContinuousPlayback(isSelected);
        });

        // Add checkboxes
        optionsMenu.add(checkBoxMenuItem);
        optionsMenu.add(new JSeparator());
        optionsMenu.add(createAlwaysOnTopItem(menuFont));
        optionsMenu.add(new JSeparator());
        optionsMenu.add(createStickToTopItem(menuFont));
        optionsMenu.add(new JSeparator());

        // Add menu items for setting keybinds
        optionsMenu.add(createSetHotkeyItem("Set Record Hotkey", 1, menuFont));
        optionsMenu.add(new JSeparator());
        optionsMenu.add(createSetHotkeyItem("Set Playback Hotkey", 2, menuFont));
        optionsMenu.add(new JSeparator());
        optionsMenu.add(createSetHotkeyItem("Set Pause Hotkey", 3, menuFont));
        optionsMenu.add(new JSeparator());
        
        // Add monitor size and resolution input field
        optionsMenu.add(createSizeAndResolutionField(menuFont, "Recording"));
        optionsMenu.add(new JSeparator());
        optionsMenu.add(createSizeAndResolutionField(menuFont, "Current"));

        // Add a mouse listener to show the popup menu
        parent.labelOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show the popup menu at the location of the click
                optionsMenu.show(parent.labelOptions, e.getX(), e.getY());
            }
        });
    }

    private JMenuItem createAlwaysOnTopItem(Font menuFont) {
        JCheckBoxMenuItem alwaysOnTop = new JCheckBoxMenuItem("Always on Top");
        alwaysOnTop.setSelected(true); // Default state
        alwaysOnTop.setBackground(new Color(0, 0, 0, 255)); // Match background color
        alwaysOnTop.setForeground(Color.WHITE); // Match text color
        alwaysOnTop.setFont(menuFont); // Match font
        alwaysOnTop.setBorder(new EmptyBorder(5, 10, 5, 10)); // Add padding

        alwaysOnTop.addActionListener(e -> {
            boolean isSelected = alwaysOnTop.isSelected();
            parent.frame.setAlwaysOnTop(isSelected);
        });

        return alwaysOnTop;
    }

    private JMenuItem createStickToTopItem(Font menuFont) {
        JCheckBoxMenuItem stickToTop = new JCheckBoxMenuItem("Stick to Top");
        stickToTop.setSelected(true); // Default state
        stickToTop.setBackground(new Color(0, 0, 0, 255)); // Match background color
        stickToTop.setForeground(Color.WHITE); // Match text color
        stickToTop.setFont(menuFont); // Match font
        stickToTop.setBorder(new EmptyBorder(5, 10, 5, 10)); // Add padding

        stickToTop.addActionListener(e -> {
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
        });

        return stickToTop;
    }

    private JMenuItem createSetHotkeyItem(String label, int whichKey, Font menuFont) {
        JMenuItem setHotkey = new JMenuItem(label);
        setHotkey.setBackground(new Color(0, 0, 0, 255)); // Match background color
        setHotkey.setForeground(Color.WHITE); // Match text color
        setHotkey.setFont(menuFont); // Match font
        setHotkey.setBorder(new EmptyBorder(5, 10, 5, 10)); // Add padding

        setHotkey.addActionListener(e -> selectNewHotkey(whichKey));

        return setHotkey;
    }

    private JPanel createSizeAndResolutionField(Font menuFont, String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(0, 0, 0, 255)); // Match background color

        sizeField = new JTextField(10); // Single text field for input
        sizeField.setBackground(new Color(0, 0, 0, 255));
        sizeField.setForeground(Color.WHITE);
        sizeField.setBorder(new LineBorder(Color.GRAY, 1));
        sizeField.setFont(menuFont);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        sizeField.setText((int) screenSize.getWidth() + "x" + (int) screenSize.getHeight()); // Initialize with current size

        JButton setSizeButton = new JButton("Set " + title + " Resolution");
        setSizeButton.setBackground(new Color(0, 0, 0, 255)); // Match background color
        setSizeButton.setForeground(Color.WHITE); // Match text color
        setSizeButton.setFont(menuFont); // Match font
        
        setSizeButton.addActionListener(e -> {
            String[] parts = sizeField.getText().trim().split("x");
            if (parts.length == 2) {
                try {
                    int width = Integer.parseInt(parts[0].trim());
                    int height = Integer.parseInt(parts[1].trim());
                    InputSimulator.setResolution(title, width, height);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter in format: WidthxHeight", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(sizeField);
        panel.add(setSizeButton);

        return panel;
    }

    //1 = record, 2 = playback, 3 = pause
    private void selectNewHotkey(int whichKey) {
        parent.interrupt = true;

        // Create a styled dialog for keybind selection
        JDialog keybindDialog = new JDialog((JFrame) null, "Press a Key", true);
        keybindDialog.getContentPane().setBackground(new Color(0, 0, 0, 255)); // Match background color

        String name = "Record";
        if (whichKey == 2) {
        	name = "Playback";
        } else if (whichKey == 3) {
        	name = "Pause";
        }
        
        JLabel instructionLabel = new JLabel("Press a key to set as " + name + " hotkey");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setForeground(Color.WHITE); // Match text color
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Match font

        keybindDialog.setSize(300, 100);
        keybindDialog.setLayout(new BorderLayout());
        keybindDialog.add(instructionLabel, BorderLayout.CENTER);

        // Add padding around the label
        instructionLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add a key listener to capture the next key press
        keybindDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedKey = KeyEventConverter.getNativeByKeyEvent(e.getKeyCode());
                if (selectedKey == -1) {
                    JOptionPane.showMessageDialog(null, "Invalid Input!");
                    return;
                }

                if (whichKey == 1) {
                    if (selectedKey == Recorder.PLAYBACK_HOTKEY || selectedKey == Recorder.PAUSE_HOTKEY) {
                        JOptionPane.showMessageDialog(null, "Keybind already used!");
                        return;
                    }

                    Recorder.RECORD_HOTKEY = selectedKey; // Update the record hotkey
                    parent.labelRecording.setText(String.format(MainFrame.TOPBAR_FORMAT, "Record", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                } else if (whichKey == 2) {
                    if (selectedKey == Recorder.RECORD_HOTKEY || selectedKey == Recorder.PAUSE_HOTKEY) {
                        JOptionPane.showMessageDialog(null, "Keybind already used!");
                        return;
                    }

                    Recorder.PLAYBACK_HOTKEY = selectedKey; // Update the playback hotkey
                    parent.labelPlayback.setText(String.format(MainFrame.TOPBAR_FORMAT, "Playback", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                } else if (whichKey == 3) {
                	if (selectedKey == Recorder.PLAYBACK_HOTKEY || selectedKey == Recorder.RECORD_HOTKEY) {
                		JOptionPane.showMessageDialog(null, "Keybind already used!");
                        return;
                    }

                    Recorder.PAUSE_HOTKEY = selectedKey; // Update the record hotkey
                    parent.labelPaused.setText(String.format(MainFrame.TOPBAR_FORMAT, "Pause", KeyEvent.getKeyText(e.getKeyCode()))); // Update the label to show the new hotkey
                }

                keybindDialog.dispose(); // Close the dialog
                parent.interrupt = false;
            }
        });

        keybindDialog.setAlwaysOnTop(true); // Ensure the dialog is always on top
        keybindDialog.setUndecorated(true); // Remove borders
        keybindDialog.setLocationRelativeTo(null); // Center the dialog
        keybindDialog.setVisible(true); // Show the dialog
    }
}
