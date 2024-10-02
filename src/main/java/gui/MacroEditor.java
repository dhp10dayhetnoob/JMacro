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

    // Input type selection and form handling
    private void promptForMacro(int index) {
        byte selectedInputType = -1;
        if (index == -1) {
            selectedInputType = showInputTypeSelection(); // Select input type
            if (selectedInputType == -1) return; // User canceled
        } else {
            selectedInputType = macroList.get(index).getInputType(); // Modify existing macro
        }

        switch (selectedInputType) {
            case 1: // Keyboard Input
                promptForKeyboardInput(index);
                break;
            case 2: // Mouse Button Input
                promptForMouseButtonInput(index);
                break;
            case 3: // Mouse Move Input
                promptForMouseMoveInput(index);
                break;
            case 4: // Delay Input
                promptForDelayInput(index);
                break;
            default:
                JOptionPane.showMessageDialog(frame, "Invalid input type.");
        }
    }

    private byte showInputTypeSelection() {
        String[] options = {"Keyboard", "Mouse Button", "Mouse Move", "Delay"};
        int selection = JOptionPane.showOptionDialog(
                frame,
                "Select Input Type",
                "Input Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if (selection == -1) return -1; // User canceled

        return (byte) (selection + 1); // Returns 1 for Keyboard, 2 for Mouse Button, etc.
    }

    private void promptForKeyboardInput(int index) {
        JTextField timeStampField = new JTextField();
        JTextField keyField = new JTextField();
        JCheckBox pressedCheckBox = new JCheckBox("Pressed");

        if (index != -1) {
            InputObject macro = macroList.get(index);
            timeStampField.setText(String.valueOf(macro.getTimeStamp()));
            keyField.setText(KeyEvent.getKeyText(macro.getKeyOrButton()));
            pressedCheckBox.setSelected(macro.getIsUpOrDown());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Timestamp:"));
        panel.add(timeStampField);
        panel.add(new JLabel("Key:"));
        panel.add(keyField);
        panel.add(new JLabel("Key State:"));
        panel.add(pressedCheckBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Keyboard Input", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double timeStamp = Double.parseDouble(timeStampField.getText());
                int keyCode = KeyCodeTranslator.getKeyCodeFromText(keyField.getText());
                boolean pressed = pressedCheckBox.isSelected();

                InputObject newMacro = new InputObject(timeStamp, (byte) 1, keyCode, pressed);
                updateMacroList(index, newMacro);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        }
    }

    private void promptForMouseButtonInput(int index) {
        JTextField timeStampField = new JTextField();
        JTextField buttonField = new JTextField();
        JCheckBox pressedCheckBox = new JCheckBox("Pressed");

        if (index != -1) {
            InputObject macro = macroList.get(index);
            timeStampField.setText(String.valueOf(macro.getTimeStamp()));
            buttonField.setText(Integer.toString(MouseButtonTranslator.getKeyCodeFromText(macro.getKeyOrButton())));
            pressedCheckBox.setSelected(macro.getIsUpOrDown());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Timestamp:"));
        panel.add(timeStampField);
        panel.add(new JLabel("Mouse Button:"));
        panel.add(buttonField);
        panel.add(new JLabel("Button State:"));
        panel.add(pressedCheckBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Mouse Button Input", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double timeStamp = Double.parseDouble(timeStampField.getText());
                int buttonCode = Integer.parseInt(buttonField.getText());
                boolean pressed = pressedCheckBox.isSelected();

                InputObject newMacro = new InputObject(timeStamp, (byte) 2, buttonCode, pressed);
                updateMacroList(index, newMacro);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        }
    }

    private void promptForMouseMoveInput(int index) {
        JTextField timeStampField = new JTextField();
        JTextField coordsField = new JTextField();

        if (index != -1) {
            InputObject macro = macroList.get(index);
            timeStampField.setText(String.valueOf(macro.getTimeStamp()));
            coordsField.setText(macro.getMouseX() + ", " + macro.getMouseY());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Timestamp:"));
        panel.add(timeStampField);
        panel.add(new JLabel("Mouse Coords (X, Y):"));
        panel.add(coordsField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Mouse Move Input", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double timeStamp = Double.parseDouble(timeStampField.getText());
                String[] coords = coordsField.getText().split(",");
                int mouseX = Integer.parseInt(coords[0].trim());
                int mouseY = Integer.parseInt(coords[1].trim());

                InputObject newMacro = new InputObject(timeStamp, (byte) 3, mouseX, mouseY);
                updateMacroList(index, newMacro);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        }
    }

    private void promptForDelayInput(int index) {
        JTextField timeStampField = new JTextField();

        if (index != -1) {
            InputObject macro = macroList.get(index);
            timeStampField.setText(String.valueOf(macro.getTimeStamp()));
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Timestamp:"));
        panel.add(timeStampField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Delay Input", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double timeStamp = Double.parseDouble(timeStampField.getText());

                InputObject newMacro = new InputObject(timeStamp, (byte) 4);
                updateMacroList(index, newMacro);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        }
    }

    private void updateMacroList(int index, InputObject newMacro) {
        if (index == -1) {
            macroList.add(newMacro);
            addMacroToTable(macroList.size() - 1, newMacro);
        } else {
            macroList.set(index, newMacro);
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        populateTable();
    }

    private String[] rowInfo(int index, InputObject macro) {
        String inputTypeText = "";
        String detailsText = "";

        switch (macro.getInputType()) {
            case 1: // Keyboard
                inputTypeText = "Keyboard";
                detailsText = KeyEvent.getKeyText(macro.getKeyOrButton()) + (macro.getIsUpOrDown() ? " Pressed" : " Released");
                break;
            case 2: // Mouse Button
                inputTypeText = "Mouse Button";
                detailsText = "Button " + macro.getKeyOrButton() + (macro.getIsUpOrDown() ? " Pressed" : " Released");
                break;
            case 3: // Mouse Move
                inputTypeText = "Mouse Move";
                detailsText = "X: " + macro.getMouseX() + " Y: " + macro.getMouseY();
                break;
            case 4: // Delay
                inputTypeText = "Delay";
                detailsText = "Timestamp: " + macro.getTimeStamp();
                break;
        }

        return new String[]{inputTypeText, detailsText};
    }
}