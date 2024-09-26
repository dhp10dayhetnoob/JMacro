package input;

import java.util.ArrayList;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import main.EventListener;
import main.Recorder;

public class Keyboard implements NativeKeyListener, EventListener {
	private ArrayList<InputObject> loggedRecording;
	private boolean enabled;
	private double recordingStartTime; //use this instead of using RecordingList !!!
	
	public Keyboard(ArrayList<InputObject> loggedRecording) {
		this.loggedRecording = loggedRecording;
		this.enabled = false;
	}
	
	public void overWriteRecording(ArrayList<InputObject> loggedRecording) {
		this.loggedRecording = loggedRecording;
	}
		 
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!enabled || keyCode == Recorder.PLAYBACK_HOTKEY || keyCode == Recorder.RECORD_HOTKEY) {
			return;
		}
		
		double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
    	this.loggedRecording.add(new InputObject(
    		elapsedTime,
			(byte) 1,
			KeyEventConverter.convertNativeToKeyEvent(keyCode),
			true
		));
	}
	
	// Handle key release events
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!enabled || keyCode == Recorder.PLAYBACK_HOTKEY || keyCode == Recorder.RECORD_HOTKEY) {
			return;
		}
		
		double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
    	this.loggedRecording.add(new InputObject(
    		elapsedTime,
			(byte) 1,
			KeyEventConverter.convertNativeToKeyEvent(keyCode),
			false
		));
	}
	
	@Override
	public void onEventTriggered(int type, boolean enabled) {
		if (type == 1) {
			this.enabled = enabled;
			
			if (enabled == true) {
				this.recordingStartTime = System.nanoTime();
			}
		}
	} 
}