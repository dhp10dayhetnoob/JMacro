package main;

import input.InputObject;
import gui.MainFrame;
import input.Keyboard;
import input.Mouse;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class Recorder implements EventListener {
	private static final double EPSILON = 0.005;
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
	public static int RECORD_HOTKEY = NativeKeyEvent.VC_F1;
	public static int PLAYBACK_HOTKEY = NativeKeyEvent.VC_F2;
	private static final int TARGET_DELAY = 16; //target delay in milliseconds, runs at ca. 62.5 fps
	
	private double recordingStartTime;
	private double previousTime;
	private double runTime;
	
	private ArrayList<InputObject> loggedRecording;
	private int iterator;
	
	private Mouse mouseListener;
	private Keyboard keyboardListener;
	private MainFrame gui;
	
	private ScheduledFuture<?> scheduledFuture;
	
	private boolean continousPlayback;
	
	public static void main(String[] args) {
		new Recorder();
	}
	
	public ArrayList<InputObject> getRecording() {
		return loggedRecording;
	}
	
	public void setRecording(ArrayList<InputObject> recording) {
		this.loggedRecording = recording;
	}
	
	public void setContinousPlayback(boolean enabled) {
		continousPlayback = enabled;
	};
	
	public Recorder() {
		loggedRecording = new ArrayList<InputObject>();
		continousPlayback = false;
		
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
		
		//create listener objects
		gui = new MainFrame(this);
		mouseListener = new Mouse(loggedRecording);
		keyboardListener = new Keyboard(loggedRecording);
		
		//add listeners
		gui.addListener(this);
		gui.addListener(keyboardListener);
		gui.addListener(mouseListener);
		
		GlobalScreen.addNativeKeyListener(keyboardListener);
		GlobalScreen.addNativeKeyListener(gui);
		GlobalScreen.addNativeMouseListener(mouseListener);
		GlobalScreen.addNativeMouseMotionListener(mouseListener);
		
		//unregister native hook on close
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}
	
	private void run() {
		previousTime = System.nanoTime();
	
		//main input processing logic for playback
		runTime = 0;
	    scheduledFuture = executor.scheduleAtFixedRate(() -> {
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
		//get current input at index iterator
		InputObject currentInput = loggedRecording.get(iterator);
		//subtract epsilon for broader processing window
		while (currentInput.getTimeStamp() - EPSILON <= runTime) {
			System.out.println("processed input at " + runTime + " index " + iterator + "/" + (loggedRecording.size() - 1));
			InputSimulator.simulate(currentInput);
			
			iterator++;
			if (iterator > loggedRecording.size() - 1) {
				if (continousPlayback == true) {
					//account for the difference in the next playback
					runTime = runTime - loggedRecording.get(loggedRecording.size() - 1).getTimeStamp();
					iterator = 0;
				} else {
					gui.setPlaying(false, true);
					break;
				}
			}
			
			currentInput = loggedRecording.get(iterator);
		}
	}
	
	@Override //type 1 = recording, type 2 = playback
	public void onEventTriggered(int type, boolean enabled, boolean authorative) {
		if (type == 1) {
			if (gui.isPlaying()) {
				if (!authorative) {
					gui.setRecording(false, true);
				}
				return;
			}
			
			if (enabled == true) {
				loggedRecording = new ArrayList<InputObject>();
				keyboardListener.overWriteRecording(loggedRecording);
				mouseListener.overWriteRecording(loggedRecording);
				recordingStartTime = System.nanoTime();
				System.out.println("new recording list");
			} else {
				if (scheduledFuture != null) {
					scheduledFuture.cancel(true);
					System.out.println("cancel running future (recording)");
				}
				
				double elapsedTime = (System.nanoTime() - recordingStartTime) / 1_000_000_000d;
				loggedRecording.add(new InputObject(elapsedTime, (byte) 4));
				
				System.out.println("ended recording");
				System.out.println("recording duration: " + elapsedTime);
			}
		} else {
			if (enabled == true) {
				if (gui.isRecording()) {
					if (!authorative) {
						gui.setRecording(false, true);
					}
					System.out.println("cancelled recording");
				}
				
				System.out.println("playback started");
				if (loggedRecording.isEmpty()) {
					if (!authorative) {
						gui.setPlaying(false, true);
					}
					System.out.println("playback stopped (empty recording)");
					return;
				}
				
				iterator = 0;
				run();
			} else {
				if (scheduledFuture != null) {
					scheduledFuture.cancel(true);
					System.out.println("cancel running future (playback)");
				}
			}
		}
	} 
}
