package input;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class MouseEventConverter {

    // Mapping from NativeKeyEvent key codes to KeyEvent key codes
    private static final Map<Integer, Integer> mouseMap = new HashMap<>();

    static {
        // Letters
    	mouseMap.put(NativeMouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK);
    	mouseMap.put(NativeMouseEvent.BUTTON2, InputEvent.BUTTON2_DOWN_MASK);
    	mouseMap.put(NativeMouseEvent.BUTTON3, InputEvent.BUTTON3_DOWN_MASK);
        // Other common keys can be added here...
    }

    /**
     * Converts a NativeKeyEvent key code to a KeyEvent key code.
     * @param nativeKeyCode The NativeKeyEvent key code.
     * @return The corresponding KeyEvent key code, or -1 if no mapping exists.
     */
    public static int convertNativeToMouseEvent(int nativeMouseCode) {
        return mouseMap.getOrDefault(nativeMouseCode, -1); // Return -1 if not found
    }
    
    public static Integer getNativeByMouseEvent(Integer value) {
        for (Entry<Integer, Integer> entry : mouseMap.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }
}