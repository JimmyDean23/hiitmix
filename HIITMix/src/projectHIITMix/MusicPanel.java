package projectHIITMix;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.NativeLibrary;
import projectHIITMix.SavedSong.SavedClip;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

@SuppressWarnings({"serial", "unused"})
public class MusicPanel extends JPanel{
	private Control control;
	private JSlider timeline;
	private JButton playPause;
	private JButton addButton, deleteButton, editButton;
	private JLabel artistLabel, songLabel, tempoLabel, energyLabel, timeLabel, clipLabel;
	private JTextField interval, artist, song, tempo, time, clip, energy;
	private JPanel labels;
	private boolean playing = false;
	private SavedSong currentSong;
	private File currentSongPath;
	private MP3Player mp3Player;
	private Icon playIcon;
	private Icon pauseIcon;
	JComponent[] inputs;
	IntegerTextField intensity, duration;
	
	public MusicPanel(Control c){
		control = c;
		mp3Player = new MP3Player();
		setLayout(new GridBagLayout());
		ButtonListener listener = new ButtonListener();
		addButton = new JButton("+");
		addButton.addActionListener(listener);
		deleteButton = new JButton("-");
		deleteButton.addActionListener(listener);
		editButton = new JButton("Edit");
		editButton.addActionListener(listener);
		interval = new JTextField();
		interval.setPreferredSize(new Dimension(200, 20));
		interval.setEditable(false);
		timeline = new JSlider();
		timeline.addChangeListener(new SliderListener());
		timeline.setValue(0);
		
		playIcon = new ImageIcon("C://HIITMix//Resources//images//play.png");
		pauseIcon = new ImageIcon("C://HIITMix//Resources//images//pause.png");
		playPause = new JButton(playIcon);
		playPause.setPreferredSize(new Dimension(50,50));
		playPause.addActionListener(listener);
		
		labels = new JPanel(new GridLayout(2, 5));
		artistLabel = new JLabel("Artist");
		songLabel = new JLabel("Song");
		tempoLabel = new JLabel("Tempo");
		energyLabel = new JLabel("Energy");
		timeLabel = new JLabel("Length");
		clipLabel = new JLabel("Interval");
		JLabel[] tempLabels = {artistLabel, songLabel, tempoLabel, energyLabel, timeLabel, clipLabel};
		for(JLabel label : tempLabels){
			labels.add(label);
		}
		
		artist = new JTextField();
		song = new JTextField();
		tempo = new JTextField();
		energy = new JTextField();
		time = new JTextField();
		clip = new JTextField();
		JTextField[] tempFields = {artist, song, tempo, energy, time, clip};
		for(JTextField field : tempFields){
			field.setEditable(false);
			labels.add(field);
		}
		intensity = new IntegerTextField();
		intensity.setText("50");
		duration = new IntegerTextField();
		duration.setText("60");
		inputs = new JComponent[] {
                new JLabel("Intensity: (in heartrate percentage)"),
                intensity,
                new JLabel("Duration: (in seconds)"),
                duration
	    };
		
		placeComponents();
		validate();
		setVisible(true);
	}

	private void placeComponents() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;
		add(addButton);
		c.gridx = 1;
		add(deleteButton);
		c.gridx = 2;
		add(editButton);
		c.gridx = 3;
		c.weightx = .5;
		add(interval, c);
		
		c.gridx = 4;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.weightx = 2;
		add(timeline, c);

		c.gridy = 1;
		c.gridx = 0;
		c.gridheight = 2;
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.RELATIVE;
		add(labels, c);
		
		c.weightx = 0;
		c.gridheight = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 5;
		c.gridy = 0;
		add(playPause, c);
	}
	
	public JTextField getSectionTextField(){return interval;}
	public MP3Player getMP3Player(){return mp3Player;}
	
	public void setInfo(SavedClip audioClip, int num, Interval val){
	    if (audioClip != null){
	    	this.artist.setText(audioClip.getSong().getArtist());
	    	this.song.setText(audioClip.getSong().getTitle());
	    	this.tempo.setText(Double.toString((audioClip.getSong().getTempo())));
	    	time.setText(Double.toString(audioClip.getDuration()));
	    	clip.setText(audioClip.toInterval());
	    	energy.setText(Double.toString(audioClip.getEnergy()));
	    	currentSongPath = audioClip.getSong().getPath();
	    	mp3Player.getPlayer().getMediaPlayer().prepareMedia(currentSongPath.getAbsolutePath());
	    	
	    	// Sets the audio file to start at the given time. 
	    	int Time = (int) ((audioClip.getStart() / audioClip.getSong().getsongLength()) * 100);
	    	timeline.setValue(Time);
	    	setSliderBasedPosition();
	    	//was origionally just
	    	// timeline.setValue(0);
	    }
	    else{
	    	this.artist.setText("");
	    	this.song.setText("");
	    	this.clip.setText("");
	    }
	    if (val != null) {
	    	interval.setText("Section " + num + ": " + val.toString());
	    }
	}
	
	private void setSliderBasedPosition() {
        if(!mp3Player.getPlayer().getMediaPlayer().isSeekable()) {
        	return;
        }
        float positionValue = (float)timeline.getValue()/100.0f;
        // Avoid end of file freeze-up 
        if(positionValue > 0.99f) {
                     positionValue = 0.99f;
              }
        mp3Player.getPlayer().getMediaPlayer().setPosition(positionValue);
	}
	
	public void pauseSong(){
		playPause.setIcon(playIcon);
		mp3Player.pause();
		playing = false;
	}
	
	public void playSong(){
		playPause.setIcon(pauseIcon);
		mp3Player.play();
		setSliderBasedPosition();
		playing = true;
	}

	class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            setSliderBasedPosition();
	        }
		}
	}
	
	class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == playPause){
				if(currentSongPath == null){
					JOptionPane.showMessageDialog(null, "No song selected!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					if(playing){
						pauseSong();
					} else {
						playSong();
					}
				}
				
			} else if(control.getWorkouts().contains(control.getCurrentWorkout())){
				JOptionPane.showMessageDialog(null, "Can't edit preset workouts!", "Error", JOptionPane.ERROR_MESSAGE);
				
			} else if(control.getCurrentWorkout().getCurrentInterval() == null){
				JOptionPane.showMessageDialog(null, "No interval selected!", "Error", JOptionPane.ERROR_MESSAGE);
				
			} else if(e.getSource() == addButton){
				control.getCurrentWorkout().addInterval(new Interval(30, 70));
				
			} else if(e.getSource() == deleteButton){
				control.getCurrentWorkout().deleteInterval();
				
			} else if(e.getSource() == editButton){
				JOptionPane.showMessageDialog(null, inputs, "Edit Interval", JOptionPane.PLAIN_MESSAGE);
				control.getCurrentWorkout().editInterval(Integer.parseInt(duration.getText()), Integer.parseInt(intensity.getText()));
			}
		}
	}
	
	class IntegerTextField extends JTextField {
		final static String badchars = "-`~!@#$%^&*()_+=\\|\"':;?/>.<, ";
		
		public void processKeyEvent(KeyEvent ev) {
			char c = ev.getKeyChar();
			if((Character.isLetter(c) && !ev.isAltDown()) || badchars.indexOf(c) > -1) {
				ev.consume();
				return;
			}
			else {
				super.processKeyEvent(ev);
			}
		}
	}

	public void makeBlank() {
		interval.setText(null);
		setInfo(null, 0, null);
		tempo.setText(null);
		time.setText(null);
	}
}
