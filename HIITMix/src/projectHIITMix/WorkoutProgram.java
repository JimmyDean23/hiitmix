package projectHIITMix;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import projectHIITMix.SavedSong.SavedClip;

@SuppressWarnings({"serial", "unused"})
public class WorkoutProgram extends JPanel implements MouseListener{
	private ArrayList<Interval> intervals;
	private ArrayList<SavedClip> playlist;
	private int seconds;
	private String name;
	private MusicPanel panelListener;
	private Control control;
	private File workoutFile;
	private Interval currentInterval;
	private boolean warmup;
	private boolean cooldown;
	private boolean saved = false;
	
	//workout program constructor for loading from file
	public WorkoutProgram(File theFile, Control control){
		initialize(control);
		workoutFile = theFile;
		try {
			setProgram(theFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//constructor for making custom workouts
	public WorkoutProgram(int sets, boolean warm, boolean cool, String givenName, Control control){
		initialize(control);
		int i = 0;
		warmup = warm;
		if(warmup){
			intervals.add(new Interval(300, 40));
			seconds += 300;
		}
		//alternate from 60% to 80% each at 30s
		while (i < sets){
			if(i % 2 == 0){
				intervals.add(new Interval(30, 80));
				seconds += 30;
			} else {
				intervals.add(new Interval(30, 60));
				seconds += 30;
			}
			i++;
		}
		cooldown = cool;
		if(cooldown){
			intervals.add(new Interval(300, 40));
			seconds += 300;
		}
		name = givenName;
	}
	
	//create variables
	private void initialize(Control control) {
		intervals = new ArrayList<Interval>();
		playlist = new ArrayList<SavedClip>();
		seconds = 0;
		currentInterval = null;
		this.control = control;
		panelListener = control.getMusicPanel();
		addMouseListener(this);
	}
	
	//saves the workouts to existing file
	public void saveWorkout(){
		try{
			FileWriter fstream;
			fstream = new FileWriter(workoutFile.getAbsolutePath(), false);
			writeToFile(fstream);
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	//Saves workout to specific file
	public void saveAsWorkout(File filename){
		try{
			FileWriter fstream;
			name = filename.getName();
			if(filename.getAbsolutePath().endsWith(".workout")){
				fstream = new FileWriter(filename, false);
				workoutFile = new File(filename.getAbsolutePath());
				name = name.substring(0, name.length()-8);
			} else {
				fstream = new FileWriter(filename + ".workout", false);
				workoutFile = new File(filename.getAbsolutePath() + ".workout");	
			}
			writeToFile(fstream);
			saved = true;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeToFile(FileWriter fstream){
		try{
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(name);
			if(warmup) {
				out.write("\r\ntrue");
			} else {
				out.write("\r\nfalse");
			}
			if(cooldown) {
				out.write("\r\ntrue");
			} else {
				out.write("\r\nfalse");
			}
			for(Interval i : intervals){
				out.write("\r\n" + i.toOutput());
			}
			out.close();
			saved = true;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public boolean deleteWorkout(){
		if(workoutFile != null){
			return workoutFile.delete();
		} else {
			return false;
		}
	}
	
	//
	public void setProgram(File file) throws FileNotFoundException{
		workoutFile = file;
		Scanner in = new Scanner(file);
		name = in.nextLine();
		warmup = in.nextBoolean();
		cooldown = in.nextBoolean();
		saved = true;
		int time, intensity = 0;
		while(in.hasNextLine()){
			time = in.nextInt();
			intensity = in.nextInt();
			seconds += time;
			intervals.add(new Interval(time, intensity));
		}
	}
	
	public void addInterval(Interval i){
		if(currentInterval == intervals.get(intervals.size()-1)){
			intervals.ensureCapacity(intervals.size()+1);
		}
		intervals.add(intervals.indexOf(currentInterval)+1,i);
		validate();
		repaint();
		control.getMainFrame().resetWorkoutPanel();
	}
	
	public void deleteInterval(){
		if(warmup && currentInterval == intervals.get(0)){
			warmup = false;
		}
		if(cooldown && currentInterval == intervals.get(intervals.size()-1)){
			cooldown = false;
		}
		intervals.remove(currentInterval);
		currentInterval = null;
		validate();
		repaint();
		control.getMainFrame().resetWorkoutPanel();
	} 
	
	public void editInterval(int newLength, int newIntensity){
		currentInterval.setLength(newLength);
		currentInterval.setHeartRate(newIntensity);
		validate();
		repaint();
		control.getMainFrame().resetWorkoutPanel();
	}
	
	public void setPlaylist(ArrayList<SavedClip> clips) throws IOException{
		playlist = clips;
		File documents = new JFileChooser().getFileSystemView().getDefaultDirectory();
		PrintWriter out = new PrintWriter(new FileWriter(documents.getAbsolutePath() + "//HIITMixDocuments//savedClips.txt"));
		for(int i = 0; i < intervals.size(); i++){
			intervals.get(i).setSongClip(playlist.get(i));
			out.println(playlist.get(i).output());
		}
		out.close();
	}
	
	//Getters and setters
	public ArrayList<SavedClip> getPlaylist(){return playlist;}
	public void setPanelListener(MusicPanel panelListener) {this.panelListener = panelListener;}
	public int getSeconds(){return seconds;}
	public String getName(){return name;}
	public Interval getCurrentInterval(){return currentInterval;}
	public ArrayList<Interval> getIntervals() {return intervals;}
	public boolean getSaved() {return saved;}
	public void setSaved(boolean save){saved = save;}
	
	public void setCurrentInterval(SavedClip audioClip){
		if (audioClip != null){
			for (Interval p : intervals){
				if (p.getSongClip().equals(audioClip)){
					currentInterval = p;
				}
			}
		}
		
		if(currentInterval != null){
			for(Interval p : intervals){
				p.setActive(false);
			}
			currentInterval.setActive(true);
			panelListener.setInfo(currentInterval.getSongClip(), (intervals.indexOf(currentInterval) + 1), currentInterval);
			control.getpList().setSelectedValue(currentInterval.getSongClip(), true);
		}
		validate();
		repaint();
	}
	
	private void pickColorAndDraw(Graphics g, Interval p, int j) {
		if((j == 0 && warmup)|| (j == intervals.size() - 1 && cooldown)){
			p.draw(g, new Color(50,210,50));
		} else if(p.getHeartRate() <= 100 && p.getHeartRate() >= 70){
			p.draw(g, new Color(220,0,0));
		} else {
			p.draw(g, new Color(230,230,0));
		}
	}
	
	@Override
	public String toString() {return name;}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int xStart = 70;
		int yStart = this.getHeight() - 55;
		int maxY = 20;
		int maxX = this.getWidth() - 30;
		
		g.setColor(Color.BLACK);
		g.drawLine(xStart - 20, maxY, xStart - 20, yStart);
		
		double yMult = (yStart - maxY)/100.0;
		double xMult = (maxX - xStart)/(double)seconds;
		for(int i = 0; i <= 100; i += 10){
			if (i % 100 != 0) {
				g.drawString(String.valueOf(i) + "%", xStart - 52, yStart - (int)(yMult*i) - 1);
			} else {
				if (i == 0) {
					g.drawString(String.valueOf(i) + "%", xStart - 45, yStart - (int)(yMult*i) - 1);
				}
				if (i == 100) {
					g.drawString(String.valueOf(i) + "%", xStart - 58, yStart - (int)(yMult*i) - 1);
				}
			}
			g.drawLine(xStart - 30, yStart - (int)(yMult*i), xStart - 10, yStart - (int)(yMult*i));
		}
		if(seconds <= 2700){
			for(int i = 0; i <= seconds; i += 60){
				g.drawLine(xStart + (int)(xMult*i), yStart + 10, xStart + (int)(xMult*i), yStart + 30);
				g.drawString(String.valueOf(i/60), xStart + (int)(xMult*i) + 1, yStart + 37);
			}
		} else {
			for(int i = 0; i <= seconds; i += 120){
				g.drawLine(xStart + (int)(xMult*i), yStart + 10, xStart + (int)(xMult*i), yStart + 30);
				g.drawString(String.valueOf(i/60), xStart + (int)(xMult*i) + 1, yStart + 37);
			}
		}
		
		g.drawLine(xStart, yStart + 20, xStart + (int)(seconds*xMult), yStart + 20);
		
		int x = 0;
		Interval selected = null;
		int selectInt = 0;
		for(int j = 0; j < intervals.size(); j++){
			Interval p = intervals.get(j);
			if(p.isActive()){
				selected = p;
				selectInt = j;
			}
			int height = (int)(p.getHeartRate()*yMult);
			int width = (int)(p.getLength()*xMult);
			p.setDrawing(xStart + x, yStart - height, width, height);
			pickColorAndDraw(g, p, j);
			x += width;
		}
		if(selected != null) {
			pickColorAndDraw(g, selected, selectInt);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		//currentInterval = null;
		int num = 0, count = 0;
		for(Interval p : intervals){
			if(p.containsClick(e.getX(), e.getY())){
				currentInterval = p;
				num = count + 1;
			}
			count++;
		}
		setCurrentInterval(null);
	}
}
