package vismid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JFrame;

/**
 * Main class to start up this MIDI graphics program.
 * @author Miguel Guerrero
 */

public class Main
{
	static Sequence sequence;
	static Sequencer sequencer;
	static Canvas canvas;
	static JFrame frame;
	private static Signaler signaler;
	
	public static void main (String[] args) throws InvalidMidiDataException, IOException, MidiUnavailableException
	{
		setupFrame();
		setupUIPanels();
		setupChannelResponders();
		start();
	}
	
	
	/**
	 * Sets up the frame for the graphical context of this application
	 */
	private static void setupFrame()
	{
		frame = new JFrame("Simple MIDI Graphics");
		frame.setLayout(new BorderLayout());	
	}
	
	/**
	 * Starts the application
	 */
	private static void start()
	{
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	/*
	 * Sets up the panels necessary to play and stop songs, as well as
	 * visualize MIDI files
	 */
	private static void setupUIPanels() throws MidiUnavailableException
	{
		canvas = new Canvas();
		signaler = new Signaler();
		PlayPanel playPanel = new PlayPanel(signaler);
		frame.add(canvas, BorderLayout.CENTER);
		frame.add(playPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Sets up 16 PulseGroups to reflect the typical
	 * 16 channels in a midi file (although, there can
	 * be more than 16 channels)
	 */
	private static void setupChannelResponders()
	{
		//Amount units per group
		int quant = 9;
		
		//The angular offset for creating the circle group
		double v = 2 * Math.PI / quant;
		
		//There are up to 16 channels typically for a midi
		//file
		//There will be 16 circles, each with the same amount of
		//units, but they will be space differently from the origin,
		//with different colors as well.
		//Also, they will be spinning in opposite directions initially
		//Start from 16 for drawing order, with the center being drawn last
		for(int i = 15; i >= 0 ; i--)
		{
			PulserGroup pg = new PulserGroup(
					quant, 
					10, 
					i * 400 / 16,
					Color.getHSBColor((float) (1 / (i * 2 * Math.PI / 15)), 1, 1),
					(int) (v * i),
					(i % 2 == 0 ? true : false));
			
			//Register this group to be drawn
			canvas.addMidiResponder(pg);
			
			//Register this group to recieve messages
			signaler.registerMidiResponder(pg, i);
		}
	}
}
