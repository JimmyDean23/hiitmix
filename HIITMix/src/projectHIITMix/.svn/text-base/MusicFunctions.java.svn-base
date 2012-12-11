package projectHIITMix;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import projectHIITMix.SavedSong.SavedClip;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.TrackAnalysis;

@SuppressWarnings({ "unchecked", "unused", "serial", "deprecation" })
public class MusicFunctions{
    
	//all outputs are tied to status and debug, status are less frequent but give less info
    private boolean debug = true, status = true, loading;
    private Control theControl;
    private ArrayList<SavedSong> saves;
    private ArrayList<SavedClip> clips;
    //Lists of files, one for first load and retry loading.
    private LinkedList<File> loadFile, retry;
    private LinkedList<LoadThread> threads;
    private EchoNestAPI en;
    private File rootFolder;
    private LoadDialog dialog;
    private double highestTarget = 0, highestInt = 0, lowestTarget = 100, lowestInt = 0;
    private int remaining, total, wait, maxRunning = 50;
    private File documents = new JFileChooser().getFileSystemView().getDefaultDirectory();
    
    //Constructor, initializes all of the variables
    public MusicFunctions(EchoNestAPI en, Control newControl) throws EchoNestException, IOException{
	this.en = en;
	theControl = newControl;
	saves = new ArrayList<SavedSong>();
	clips = new ArrayList<SavedClip>();
	loadFile = new LinkedList<File>();
	retry = new LinkedList<File>();
	rootFolder = null;
    }
    
