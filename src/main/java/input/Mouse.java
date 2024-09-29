package input;

import java.util.ArrayList;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import jdk.internal.org.jline.terminal.MouseEvent;
import main.EventListener;

public class Mouse implements NativeMouseListener, NativeMouseMotionListener, EventListener  {
	private static final double MOUSE_MOVE_THROTTLE = .033; //max 30 mouse moves a second
	private static final double MOUSE_MOVE_TIMESHIFT = 0.001;
	private double nextMouseMove;
	private double recordingStartTime; //use this instead of using RecordingList !!!
	
	private ArrayList<InputObject> loggedRecording;
	private boolean enabled;
	
	public Mouse(ArrayList<InputObject> loggedRecording) {
		this.loggedRecording = loggedRecording;
		enabled = false;
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
    	
    	int converted = MouseEventConverter.convertNativeToMouseEvent(e.getButton());
    	double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
    	loggedRecording.add(new InputObject(elapsedTime - MOUSE_MOVE_TIMESHIFT, (byte) 3, e.getX(), e.getY()));
    	loggedRecording.add(new InputObject(elapsedTime, (byte) 2, converted, true));
    }

    // Handle mouse button release events
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
    	if (!enabled) {
			return;
		}
    	
    	int converted = MouseEventConverter.convertNativeToMouseEvent(e.getButton());
    	double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
    	loggedRecording.add(new InputObject(elapsedTime - MOUSE_MOVE_TIMESHIFT, (byte) 3, e.getX(), e.getY()));
    	loggedRecording.add(new InputObject(elapsedTime, (byte) 2, converted, false));
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
        	loggedRecording.add(new InputObject(elapsedTime, (byte) 3, e.getX(), e.getY()));
    		
    		nextMouseMove = currentTime + MOUSE_MOVE_THROTTLE * 1000;
    	}
    }
    
    @Override
	public void onEventTriggered(int type, boolean enabled) {
		if (type == 1) {
			this.enabled = enabled;
			
			if (enabled == true) {
				recordingStartTime = System.nanoTime();
			}
		}
	} 
}