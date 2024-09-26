package input;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class KeyEventConverter {

    // Mapping from NativeKeyEvent key codes to KeyEvent key codes
    private static final Map<Integer, Integer> keyMap = new HashMap<>();

    static {
        // Letters
        keyMap.put(NativeKeyEvent.VC_A, KeyEvent.VK_A);
        keyMap.put(NativeKeyEvent.VC_B, KeyEvent.VK_B);
        keyMap.put(NativeKeyEvent.VC_C, KeyEvent.VK_C);
        keyMap.put(NativeKeyEvent.VC_D, KeyEvent.VK_D);
        keyMap.put(NativeKeyEvent.VC_E, KeyEvent.VK_E);
        keyMap.put(NativeKeyEvent.VC_F, KeyEvent.VK_F);
        keyMap.put(NativeKeyEvent.VC_G, KeyEvent.VK_G);
        keyMap.put(NativeKeyEvent.VC_H, KeyEvent.VK_H);
        keyMap.put(NativeKeyEvent.VC_I, KeyEvent.VK_I);
        keyMap.put(NativeKeyEvent.VC_J, KeyEvent.VK_J);
        keyMap.put(NativeKeyEvent.VC_K, KeyEvent.VK_K);
        keyMap.put(NativeKeyEvent.VC_L, KeyEvent.VK_L);
        keyMap.put(NativeKeyEvent.VC_M, KeyEvent.VK_M);
        keyMap.put(NativeKeyEvent.VC_N, KeyEvent.VK_N);
        keyMap.put(NativeKeyEvent.VC_O, KeyEvent.VK_O);
        keyMap.put(NativeKeyEvent.VC_P, KeyEvent.VK_P);
        keyMap.put(NativeKeyEvent.VC_Q, KeyEvent.VK_Q);
        keyMap.put(NativeKeyEvent.VC_R, KeyEvent.VK_R);
        keyMap.put(NativeKeyEvent.VC_S, KeyEvent.VK_S);
        keyMap.put(NativeKeyEvent.VC_T, KeyEvent.VK_T);
        keyMap.put(NativeKeyEvent.VC_U, KeyEvent.VK_U);
        keyMap.put(NativeKeyEvent.VC_V, KeyEvent.VK_V);
        keyMap.put(NativeKeyEvent.VC_W, KeyEvent.VK_W);
        keyMap.put(NativeKeyEvent.VC_X, KeyEvent.VK_X);
        keyMap.put(NativeKeyEvent.VC_Y, KeyEvent.VK_Y);
        keyMap.put(NativeKeyEvent.VC_Z, KeyEvent.VK_Z);

        // Numbers
        keyMap.put(NativeKeyEvent.VC_0, KeyEvent.VK_0);
        keyMap.put(NativeKeyEvent.VC_1, KeyEvent.VK_1);
        keyMap.put(NativeKeyEvent.VC_2, KeyEvent.VK_2);
        keyMap.put(NativeKeyEvent.VC_3, KeyEvent.VK_3);
        keyMap.put(NativeKeyEvent.VC_4, KeyEvent.VK_4);
        keyMap.put(NativeKeyEvent.VC_5, KeyEvent.VK_5);
        keyMap.put(NativeKeyEvent.VC_6, KeyEvent.VK_6);
        keyMap.put(NativeKeyEvent.VC_7, KeyEvent.VK_7);
        keyMap.put(NativeKeyEvent.VC_8, KeyEvent.VK_8);
        keyMap.put(NativeKeyEvent.VC_9, KeyEvent.VK_9);

        // Function keys
        keyMap.put(NativeKeyEvent.VC_F1, KeyEvent.VK_F1);
        keyMap.put(NativeKeyEvent.VC_F2, KeyEvent.VK_F2);
        keyMap.put(NativeKeyEvent.VC_F3, KeyEvent.VK_F3);
        keyMap.put(NativeKeyEvent.VC_F4, KeyEvent.VK_F4);
        keyMap.put(NativeKeyEvent.VC_F5, KeyEvent.VK_F5);
        keyMap.put(NativeKeyEvent.VC_F6, KeyEvent.VK_F6);
        keyMap.put(NativeKeyEvent.VC_F7, KeyEvent.VK_F7);
        keyMap.put(NativeKeyEvent.VC_F8, KeyEvent.VK_F8);
        keyMap.put(NativeKeyEvent.VC_F9, KeyEvent.VK_F9);
        keyMap.put(NativeKeyEvent.VC_F10, KeyEvent.VK_F10);
        keyMap.put(NativeKeyEvent.VC_F11, KeyEvent.VK_F11);
        keyMap.put(NativeKeyEvent.VC_F12, KeyEvent.VK_F12);

        // Special keys
        keyMap.put(NativeKeyEvent.VC_ENTER, KeyEvent.VK_ENTER);
        keyMap.put(NativeKeyEvent.VC_ESCAPE, KeyEvent.VK_ESCAPE);
        keyMap.put(NativeKeyEvent.VC_TAB, KeyEvent.VK_TAB);
        keyMap.put(NativeKeyEvent.VC_SPACE, KeyEvent.VK_SPACE);
        keyMap.put(NativeKeyEvent.VC_BACKSPACE, KeyEvent.VK_BACK_SPACE);
        keyMap.put(NativeKeyEvent.VC_DELETE, KeyEvent.VK_DELETE);
        keyMap.put(NativeKeyEvent.VC_HOME, KeyEvent.VK_HOME);
        keyMap.put(NativeKeyEvent.VC_END, KeyEvent.VK_END);
        keyMap.put(NativeKeyEvent.VC_PAGE_UP, KeyEvent.VK_PAGE_UP);
        keyMap.put(NativeKeyEvent.VC_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        keyMap.put(NativeKeyEvent.VC_LEFT, KeyEvent.VK_LEFT);
        keyMap.put(NativeKeyEvent.VC_RIGHT, KeyEvent.VK_RIGHT);
        keyMap.put(NativeKeyEvent.VC_UP, KeyEvent.VK_UP);
        keyMap.put(NativeKeyEvent.VC_DOWN, KeyEvent.VK_DOWN);

        // Modifier keys
        keyMap.put(NativeKeyEvent.VC_SHIFT, KeyEvent.VK_SHIFT);
        keyMap.put(NativeKeyEvent.VC_CONTROL, KeyEvent.VK_CONTROL);
        keyMap.put(NativeKeyEvent.VC_META, KeyEvent.VK_META); // Command key on Mac
        keyMap.put(NativeKeyEvent.VC_ALT, KeyEvent.VK_ALT);

        // Other common keys can be added here...
    }

    /**
     * Converts a NativeKeyEvent key code to a KeyEvent key code.
     * @param nativeKeyCode The NativeKeyEvent key code.
     * @return The corresponding KeyEvent key code, or -1 if no mapping exists.
     */
    public static int convertNativeToKeyEvent(int nativeKeyCode) {
        return keyMap.getOrDefault(nativeKeyCode, -1); // Return -1 if not found
    }
    
    public static Integer getNativeByKeyEvent(Integer value) {
        for (Entry<Integer, Integer> entry : keyMap.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }
}