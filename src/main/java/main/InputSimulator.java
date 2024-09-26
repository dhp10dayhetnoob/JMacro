package main;

import java.awt.Robot;
import input.InputObject;

public class InputSimulator {
	public static final Robot ROBOT;
	//not very pretty i know
	static {
	    try {
	        ROBOT = new Robot();
	    } catch (final Exception ex) {
	        throw new RuntimeException("Failed to create Robot instance in static block.", ex);
	    }
	}
	
	public static void simulate(InputObject input) {
		byte type = input.getInputType();
		if (type == 4) {
			return;
		}
		
		try {
			if (type == 3) {
				ROBOT.mouseMove(input.getMouseX(), input.getMouseY());
			} else if (type == 2) {
				boolean isDown = input.getIsUpOrDown();
				if (isDown) {
					ROBOT.mousePress(input.getKeyOrButton());
				} else {
					ROBOT.mouseRelease(input.getKeyOrButton());
				}
			} else if (type == 1) {
				boolean isDown = input.getIsUpOrDown();
				if (isDown) {
					ROBOT.keyPress(input.getKeyOrButton());
				} else {
					ROBOT.keyRelease(input.getKeyOrButton());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
