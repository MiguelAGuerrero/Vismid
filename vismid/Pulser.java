package vismid;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.sound.midi.ShortMessage;

/**
 * Pulser is a basic implementation of MidiResponder. 
 * Every time a Pulser is signaled, it enters an active state
 * for so many ticks. A tick represents a unit of drawing time, or frames.
 * In it's active state, the pulser increases its size using
 * an AffineTransform, and shrinks back to original scale.
 * @author Miguel Guerrero
 */
public class Pulser implements MidiResponder
{
	public int x;
	public int y;
	private boolean isActive;
	private int ticks = 0;
	private Shape baseRepr;
	private ArrayList<Shape> pulses = new ArrayList<>();
	Color color;
	private AffineTransform scale;
	
	public Pulser(int x, int y, int size, Color color)
	{
		this.x = x;
		this.y = y;
		this.isActive = false;
		this.baseRepr = new Ellipse2D.Double(x - size / 2, y - size / 2, size, size);
		this.color = color;
		
		//Pre-setup the scaling matrix for this pulser
		setupTransform();
	}
	
	/**
	 * Creates a scaling matrix that scales according to the amount
	 * of ticks this pulser has in its active state
	 * 
	 * Scaling occurs about the local coordinates of this pulser
	 */
	private void setupTransform()
	{
		//Get the center coordinates of this pulser
		double tx = baseRepr.getBounds2D().getCenterX();
		double ty = baseRepr.getBounds2D().getCenterY();
		
		//Concatenate the matrices necessary to scale about
		//one's center
		scale = AffineTransform.getTranslateInstance(tx, 
				ty);
		scale.scale(1 + ticks, 1 + ticks);
		scale.translate(-tx, -ty);
	}
	
	/**
	 * Ticks the pulser and draws the resulting state
	 */
	public void drawResponse(Graphics2D g2d)
	{
		//Decrement tick and deactivate pulser
		//if needed
		tick();
		g2d.setColor(this.color);
		
		//Draw the base representation of this pulser
		//if it is not active
		if(!isActive)
		{ 
			g2d.fill(baseRepr);
		}
		
		//Create the transform necessary to represent the 
		//state of this pulser. Each tick reflects a different
		//magnitude of change
		else
		{
			setupTransform();
			g2d.fill(scale.createTransformedShape(baseRepr));
		}
	}
	
	/**
	 * Ticks this Pulser, advancing its state
	 */
	private void tick()
	{
		if(isActive)
			if(--ticks <= 0) 
				isActive = false;
	}

	/**
	 * Activates this Pulser
	 */
	public void signalOn(ShortMessage sm)
	{
		this.isActive = true;
		this.ticks = 10;
	}

	/**
	 * Signals this pulser that an Off message
	 * was received. By default empty.
	 */
	public void signalOff(ShortMessage sm){}

	/**
	 * Mutates the current's base representation of 
	 * this pulser. Coordinates reflect the bounding box
	 * of the new shape.
	 */
	@Override
	public void transform(AffineTransform at)
	{
		this.baseRepr = at.createTransformedShape(baseRepr);
		
		//Reposition this pulser so that it reflects it transformed shape
		x = (int) baseRepr.getBounds2D().getCenterX();
		y = (int) baseRepr.getBounds2D().getCenterY();
	}
}
