package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.JOptionPane;

import input.InputObject;

public class InputSimulator {
	public static Robot ROBOT;
	private static int currentWidth;
	private static int currentHeight;
	private static int recordedWidth;
	private static int recordedHeight;
	
	private static double widthMultiplier = 1;
	private static double heightMultiplier = 1;
	
	//not very pretty i know
	static {
		try {
			ROBOT = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        ROBOT.setAutoWaitForIdle(true);
	}
	
	public static void setResolution(String title, int width, int height) {
		if (title.equals("Current")) {
			currentWidth = width;
			currentHeight = height;
		} else if (title.equals("Recorded")) {
			recordedWidth = width;
			recordedHeight = height;
		}
		
		widthMultiplier = (double) currentWidth/(double) recordedWidth;
		heightMultiplier = (double) currentHeight/(double) recordedHeight;
	}
	
	public static void simulate(InputObject input) {
		byte type = input.getInputType();
		if (type == 4) {
			return;
		}
		
		if (type == 3) {
			ROBOT.mouseMove((int) (input.getMouseX() * widthMultiplier), (int) (input.getMouseY() * heightMultiplier));
		} else if (type == 2) {
			if (input.getKeyOrButton() == -1) {
				return;
			}
			
			boolean isDown = input.getIsUpOrDown();
			if (isDown) {
				ROBOT.mousePress(input.getKeyOrButton());
		} else {
				ROBOT.mouseRelease(input.getKeyOrButton());
			}
		} else if (type == 1) {
			if (input.getKeyOrButton() == -1) {
				return;
			}
			
			boolean isDown = input.getIsUpOrDown();
			if (isDown) {
				ROBOT.keyPress(input.getKeyOrButton());
			} else {
				ROBOT.keyRelease(input.getKeyOrButton());
			}
		}
	}
}