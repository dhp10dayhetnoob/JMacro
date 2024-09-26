package main;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MouseEventConverter {

    // Mapping from NativeKeyEvent key codes to KeyEvent key codes
    private static final Map<Integer, Integer> keyMap = new HashMap<>();

    static {
        // Letters
        keyMap.put(NativeMouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK);
        keyMap.put(NativeMouseEvent.BUTTON2, InputEvent.BUTTON2_DOWN_MASK);
        keyMap.put(NativeMouseEvent.BUTTON3, InputEvent.BUTTON3_DOWN_MASK);
        // Other common keys can be added here...
    }

    /**
     * Converts a NativeKeyEvent key code to a KeyEvent key code.
     * @param nativeKeyCode The NativeKeyEvent key code.
     * @return The corresponding KeyEvent key code, or -1 if no mapping exists.
     */
    public static int convertNativeToMouseEvent(int nativeMouseCode) {
        return keyMap.getOrDefault(nativeMouseCode, -1); // Return -1 if not found
    }
}