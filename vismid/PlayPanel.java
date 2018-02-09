package vismid;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.io.File;
import java.awt.GridLayout;

@SuppressWarnings("serial")
/**
 * PlayPanel provides a MIDI playing interface for a User.
 * It is constructed with a Signaler so that registered
 * MidiResponses are mapped to the same Midi Transmitter.
 * Otherwise, a MIDI might play but observers of the the
 * Signaler will not receive a response.
 * @author Miguel Guerrero
 *
 */
public class PlayPanel extends JPanel
{
	private JTextField fieldMessage;
	private JTextField fieldSelected;
	private JFileChooser fileChooserDialog = new JFileChooser();
	private File selectedMidiFile = null;
	private Sequencer sequencer;
	
	/**
	 * Creates the panel.
	 * @throws MidiUnavailableException 
	 */
	public PlayPanel(Signaler signaler) throws MidiUnavailableException
	{
		setLayout(new GridLayout(3, 1, 0, 0));
		setupFindPanel();
		setupPlayPanel();
		setupMessageField();
		fileChooserDialog.setFileFilter(new FileNameExtensionFilter("MIDI", "mid"));
		sequencer = MidiSystem.getSequencer();
		sequencer.getTransmitter().setReceiver(signaler);
		sequencer.open();
	}
	
	/**
	 * Sets a message field. Status message are displayed
	 * via the field box
	 */
	private void setupMessageField()
	{
		fieldMessage = new JTextField();
		fieldMessage.setBackground(Color.LIGHT_GRAY);
		fieldMessage.setEditable(false);
		add(fieldMessage);
		fieldMessage.setColumns(10);
	}
	
	/**
	 * Sets up the panel necessary to  play and stop
	 * MIDI files
	 */
	private void setupPlayPanel()
	{
		JPanel panelPlay = new JPanel();
		add(panelPlay);
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener((ae) -> 
		{
			try
			{
				if(selectedMidiFile == null)
				{
					fieldMessage.setText("No MIDI selected");
					return;
				}
				Sequence sequence = MidiSystem.getSequence(selectedMidiFile);
				sequencer.setSequence(sequence);
				sequencer.start();
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
		});
		
		panelPlay.add(btnPlay);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener((ae) -> sequencer.stop()); 
		panelPlay.add(btnStop);
	}

	/**
	 * Sets up the panel needed to find a MIDI
	 * file on disk
	 */
	private void setupFindPanel()
	{
		JPanel panelFind = new JPanel();
		add(panelFind);
		
		JButton btnFileFind = new JButton("Find MIDI");
		btnFileFind.addActionListener((ae) -> 
		{
			fileChooserDialog.showOpenDialog(this);
			selectedMidiFile = fileChooserDialog.getSelectedFile();
			if(selectedMidiFile == null)
			{
				fieldMessage.setText("File selection cancelled");
				return;
			}
			fieldSelected.setText(selectedMidiFile.getPath());
			fieldMessage.setText("Selected MIDI: " + selectedMidiFile.getName());
		});
		
		panelFind.add(btnFileFind);
		
		fieldSelected = new JTextField();
		panelFind.add(fieldSelected);
		fieldSelected.setColumns(30);	
	}
}
