package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import input.InputObject;
import main.EventListener;
import main.Recorder;

public class MainFrame implements NativeKeyListener {
	public static final String TOPBAR_FORMAT = "<html>%s<br><h1 style=\"text-align:center\"><b>%s</b></h1></html>";
	
	private List<EventListener> listeners = new ArrayList<EventListener>();
	private boolean recording;
	private boolean playing;
	private boolean paused;
	
	boolean interrupt;
	Recorder parent;
	JLabel labelRecording;
	JLabel labelPlayback;
	JLabel labelPaused;
	JLabel labelOptions;
	JFrame frame;
	
    public MainFrame(Recorder parent) {
    	interrupt = false;
    	this.parent = parent;
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    public void setRecording(boolean enabled) {
    	recording = enabled;
    	
    	if (enabled) {
    		labelRecording.setForeground(Color.RED);
    	} else {
    		labelRecording.setForeground(Color.WHITE);
    	}

    	for (EventListener listener : listeners) {
            listener.onEventTriggered(1, recording);
        }
    }
    
    public void setPlaying(boolean enabled) {
    	playing = enabled;
    	
    	if (enabled) {
    		labelPlayback.setForeground(Color.RED);
    	} else {
    		labelPlayback.setForeground(Color.WHITE);
    	}
    	
    	for (EventListener listener : listeners) {
            listener.onEventTriggered(2, playing);
        }
    }
    
    public void setPaused(boolean enabled) {
    	paused = enabled;
    	
    	if (enabled) {
    		labelPaused.setForeground(Color.RED);
    	} else {
    		labelPaused.setForeground(Color.WHITE);
    	}
    	
    	for (EventListener listener : listeners) {
    		listener.onEventTriggered(3, paused);
    	}
    }
    
    public boolean isRecording() {
    	return recording;
    }
    
    public boolean isPlaying() {
    	return playing;
    }
    
    public boolean isPaused() {
    	return paused;
    }
    
    public void addListener(EventListener listener) {
    	listeners.add(listener);
    }

    private void createAndShowGUI() {
        // Create the JFrame (window) without borders
        frame = new JFrame("JMacro");
        frame.setUndecorated(Recorder.settings.getBoolean("StickToTop", true)); // Removes window borders
        frame.setAlwaysOnTop(Recorder.settings.getBoolean("AlwaysOnTop", true)); // Keep it always on top of other windows
        frame.setResizable(false); // Not resizable
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	System.exit(0);
            }
        });

        // Create the top bar panel with horizontal layout
        JPanel topBarPanel = new JPanel();
        topBarPanel.setLayout(new BoxLayout(topBarPanel, BoxLayout.X_AXIS)); // Horizontal BoxLayout
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding
        topBarPanel.setBackground(new Color(0, 0, 0, 255)); // Semi-transparent black background

        // Create labels (or buttons) for "A", "B", "C", "D"
        labelRecording = new JLabel(String.format(TOPBAR_FORMAT, "Record", NativeKeyEvent.getKeyText(Recorder.RECORD_HOTKEY)));
        labelPlayback = new JLabel(String.format(TOPBAR_FORMAT, "Play/Stop", NativeKeyEvent.getKeyText(Recorder.PLAYBACK_HOTKEY)));
        labelPaused = new JLabel(String.format(TOPBAR_FORMAT, "Pause", NativeKeyEvent.getKeyText(Recorder.PAUSE_HOTKEY)));
        labelOptions = new JLabel("Options"); // Change label to reflect that it's clickable
        JLabel btnExport = new JLabel("Export");
        JLabel btnImport = new JLabel("Import");
        
        // Customize label appearance
        labelRecording.setForeground(Color.WHITE);
        labelPlayback.setForeground(Color.WHITE);
        labelOptions.setForeground(Color.WHITE);
        labelPaused.setForeground(Color.WHITE);
        btnExport.setForeground(Color.WHITE);
        btnImport.setForeground(Color.WHITE);

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        labelRecording.setFont(labelFont);
        labelPlayback.setFont(labelFont);
        labelPaused.setFont(labelFont);
        labelOptions.setFont(labelFont);
        btnExport.setFont(labelFont);
        btnImport.setFont(labelFont);

        // Add elements to the panel with spacing
        topBarPanel.add(labelRecording);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(labelPlayback);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(labelPaused);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(labelOptions);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(btnExport);
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topBarPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing
        topBarPanel.add(btnImport);

        // Add the top bar to the frame
        frame.getContentPane().add(topBarPanel);
        frame.pack(); // Size the window to fit the content

        // Set the frame at the top-center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = frame.getWidth();
        int xPos = (screenSize.width / 2) - (frameWidth / 2); // Center horizontally
        frame.setLocation(xPos, 0); // Stick to the top of the screen
        
        // Add action listeners for Import/Export
        btnExport.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
        		Files.exportRecording(parent.getRecording());
            }
        });

     // Add action listeners for Import/Export
        btnImport.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
        		ArrayList<InputObject> importedRecording = Files.importRecording(frame);
        		if (importedRecording != null) {
        			parent.setRecording(importedRecording);
        		}
            }
        });

        new Options(this);
        
        // Make the frame visible
        frame.setVisible(true);
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    	if (interrupt == true) {
    		return;
    	}
    	
    	if (e.getKeyCode() == Recorder.RECORD_HOTKEY) {
    		setRecording(!recording);
    	} else if (e.getKeyCode() == Recorder.PLAYBACK_HOTKEY) {
    		setPlaying(!playing);
    	} else if (e.getKeyCode() == Recorder.PAUSE_HOTKEY) {
    		setPaused(!paused);
    	}
    }
}