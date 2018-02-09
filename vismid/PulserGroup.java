package vismid;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

import javax.sound.midi.ShortMessage;

/**
 * PularGroup represents a collection of Pulser objects as a single unit.
 * This is essentially a way of unifying a set of Pulser objects to represent
 * a response to a channel.
 * 
 * For aesthetic, a pulser group is organized in a circle, and every
 * pulser in the group shares the same attributes of size and color
 * @author Miguel Guerrero
 */
public class PulserGroup implements MidiResponder
{
	private LinkedList<Pulser> group;
	
	private AffineTransform rot;
	private int dir;
	private int frames;
	/**
	 * Constructs a PulserGroup specified by the amount of pulsers 
	 * in the group, the size of each pulser, the 
	 * @param num numbers of pulser objects
	 * @param unitSize size of each pulser in pixels
	 * @param diameter size of the circle
	 * @param color color of each pulser
	 * @param offset offset angle of the circle
	 * @param reverse direction group's rotation
	 */
	public PulserGroup(int num, int unitSize, int diameter, Color color, double offset, boolean reverse)
	{
		//Represent the group as a linked list
		group = new LinkedList<>();
		

		//Get the interval to evenly spread the number 
		//of pulsers in the group over a circle 
		double a = 2 * Math.PI / num;
		for(int i = 0; i < num; i++)
		{
			//Instantiate each pulser in sequence and
			//as having a position on a circle, offset by
			//some angrad
			int x = (int) (diameter * Math.cos(a * i + offset));
			int y = (int) (diameter * Math.sin(a * i + offset));
			group.add(new Pulser(x, y, unitSize, color));
		}
		
		//Sets up the rotation matrix to rotate each pulser about
		//the center
		dir = reverse ? -1 : 1;
		rot = AffineTransform.getRotateInstance(dir * 0.0025);
	}
	
	/**
	 * Signals every pulser in the group that a note has been
	 * received.
	 */
	@Override
	public void signalOn(ShortMessage sm)
	{	
		//Propagate the message to the entire group
		for(MidiResponder m: group) 
			m.signalOn(sm);
	}

	/**
	 * Cascades a signal to every pulser in the group
	 * with a message that a Off message has been received.
	 * 
	 * For this group, the rate of rotation is perodic, and every signal
	 * off results in a faster -> slower -> faster rotation
	 */
	@Override
	public void signalOff(ShortMessage sm)
	{
		//Accelerate rotation of 
		frames++;
		rot = AffineTransform.getRotateInstance(0.05 * Math.cos(
				Math.toRadians(dir * 3 * frames % 360)));
		//Send the short message to each member of the group
		for(MidiResponder m: group) 
			m.signalOff(sm);
	}

	/**
	 * Draws the entire pulser group, with links
	 * between them to demonstrate unity
	 */
	@Override
	public void drawResponse(Graphics2D g2d)
	{
		int size = group.size();
		for(int i = 0; i <= group.size(); i++)
		{
			//Apply a modulo so that the first and last
			//pulsers in the group can link to each other with
			//having to account for the edge case outside of the
			//loop
			Pulser a = group.get(i % size);
			Pulser b = group.get((i + 2) % size);
			
			g2d.setPaint(new GradientPaint(0, 0, a.color, 0, 100, Color.WHITE));
			
			//Draw a line between two neighboring pulsers
			g2d.drawLine(
					(int) a.x, (int) a.y, 
					(int) b.x, (int) b.y);
		}

		//Draw every pulser in the group
		for(Pulser pulsar: group) 
			pulsar.drawResponse(g2d);
		
		//Apply a subtle rotation transformation on this
		//entire group about the center
		//Pulser will look like they are traveling in a circle
		transform(this.rot);
	}
	
	/**
	 * Transform the entire group using the provided
	 * AffineTransform
	 */
	public void transform(AffineTransform at)
	{
		//Pass the transformation to each member of 
		//the group
		for(Pulser pulser: group)
			pulser.transform(at);
	}
}
