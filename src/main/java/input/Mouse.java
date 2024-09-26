package input;

import java.util.ArrayList;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import main.EventListener;

public class Mouse implements NativeMouseListener, NativeMouseMotionListener, EventListener  {
	private final double MOUSE_MOVE_THROTTLE = .033; //max 30 mouse moves a second
	private double nextMouseMove;
	private double recordingStartTime; //use this instead of using RecordingList !!!
	
	private ArrayList<InputObject> loggedRecording;
	private boolean enabled;
	
	public Mouse(ArrayList<InputObject> loggedRecording) {
		this.loggedRecording = loggedRecording;
		this.enabled = false;
		nextMouseMove = 0;
	}
	
	public void overWriteRecording(ArrayList<InputObject> loggedRecording) {
		this.loggedRecording = loggedRecording;
	}
	
	// Handle mouse button press events
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
    	if (!enabled) {
    		return;
    	}
    	
    	double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
    	this.loggedRecording.add(new InputObject(
    		elapsedTime,
    		(byte) 2,
    		MouseEventConverter.convertNativeToMouseEvent(e.getButton()),
    		true
    	));
    }

    // Handle mouse button release events
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
    	if (!enabled) {
			return;
		}
    	
    	double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
    	this.loggedRecording.add(new InputObject(
    		elapsedTime,
        	(byte) 2,
        	MouseEventConverter.convertNativeToMouseEvent(e.getButton()),
        	false
        ));
    }
    
    // Handle mouse movement events
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
    	if (!enabled) {
			return;
		}
    	
    	double currentTime = System.currentTimeMillis();
    	if ((currentTime + MOUSE_MOVE_THROTTLE) >= nextMouseMove) {
    		double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
        	this.loggedRecording.add(new InputObject(
        		elapsedTime,
    	        (byte) 3,
    	        e.getX(),
    	        e.getY()
    		));
    		
    		nextMouseMove = currentTime + MOUSE_MOVE_THROTTLE * 1000;
    	}
    }
    
    @Override
	public void onEventTriggered(int type, boolean enabled, boolean authorative) {
		if (type == 1) {
			this.enabled = enabled;
			
			if (enabled == true) {
				this.recordingStartTime = System.nanoTime();
			}
		}
	} 
}