    //Selects a folder 
    public void AddFolder(File newRootFolder){
	rootFolder = newRootFolder;
	TreeSet<File> theCollection = new TreeSet<File>();
	if (rootFolder == null) {
	    System.out.println("No file selected Yo");
	    return;	    
	}
	if(rootFolder != null) {
	    addTree(rootFolder, theCollection);
	}
	remaining = theCollection.size();
	//load songs previously saved
	
	if (debug) 
	    System.out.println("Found: " + theCollection.size());
	
	loading = true;	
	remaining = total = theCollection.size();
	
	threads = new LinkedList<LoadThread>();
	try {
		// throw out the first line since it is given a folder to use
		Scanner in = new Scanner(new File(documents.getAbsolutePath() + "//HIITMixDocuments//saves.saves"));
		if (in.hasNextLine())
			in.nextLine();
	    loadSavedMusic(theCollection, in);
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (EchoNestException e) {
	    e.printStackTrace();
	}
	loadFile.clear();
	loadFile.addAll(theCollection);
	theControl.getbPanel().activeButtons(1);
    }
    
    public void loadFromSave(){
    		Scanner in;
			try {
				//set the root folder to the first line given.
				in = new Scanner(new File(documents.getAbsolutePath() + "//HIITMixDocuments//saves.saves"));
				if (in.hasNextLine())
					rootFolder = new File(in.nextLine());
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    		AddFolder(rootFolder);
    }
    
    /*
     * iterate through to loadfile list and load each one into a thread calling econest. 
     * do not go over the maxrunning number of threads
     * if a thread errors or does not complete within a minute, add it to the retry list
     * 
     * if the loadfile list is empty begin iterating through the retry list
     */
    public ArrayList<SavedSong> LoadMusic() throws EchoNestException, IOException, InterruptedException{
	loading = true;
	dialog = new LoadDialog();
	dialog.setTotal("0/"+total);
	int delay = 500;
	wait = 0;
	// Continue loop until loading is completed or canceled
	for (int x = 0; loading; x++){ 
	    wait++;
	    remaining = threads.size();
	    
	    //if there are less that the max running number of threads and loadfile isn't empty pull from the loadfile
	    if (!loadFile.isEmpty() && threads.size() <= maxRunning){
		if (debug){
		    System.out.println("loading: " + loadFile.peek().getName() + " " + loadFile.size());
		}
		LoadThread temp = new LoadThread(loadFile.pop(), x, true);
		temp.start();
		threads.add(temp);
		wait = 0;
	    }else {
	    	
	    //same with retry list
		if (!retry.isEmpty() && threads.size() <= maxRunning){
		    if (debug){
			System.out.println("restarting: " + retry.peek().getName());
		    }
		    LoadThread temp = new LoadThread(retry.pop(), x, false);
		    temp.start();
		    threads.add(temp);
		    wait = 0;
		}else{
		    if (debug && (wait%5 == 0)) System.out.println("waiting: " + wait);
		    dialog.recentWait();
		}
	    }
	    
	    //Clean up the thread list, removing all completed or overtime threads
	    for (int i = 0; i<threads.size(); i++) {
		LoadThread thread = threads.get(i);
		if (!thread.isAlive()) {
		    threads.remove(i);
		    i--;
		} else {
			//This is the thread timeout. Currently set to 2 minutes. Each loop completes after .5 seconds
		    if ((x - thread.getStart()) >= 240) {
			if (thread.isAlive()) {
			    thread.stop();
			}
			retry.add(thread.getFile());
			threads.remove(i);
			i--;
		    }
		}
		//if there are enough songs saved to fill the intervals
		if (saves.size() >= theControl.getCurrentWorkout().getIntervals().size())
		    theControl.getbPanel().activeButtons(2);
	    }
	    //update the dialog box on every loop. making sure its up to date
	    dialog.setTotal((saves.size()) + "/" + total + 
		    "| waiting threads: " + threads.size() + "," + loadFile.size() + "," + retry.size());
	    		Thread.sleep(delay); 
	    }	
		//Once loading is completed, write all saves to file
	    saveMusic();
	    //empty threads
	    if (!threads.isEmpty()) {
	    	for (LoadThread thread: threads){
	    	    retry.add(thread.getFile());
	    	    thread.stop();
	    	}
	    	if (debug) System.out.println("Done loading");
	    	dialog.setRecent("Complete");
	    }
	    
	    return saves;
		
	}
    public void addSong(File file){
    	if (debug)	System.out.println(file.getPath());
    	LoadThread load = new LoadThread(file, -1, false);
    	load.start();
    }
    
    
	public void SaveSong(SavedSong song) throws EchoNestException{
	    
		//verifying song content, retry if data is corrupted
		if ((song.getPath() == null) || (song.getArtist() == null) || (song.getTitle() == null) || (song.getTempo()<= 0)){
			if (song.getPath()!= null)
				retry.add(song.getPath());
			if (debug){
				System.out.println("File corrupted");
			}
			return;
		}
		//Find the fastest and slowest songs setting them to the highest/lowest tempos
	    if (song.getTempo()*song.getEnergy() > highestTarget) {
	       highestTarget = song.getTempo()*song.getEnergy();
	    }
	    if (song.getTempo()*song.getEnergy() < lowestTarget && song.getTempo()*song.getEnergy() != 0) {
	        lowestTarget = song.getTempo()*song.getEnergy();
	    }
	    
	    if (!saves.contains(song))
	    	saves.add(song);
	    
	    if (debug) {
	    	System.out.println("Complete: " + song + ", Left: "+ (total - saves.size()));
	    }
	    if (saves.size() == total){
	    	    if (status) {
	    	    	System.out.println("done loading");
	    	    }
	    	    loading = false;
	    }
	    //update the dialog box 
	    wait = 0;
	    if (dialog != null ){
	    	dialog.setRecent("Finished: " + song.getTitle());
	    	dialog.setTotal((saves.size()) + "/" + total + 
	    			"| waiting threads: " + threads.size() + "," + loadFile.size() + "," + retry.size());
	    }
	}
	/*
	 * Add tree is a recursive call through the file menu to find every folder inside the given file and return every file that ends in 
	 * the common music types, .m4a, .mp3, .wav
	 */
	private void addTree(File file, Collection<File> music) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				String filename = child.getName();
				if(filename.endsWith(".m4a") || filename.endsWith(".mp3") || filename.endsWith(".wav")){
				    if (!music.contains(child)) {
				    	music.add(child);
				    }
				}		
				addTree(child, music);
			}
		}
	}
	
	/*
	 * Find clips goes through the saved songs and pulls out the inner segments returning a list of clips that match the given workout interval
	 */
	public ArrayList<SavedSong.SavedClip> findClips(WorkoutProgram workoutProgram) throws EchoNestException, IOException{
	    clips = new ArrayList<SavedSong.SavedClip>();
	    highestInt = 100;
	    lowestInt = 50;
	    if (debug)
	    	System.out.println( highestTarget + " " + highestInt + " " + lowestTarget + " " + lowestInt);
	    
	ArrayList<SavedSong.SavedClip> picks = new ArrayList<SavedSong.SavedClip>();
	ArrayList<Interval> intervals = workoutProgram.getIntervals();
	ArrayList<SavedSong> unused = (ArrayList<SavedSong>) saves.clone();
	for (Interval interval: intervals) {
	    double intensity = interval.getHeartRate();
	    //math to convert from heartrate and music tempo
	    intensity -= lowestInt;
	    intensity /= (highestInt-lowestInt);
	    intensity *= (highestTarget-lowestTarget);
	    intensity += lowestTarget;
    		
	    DecimalFormat df = new DecimalFormat("#");
    		
	    if (debug) {
		System.out.print("Int: " + df.format(intensity) + ", Time: " + interval.getLength() + " |"); 
	    }
	    for (SavedSong saved: unused){
    		    
		ArrayList<SavedSong.SavedClip> matches = saved.findChunk(interval.getLength());
		if (!matches.isEmpty()){
		    //refine clips of the song to the best matching the time
		    SavedSong.SavedClip best = matches.get(0);
		    for (SavedSong.SavedClip nextPick: matches){
			//if the next pick is closer in timing, make it te best
		    	if (best.compareTime(nextPick, interval.getLength())){
		    		best = nextPick;
		    	}
		    }
		    picks.add(best.copy());
		    
		} else {
		    if (debug)System.out.println("Song returned no matches"); 
		}
	    } 
	    if (!picks.isEmpty()){
		//refine the single clips from songs into the best matching tempo
	    	SavedSong.SavedClip best = picks.get(0).copy();
	    	for (SavedSong.SavedClip nextPick: picks){
	    		//if the next pick is closer in timing, make it the best
	    		if (best.compareTarget(nextPick, intensity)){
	    			best = nextPick.copy();
	    		}
	    	}
	    	best.setDuration(interval.getLength());
	    	clips.add(best);
	    	unused.remove(best.getSong());
	    	
	    } else {
	    	if (debug){
	    		System.out.println("clip not found to match"); 
	    	}
	    }
	    picks.clear();
	}
	
	if (debug)
		{ System.out.println("\n" + clips); }
	    
	    theControl.getbPanel().activeButtons(3);
	    return clips;
	}
	
	/*
	 * outputs the everything saved to a file including the current root folder
	 */
	
	public void saveMusic() throws IOException{
		PrintWriter out = new PrintWriter(new FileWriter(documents.getAbsolutePath() + "//HIITMixDocuments//saves.saves"));
		out.println(rootFolder.getPath());
		for (SavedSong song: saves){
			out.println(song.output());
		}
		out.close();
		if (debug) {
		    System.out.println("music saved"); 
		}
	}
	
	/*
	 * saves the selected clips that are used for the workout program
	 */
	public String saveClips() throws IOException{
		String fileName = (documents.getAbsolutePath() + "//HIITMixDocuments//savedClips" + new Random().nextInt(1000) + ".saves");
		PrintWriter out = new PrintWriter(new FileWriter(fileName)); 
		for (SavedClip clip: clips){
			out.println(clip.output());
		}
		out.close();
		if (debug) {
		    System.out.println("music saved"); 
		}
		return fileName;
	}
	
	/*
	 * Searches through the saved file and loads the saves that are found there
	 */
	public void loadSavedMusic(TreeSet<File> files, Scanner in) throws IOException, EchoNestException{
		
		for (SavedSong song: saves){
			if (files.contains(song.getPath()))
				files.remove(song.getPath());
		}
		while (in.hasNextLine()){
	    	SavedSong temp = new SavedSong(in.nextLine());
	    	if (debug) System.out.println("Loading save: " + temp.getPath());
	    	
	    	if (temp.getPath() != null && files.contains(temp.getPath())){
	    		files.remove(temp.getPath());
	    		SaveSong(temp);
	    	}
	    	
		}
		in.close();
		if (saves.size() >= theControl.getCurrentWorkout().getIntervals().size())
		    theControl.getbPanel().activeButtons(2);
	}
	
	
	public ArrayList<SavedSong> getSaves(){return saves;}
	public void setSaves(ArrayList<SavedSong> newSaves){saves = newSaves;}
	/*
	 * Adding A Dialog
	 */
	private class LoadDialog extends JDialog {
	    private JButton stop, reset;
	    private JPanel statusPanel;
	    private JTextField recent, total;
	    private JSlider slider;
	    LoadDialog(){
	    	reset = new JButton ("Reset Loading");
	    	stop = new JButton ("End Loading");
	    	slider = new JSlider(5, 120, maxRunning);
	    	recent = new JTextField(10);
	    	total = new JTextField(10);
	    	statusPanel = new JPanel();
	    	statusPanel.setLayout(new GridLayout (1,2));
	    	statusPanel.add(stop);
	    	statusPanel.add(reset);
	    	setTitle("Loading Songs");
	    	setLayout(new GridLayout (4,1));
	    	StopListener start = new StopListener();
	    	stop.addActionListener(start);
	    	reset.addActionListener(start);
	    	slider.addChangeListener(new SlideListener());
	    	add(total);
	    	add(recent);
	    	add(slider);
	    	add(statusPanel);
	    	
	    	setSize(300,150);
	    	setLocationRelativeTo(null);
	    	setVisible(true);
	    }
	    public class SlideListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
		    if (e.getSource() == slider){
			maxRunning = slider.getValue();
		    }
		}
	    }
	    public class StopListener implements ActionListener {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		if (e.getSource() == stop){
	    		    	try {
				    Thread.sleep(1000);
				} catch (InterruptedException e1) {
				    e1.printStackTrace();
				}
	    			loading = false;
	    			setVisible(false);
	    		}
	    		if (e.getSource() == reset){
	    			loading = false;
	    			new Thread(new Runnable() {
	    				public void run() {
	    					try {
	    						Thread.sleep(1000);
	    						dialog.setVisible(false);
	    						LoadMusic();
	    					} catch (EchoNestException e1) {
	    						e1.printStackTrace();
	    					} catch (IOException e1) {
	    						e1.printStackTrace();
	    					} catch (InterruptedException e1) {
	    						e1.printStackTrace();
	    					}
	    				}
	    			} ).start();
	    		}
	    	}
	    }
	    public void setRecent(String text){
	    	recent.setText(text);
	    }
	    public void setTotal(String text){
	    	total.setText("Loading songs.. " + text);
	    }
	    public void recentWait(){ recent.setText(recent.getText() + ".");}
	}
	/*
	 * Here is the load thread that is used to pass each file 
	 */
	public class LoadThread extends Thread{
	    private File file;
	    private int start;
	    private boolean restart;
	    
	    public LoadThread(File file, int time, boolean restart) {
	    	super();
	    	this.file = file;
	    	this.start = time;
	    }

	    public File getFile() {return file;}
	    public int getStart() {return start;}

	    @Override
	    public void run(){
	    	Track each;
	    	try {
	    		each = null;
	    		if (restart)
	    			each = en.getKnownTrack (file);
		    
	    		if (each == null) each = en.uploadTrack(file, false);
	    		Song songData = new Song(en, each.getSongID());
	    		if (loading) SaveSong(new SavedSong(each, each.getAnalysis(), songData, file));
	    		
	    		} catch (EchoNestException e) {
	    			if (debug) {
	    				System.out.println("Upload error (echonest)");
	    			}
			    			//e.printStackTrace();
	    			if (start >= 0){
	    				dialog.setRecent("Reloading: " + file.getName());
	    				retry.addLast(file);
	    			}
	    		}
	    	catch (IOException e) {
				if (debug) {
			    	   System.out.println("File error");
			    }
				if (start >= 0){
			    	dialog.setRecent("Reloading: "  + file.getName());
			    	retry.addLast(file);
				}
	    	}
	    	
	    }
	}	
}
	
