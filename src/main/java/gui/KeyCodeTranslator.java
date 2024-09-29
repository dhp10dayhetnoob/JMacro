package gui;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyCodeTranslator {
    private static final Map<String, Integer> textToKeyCodeMap = new HashMap<>();

    static {
        // Populate the map with the key texts and their respective key codes
        for (int keyCode = 1; keyCode <= 255; keyCode++) {
            String keyText = KeyEvent.getKeyText(keyCode);
            textToKeyCodeMap.put(keyText, keyCode);
        }
    }

    // Method to get the key code from the text representation
    public static int getKeyCodeFromText(String keyText) {
        return textToKeyCodeMap.getOrDefault(keyText, -1);  // Return -1 if not found
    }
}