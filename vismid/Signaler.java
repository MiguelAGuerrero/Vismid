package vismid;

import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * The Signaler is the class that is able to receive input from a Midi
 * Transmitter. Every time a MIDI message is received, it is processed
 * for whether it is a short message (effectively, song content) and
 * signals any registered MidiResponder objects.
 * 
 * Registered MidiResponder objects are organized according to channel
 * number.
 * @author Miguel Guerrero
 */
public class Signaler implements Receiver
{
	private HashMap<Integer, LinkedList<MidiResponder>> observers;
	public Signaler()
	{
		observers = new HashMap<>();
	}

	/**
	 * Maps a MidiResponder to a specific channel for this Signaler.
	 * @param m midi responder
	 * @param channel channel number
	 */
	public void registerMidiResponder(MidiResponder m, int channel)
	{
		if(!channelSet(channel))
			setupNewChannel(channel);
		observers.get(channel).add(m);
	}
	
	/**
	 * Determines whether a channel number has been
	 * set a key in the hashmap
	 */
	private boolean channelSet(int channel)
	{
		return this.observers.containsKey(channel);
	}
	
	/**
	 * Sets up a new LinkedList for a particular channel
	 */
	private void setupNewChannel(int channel)
	{
		this.observers.put(channel, new LinkedList<>());
	}
	
	/**
	 * Empty method to satisfy interface Reciever interface
	 */
	@Override
	public void close(){}

	/**
	 * Receives a MidiMessage from the Sequencer's transmitter.
	 * These messages are then processed to determine whether
	 * responders to off-on notes should be notified.
	 */
	@Override
	public void send(MidiMessage message, long timeStamp)
	{
		//First check if the message is a ShortMessage. A 
		//ShortMessage represents the content of songs
	    if(message instanceof ShortMessage)
	    {
	    	//Cast to access specific methods of the ShortMessage
	        ShortMessage sm = (ShortMessage) message;
	        
	        //Get the channel that produced this message
	        int channel = sm.getChannel();
	        
	        //If this channel is not a key in the observer
	        //map, then there are no responses mapped to
	        //the channel and the message is meaningless
	        if(!this.observers.containsKey(channel)) 
	        	return;
	        
	        //These attributes are necessary to determine 
	        //whether a signal if off or on
            int velocity = sm.getData2();
            int com = sm.getCommand();
            
            //MIDI specifications state that a message be on,
            //but still be classified as off if  velocity 
            //is 0
            if(com == ShortMessage.NOTE_ON && velocity != 0)
            {
            	//Signal each responder for the message's channel
            	//a note on message
            	for(MidiResponder mr: observers.get(channel))
            	{
            		mr.signalOn(sm);
            	}
            }
            
            //Check if the message is note-off
	        else if (com == ShortMessage.NOTE_OFF 
	        		|| 
	        		(com == ShortMessage.NOTE_ON && velocity == 0))
	        {
	        	
	        	//Signal each responder for this channel that the
	        	//message is off
	          	for(MidiResponder mr: observers.get(channel))
            	{
            		mr.signalOff(sm);
            	}
	        } 
            
            //Do nothing otherwise
	    }
	}
}
