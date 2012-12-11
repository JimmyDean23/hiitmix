package projectHIITMix;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.TimedEvent;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.TrackAnalysis;

@SuppressWarnings("unused")
public class SavedSong {
	private ArrayList<SavedClip> chunks;
	private String title;
	private String artist;
	private double tempo, songLength, energy;
	private File path;
    
	/*
	 * Use this contructor when reading tracks from echonest
	 */
	
    public SavedSong(Track track,TrackAnalysis trackInfo, Song songData, File path) throws EchoNestException {
		super();
			energy = songData.getEnergy();
			songLength = trackInfo.getDuration();
			title = track.getTitle();
			artist = track.getArtistName();
			tempo = track.getTempo();
			this.path = path;
			chunks = new ArrayList<SavedClip>();
			for (TimedEvent event: trackInfo.getSections()){
				this.chunks.add(new SavedClip(event.getDuration(), event.getStart(), this, tempo));
			}
    }
    
    /*
     * Use this constructor when reading from a save.
     */
    public SavedSong(String save) {
    	// split the string into chunks so that it can be read/saved to the class properly
    	chunks =new ArrayList<SavedClip>();
    	String[] values = save.split("\\|");
    	title = values[0];
    	path = new File(values[1]);
    	if (!values[2].equals("?"))
    		tempo = Double.parseDouble(values[2]);
   	 	else
			tempo = 0;
    	if (!values[3].equals("?"))
			songLength = Double.parseDouble(values[3]);
	 	else
	 	   	songLength = 0;
    	artist = values[4];
    	energy = Double.parseDouble(values[5]);
    	String[] sections = values[6].split("/");
    	for (String section: sections){
    		String[] sec = section.split(",");
    		chunks.add(new SavedClip(Double.parseDouble(sec[0]),
			Double.parseDouble(sec[1]), this, tempo));
    	}
    }


	/*
     * General getters and setters
     * plus a to string and to output for saving
     */
    public String getTitle() {return title;}
    public double getTempo() {return tempo;}
    public File getPath() {return path;}
    public String getArtist() {return artist;}
    public double getEnergy() {return energy;}
    public double getsongLength(){return songLength;}
    public ArrayList<SavedClip> getChunks(){return chunks;}
    public boolean equals(SavedSong other){ return path.equals(other.getPath());}
	public String toString(){
    	String message = "";
		message += title + " " + tempo;
    	return message;
    }
    
	public String output(){
	    DecimalFormat df = new DecimalFormat("#.#");
	    String output = "";
	    output += title + "|";
	    output += path + "|";
	    output += df.format(tempo) + "|";
	    output += df.format(songLength) + "|";
	    output += artist + "|";
	    output += df.format(energy) + "|";
	    for (SavedClip event: chunks){
	    	output += df.format(event.getDuration()) + "," + df.format(event.getStart()) + "/";
	    }		
	    return output;
	}
	
	/*
	 * Searching through the available chunks and returning the one what matches the time the closest
	 * without going under. if the song doesn't have long enough clips make one. 
	 */
	public ArrayList<SavedClip> findChunk(double time) throws EchoNestException{
    	ArrayList<SavedClip> matches = new ArrayList<SavedClip>();
    	double songLength = 0;
		for (SavedClip clip: chunks){
			if (clip.getDuration() > time){
				matches.add(clip);
			}else{
				songLength += clip.getDuration();
			}
		}
		if (matches.isEmpty()){// && (songLength > time)){
			matches.add(new SavedClip (0, time, this, tempo));
		}
		return matches;
    }
    
    public class SavedClip{
		private double start, duration;
		private double tempo, energy;
		private SavedSong song;
		public SavedClip(double start, double duration, SavedSong song, double tempo) {
		    super();
		    this.start = start;
		    this.duration = duration;
		    this.song = song;
		    this.tempo = tempo;
		    this.energy = song.getEnergy();
		    //this.loudness = loudness;
		}
		public SavedClip(){
		    start = 0;
		    duration = 0;
		    song = null;
		    tempo = 0;
		}
		
		public boolean equals (SavedClip other){ return song.equals(other.getSong());}
		public SavedClip copy(){ return new SavedClip(start, duration, song, tempo);}
		/*
		 * Compare functions for time and tempo, tempo uses absoulte value to find the closest match
		 * time does not since we dont want clips that are too short, cross fade whatever is extra
		 */	
		public boolean compareTime(SavedClip other, double time){
		    return (duration<time) || ((other.getDuration() >= time) && 
		    		(other.getDuration() < duration));
		}		
		public boolean compareTarget(SavedClip other, double target){
		    return (Math.abs(other.getTempo()*other.getEnergy() - target) < Math.abs(tempo*energy - target));
		}
		
		public String output(){
		    DecimalFormat df = new DecimalFormat("#.#");
		    return (song.getPath() + "|" + df.format(start) + "|" + df.format(duration));
		}
		public String toInterval(){
			return (start + "-" + (start+duration));
		}
		public String toString(){
			return (song.getTitle() + " , " + song.getArtist());
		}
		
		public double getEnergy() {return energy;}
		public double getStart() {return start;}
		public double getDuration() {return duration;}
		public void setDuration(double newDuration) {duration = newDuration;}
		public double getTempo() {return tempo;}
		public SavedSong getSong() {return song;}
		public void setTempo(double tempo) {this.tempo = tempo;}
    }
}
