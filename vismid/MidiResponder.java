package vismid;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.sound.midi.ShortMessage;

/**
 * An interface that specifies how an object behaves in response to MIDI notes.
 * Implementors should react to notes being on and off, as well as being able to
 * draw their responses
 * 
 * Transformations are additional to creating dynamic changes to each
 * responder's graphical representation
 * @author Miguel Guerrero
 *
 */
public interface MidiResponder
{
	/**
	 * Signals that an ON message was received
	 * @param sm
	 */
	public void signalOn(ShortMessage sm);
	
	/**
	 * Signals that an OFF message was received
	 * @param sm
	 */
	public void signalOff(ShortMessage sm);
	
	/**
	 * Renders this MidiResponse onto a graphical context
	 * @param g2d
	 */
	public void drawResponse(Graphics2D g2d);
	
	/**
	 * Transform the geometric representation of this MidiResponse
	 * @param at
	 */
	public void transform(AffineTransform at);
}
