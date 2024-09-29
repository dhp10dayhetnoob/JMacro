package main;

// Define the listener interface
public interface EventListener {
    void onEventTriggered(int type, boolean enabled);
}