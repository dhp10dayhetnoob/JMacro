package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import input.InputObject;
import input.Keyboard;
import input.Mouse;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

public class Recorder {
	private static final int TARGET_DELAY = 16; //target delay in milliseconds, runs at ca. 62.5 fps
	private double previousTime;
	private double runTime;
	
	private ArrayList<InputObject> recording;
	private boolean isRecording;
	
	private Mouse mouseListener;
	private Keyboard keyboardListener;

	public Recorder() {
		this.isRecording = false;
		this.recording = new ArrayList<InputObject>();
		
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
		
		this.mouseListener = new Mouse(this.recording);
		this.keyboardListener = new Keyboard(this.recording);
		
		GlobalScreen.addNativeKeyListener(this.keyboardListener);
		GlobalScreen.addNativeMouseListener(this.mouseListener);
		GlobalScreen.addNativeMouseMotionListener(this.mouseListener);
	}
	
	public static void main(String[] args) {
		new Recorder();
	}
	
	//once the recording is done, reverse the list so i dont need a queue that rearranges the other subsequent entries
	public void run() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		previousTime = System.nanoTime();
	
		//main input processing logic for playback
	    executor.scheduleAtFixedRate(() -> {
	    	double currentTime = System.nanoTime();
	    	//get accurate time between calls
	    	double deltaTime = (currentTime - previousTime) / 1_000_000_000d;
	    	//add to runTime for playback processing
	    	runTime += deltaTime;
	    	
	    	//processing for current frame
	    	update(deltaTime);
	    	
	    	previousTime = currentTime;
	    }, TARGET_DELAY, TARGET_DELAY, TimeUnit.MILLISECONDS);
	}

	private void update(double deltaTime) {
		//System.out.println("Updating with deltaTime: " + deltaTime);
	}
}