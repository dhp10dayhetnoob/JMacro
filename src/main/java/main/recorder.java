package main;

public class recorder {
	private static final int TARGET_FPS = 60; // Target frames per second
	private static final double TIME_STEP = 1d / TARGET_FPS; // Time per frame in seconds
	private boolean running;

	public static void main(String[] args) {
		new recorder().run();
	}
	
	public recorder() {
		running = true;
	}

	public void run() {
	    double previousTime = System.nanoTime() / 1_000_000_000d;
	    double accumulatedTime = 0;

	    while (this.running) { // Main loop
	        double currentTime = System.nanoTime() / 1_000_000_000d;
	        double elapsedTime = currentTime - previousTime;
	        previousTime = currentTime;
	        accumulatedTime += elapsedTime;

	        // Process input or other non-time-dependent tasks here
	        while (accumulatedTime >= TIME_STEP) {
	        	update(accumulatedTime); // Update logic with a fixed time step
	            accumulatedTime -= TIME_STEP;
	        }
	    }
	}

	private void update(double deltaTime) {
    	// Update your game logic here
    	System.out.println("Updating with deltaTime: " + deltaTime);
    }
}