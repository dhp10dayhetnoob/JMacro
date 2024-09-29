package gui;

import java.util.HashMap;
import java.util.Map;

public class MouseButtonTranslator {
    private static final Map<Integer, Integer> textToKeyCodeMap = new HashMap<>();

    static {
    	textToKeyCodeMap.put(1024, 1);
    	textToKeyCodeMap.put(2048, 3);
    	textToKeyCodeMap.put(4096, 2);
    	
    	textToKeyCodeMap.put(1, 1024);
    	textToKeyCodeMap.put(3, 2048);
    	textToKeyCodeMap.put(2, 4096);
    }

    // Method to get the key code from the text representation
    public static int getKeyCodeFromText(int key) {
        return textToKeyCodeMap.getOrDefault(key, -1);  // Return -1 if not found
    }
}