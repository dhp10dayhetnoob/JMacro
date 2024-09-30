package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import input.InputObject;

public final class Files {
	private Files() {} //static class
	
	@SuppressWarnings("unchecked")
	public static ArrayList<InputObject> importRecording(JFrame frame) {
		JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                JOptionPane.showMessageDialog(null, "Import Successful!");
                frame.setTitle("JMacro - " + file.getName());
                return (ArrayList<InputObject>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Import Failed!");
            }
        }
        
        return null;
	}
	
	public static void exportRecording(ArrayList<InputObject> object) {
		if (object.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Nothing to Export!");
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(object);
                fos.close();
                oos.close();
                JOptionPane.showMessageDialog(null, "Export Successful!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Export Failed!");
            }
        }
	}
}