package vismid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
/**
 * Canvas is the graphical display for this MIDI visualizer application.
 * By registering responders to this object, it is able to invoke the 
 * appropriate draw methods on each of them to produce the visualization
 * @author Miguel Guerrero
 */
public class Canvas extends JPanel
{
	private ArrayList<MidiResponder> responders;
	public Canvas()
	{
		Dimension size = new Dimension(400, 400);
		this.setSize(size);
		this.setPreferredSize(size);
		responders = new ArrayList<>();
		startTimer();
	}
	
	/**
	 * Starts the timer that repaints the frame every
	 * so often. Needed so that the Signaler
	 * can 
	 */
	private void startTimer()
	{
		Timer t = new Timer(20, null);
		t.addActionListener((ae) -> Main.frame.repaint());
		t.start();
	}
	
	/**
	 * Sets up the coordinate system, draws the background, and passes
	 * around the graphical context to each of the registered MIDI responders
	 */
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		drawBackground(g2d);
		setCartesianCoordinateScheme(g2d);
		
		//Draw each response
		for(MidiResponder m: this.responders) 
			m.drawResponse(g2d);
	}
	
	/**
	 * Draws the background
	 * @param g2d
	 */
	private void drawBackground(Graphics2D g2d)
	{
		g2d.setBackground(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	/**
	 * Sets the coordinate system of the graphical context to be
	 * more like a cartesian coordinate system where the origin 
	 * is the center
	 * @param g2d
	 */
	private void setCartesianCoordinateScheme(Graphics2D g2d)
	{
		g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
		g2d.scale(1, -1);
	}
	
	/**
	 * Adds a response to be drawn by this canvas
	 * @param m
	 */
	public void addMidiResponder(MidiResponder m)
	{
		this.responders.add(m);
	}
	
	/**
	 * Remove a response from the canvas. Removed
	 * responses are no longer rendered on screen
	 * @param m
	 */
	public void removeMidiResponder(MidiResponder m)
	{
		this.responders.remove(m);
	}
}
