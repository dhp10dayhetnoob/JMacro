package input;

import java.util.ArrayList;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

public class Mouse implements NativeMouseListener, NativeMouseMotionListener  {
	private final double MOUSE_MOVE_THROTTLE = .05; //max 20 mouse moves a second
	private double nextMouseMove;
	
	private ArrayList<InputObject> recording;
	private boolean enabled;
	
	public Mouse(ArrayList<InputObject> recording) {
		this.recording = recording;
		this.enabled = true;
		nextMouseMove = 0;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	// Handle mouse button press events
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
    	if (!enabled) {
			return;
		}
    	
    	this.recording.add(new InputObject(
    		System.nanoTime(),
    		(byte) 2,
    		(byte) e.getButton(),
    		true
    	));
    	
    	System.out.println("Mouse Pressed: " + e.getButton());
    }

    // Handle mouse button release events
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
    	if (!enabled) {
			return;
		}
    	
    	this.recording.add(new InputObject(
        	System.nanoTime(),
        	(byte) 2,
        	(byte) e.getButton(),
        	false
        ));
    	
        System.out.println("Mouse Released: " + e.getButton());
    }
    
    // Handle mouse movement events
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
    	if (!enabled) {
			return;
		}
    	
    	double currentTime = System.currentTimeMillis();
    	if ((currentTime + MOUSE_MOVE_THROTTLE) >= nextMouseMove) {
    		this.recording.add(new InputObject(
    	        System.nanoTime(),
    	        (byte) 3,
    	        e.getX(),
    	        e.getY()
    		));
    		
    		System.out.println("Mouse Moved: (" + e.getX() + ", " + e.getY() + ")");
    		nextMouseMove = currentTime + MOUSE_MOVE_THROTTLE * 1000;
    	}
    }
}