package input;

public class InputObject {
	private double timeStamp;
	private byte inputType; //1 = keyboard, 2 = mouse input, 3 = mouse move, 4 delay
	
	//1 byte for key/button input, a boolean for up/down state
    private byte keyOrButton;
    private boolean upOrDown;

    //coordinates for mouse movement
    private int mouseX;
    private int mouseY;
    
    //object in case of key/button input
    public InputObject(double timeStamp, byte inputType, byte keyOrButton, boolean upOrDown) {
    	this.timeStamp = timeStamp;
    	this.inputType = inputType;
    	this.keyOrButton = keyOrButton;
    	this.upOrDown = upOrDown;
    	System.out.println("registered input at " + timeStamp);
    }
    
    //object in case of mouse movement
    public InputObject(double timeStamp, byte inputType, int mouseX, int mouseY) {
    	this.timeStamp = timeStamp;
    	this.inputType = inputType;
    	this.mouseX = mouseX;
    	this.mouseY = mouseY;
    	System.out.println("registered mouse movement at " + timeStamp);
    }
    
    public InputObject(double timeStamp, byte inputType) {
    	this.timeStamp = timeStamp;
    	this.inputType = inputType;
    	System.out.println("registered delay at " + timeStamp);
    }
    
    //getters
    public double getTimeStamp() {
    	return timeStamp;
    }

    public byte getInputType() {
        return inputType;
    }

    public int getKeyOrButton() {
        if (inputType == 1 || inputType == 2) {
            return keyOrButton;
        }
        throw new UnsupportedOperationException("This is not a key/button input.");
    }
    
    public boolean getIsUpOrDown() {
        if (inputType == 1 || inputType == 2) {
            return upOrDown;
        }
        throw new UnsupportedOperationException("This is not a key/button input.");
    }

    public int getMouseX() {
        if (inputType == 3) {
            return mouseX;
        }
        throw new UnsupportedOperationException("This is not a mouse movement input.");
    }

    public int getMouseY() {
        if (inputType == 3) {
            return mouseY;
        }
        throw new UnsupportedOperationException("This is not a mouse movement input.");
    }
}
