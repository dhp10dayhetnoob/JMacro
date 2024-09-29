package gui;

import input.InputObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

public class MacroEditor {
    private ArrayList<InputObject> macroList;
    private JFrame frame;
    private JTable macroTable;
    private DefaultTableModel tableModel;

    public MacroEditor(ArrayList<InputObject> macroList) {
        this.macroList = macroList;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Macro Editor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Timestamp", "Input Type", "Details"};
        tableModel = new DefaultTableModel(columns, 0);
        macroTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(macroTable);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnRemove = new JButton("Remove");
        JButton btnModify = new JButton("Modify");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnModify);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        populateTable(); // Populate with existing macro entries

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                promptForMacro(-1); // Pass -1 to indicate new addition
            }
        });

        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = macroTable.getSelectedRow();
                if (selectedRow >= 0) {
                    macroList.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "No row selected");
                }
            }
        });

        btnModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = macroTable.getSelectedRow();
                if (selectedRow >= 0) {
                    promptForMacro(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "No row selected");
                }
            }
        });

        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void populateTable() {
        for (int i = 0; i < macroList.size(); i++) {
            addMacroToTable(i, macroList.get(i));
        }
    }

    private void addMacroToTable(int index, InputObject macro) {
    	String[] parsed = rowInfo(index, macro);
        String inputTypeText = parsed[0];
        String detailsText = parsed[1];

        tableModel.addRow(new Object[]{macro.getTimeStamp(), inputTypeText, detailsText});
    }

    private void promptForMacro(int index) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Input Types:"));
        panel.add(new JLabel(("1=Keyboard, 2=Mouse Button, 3=Mouse Move, 4=Delay")));
        
        JTextField timeStampField = new JTextField();
        JTextField inputTypeField = new JTextField();
        JTextField keyOrButtonField = new JTextField();
        JCheckBox upOrDownField = new JCheckBox("Pressed");

        if (index != -1) {
            // Modify existing macro
            InputObject macro = macroList.get(index);
            timeStampField.setText(String.valueOf(macro.getTimeStamp()));
            inputTypeField.setText(String.valueOf(macro.getInputType()));

            if (macro.getInputType() == 1) {
                keyOrButtonField.setText(KeyEvent.getKeyText(macro.getKeyOrButton()));
                upOrDownField.setSelected(macro.getIsUpOrDown());
            } else if (macro.getInputType() == 2) {
            	keyOrButtonField.setText(Integer.toString(MouseButtonTranslator.getKeyCodeFromText(macro.getKeyOrButton())));
                upOrDownField.setSelected(macro.getIsUpOrDown());
            } else if (macro.getInputType() == 3) {
                keyOrButtonField.setText(macro.getMouseX() + ", " + macro.getMouseY());
            } else if (macro.getInputType() == 4) {
                keyOrButtonField.setText(""); // No specific input for delay
                upOrDownField.setVisible(false); // Hide checkbox for delay
            }
        }

        panel.add(new JLabel("Timestamp:"));
        panel.add(timeStampField);
        panel.add(new JLabel("Input Type:"));
        panel.add(inputTypeField);
        panel.add(new JLabel("Key/Button or Mouse Coords: "));
        panel.add(keyOrButtonField);
        panel.add(new JLabel("Key/Button State:"));
        panel.add(upOrDownField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Macro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double timeStamp = Double.parseDouble(timeStampField.getText());
                byte inputType = Byte.parseByte(inputTypeField.getText());

                if (timeStamp < 0) {
                    throw new IllegalArgumentException("Timestamp cannot be negative.");
                }

                // Handle Key/Button input or Mouse input based on inputType
                if (inputType == 1 || inputType == 2) { // Key/Button Input
                    String keyOrButtonFieldText = keyOrButtonField.getText();
                    int keyOrButtonCode = -1;
                    
                    if (inputType == 1) {
                    	keyOrButtonCode = KeyCodeTranslator.getKeyCodeFromText(keyOrButtonFieldText);
                    } else {
                    	keyOrButtonCode = MouseButtonTranslator.getKeyCodeFromText(Integer.parseInt(keyOrButtonFieldText));
                    }

                    if (keyOrButtonCode == -1) {
                        throw new IllegalArgumentException("Invalid key/button.");
                    }

                    boolean upOrDown = upOrDownField.isSelected();
                    InputObject newMacro = new InputObject(timeStamp, inputType, keyOrButtonCode, upOrDown);

                    updateMacroList(index, newMacro);

                } else if (inputType == 3) { // Mouse Move
                    String[] coords = keyOrButtonField.getText().split(",");
                    if (coords.length != 2) {
                        throw new IllegalArgumentException("Mouse coordinates must be in the format 'X, Y'.");
                    }

                    int mouseX = Integer.parseInt(coords[0].trim());
                    int mouseY = Integer.parseInt(coords[1].trim());

                    InputObject newMacro = new InputObject(timeStamp, inputType, mouseX, mouseY);
                    updateMacroList(index, newMacro);

                } else if (inputType == 4) { // Delay
                    InputObject newMacro = new InputObject(timeStamp, inputType);
                    updateMacroList(index, newMacro);

                } else {
                    throw new IllegalArgumentException("Invalid input type.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number format. Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateMacroList(int index, InputObject newMacro) {
        if (index == -1) {
            // Adding a new macro
            macroList.add(newMacro);
            
            // Sort and maintain the list order
            macroList.sort(Comparator.comparing(InputObject::getTimeStamp));

            // Find the index of the new macro after sorting
            int newIndex = macroList.indexOf(newMacro);
            addMacroToTable(newIndex, newMacro); // Add to table only for new entries
        } else {
            // Modifying an existing macro
            InputObject existingMacro = macroList.get(index);
            
            // Update the existing macro
            macroList.set(index, newMacro);
            
            if (existingMacro.getTimeStamp() == newMacro.getTimeStamp()) {
                // If the timestamp hasn't changed, just update the table row
                updateTableRow(index, newMacro);
            } else {
                // Sort and find the correct index for the new timestamp
                macroList.sort(Comparator.comparing(InputObject::getTimeStamp));
                int newIndex = macroList.indexOf(newMacro);
                
                // Update the table row only if the index changes
                if (newIndex != index) {
                    tableModel.removeRow(index); // Remove old row
                    addMacroToTable(newIndex, newMacro); // Add to the correct new position
                } else {
                    updateTableRow(newIndex, newMacro); // If it's in the same index, just update
                }
            }
        }
    }
    
    private String[] rowInfo(int index, InputObject macro) {
    	String[] returnString = new String[2];
    	
    	switch (macro.getInputType()) {
        case 1: // Keyboard Input
            returnString[0] = "Keyboard";
            String keyText = KeyEvent.getKeyText(macro.getKeyOrButton());
            returnString[1] = "Key: " + keyText + ", " + (macro.getIsUpOrDown() ? "Pressed" : "Released");
            break;
        case 2: // Mouse Input
        	returnString[0] = "Mouse Button";
            String buttonText = Integer.toString(MouseButtonTranslator.getKeyCodeFromText(macro.getKeyOrButton()));
            returnString[1] = "Button: " + buttonText + ", " + (macro.getIsUpOrDown() ? "Pressed" : "Released");
            break;
        case 3: // Mouse Move
        	returnString[0] = "Mouse Move";
        	returnString[1] = "X: " + macro.getMouseX() + ", Y: " + macro.getMouseY();
            break;
        case 4: // Delay
        	returnString[0] = "Delay";
        	returnString[1] = "Delay";
            break;
        default:
        	returnString[0] = "Unknown";
    	}
    	
    	return returnString;
    }

    private void updateTableRow(int index, InputObject macro) {
    	String[] parsed = rowInfo(index, macro);
        String inputTypeText = parsed[0];
        String detailsText = parsed[1];

        tableModel.setValueAt(macro.getTimeStamp(), index, 0);
        tableModel.setValueAt(inputTypeText, index, 1);
        tableModel.setValueAt(detailsText, index, 2);
    }
}