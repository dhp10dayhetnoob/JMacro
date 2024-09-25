package input;

import java.util.ArrayList;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Keyboard implements NativeKeyListener {
	private ArrayList<InputObject> recording;
	private boolean enabled;
	
	public Keyboard(ArrayList<InputObject> recording) {
		this.recording = recording;
		this.enabled = true;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
		 
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (!enabled) {
			return;
		}
		
		this.recording.add(new InputObject(
			System.nanoTime(),
			(byte) 1,
			(byte) e.getKeyCode(),
			true
		));
		
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}
	
	// Handle key release events
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (!enabled) {
			return;
		}
		
		this.recording.add(new InputObject(
			System.nanoTime(),
			(byte) 1,
			(byte) e.getKeyCode(),
			false
		));
		
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}
}