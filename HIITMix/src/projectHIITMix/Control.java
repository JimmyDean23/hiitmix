package projectHIITMix;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import projectHIITMix.SavedSong.SavedClip;
import com.echonest.api.v4.*;

@SuppressWarnings({ "rawtypes", "serial" })
public class Control {
	
	private EchoNestAPI en;
	private String echoNestAPICode;
	private TrackAnalysis analysis;
	private ArrayList<WorkoutProgram> workouts;
	private WorkoutProgram currentWorkout;
	private MusicFunctions functions;
	private MusicPanel mPanel;
	private MainFrame mf;
	private ButtonPanel bPanel;
	private playList pList;
	private ArrayList<WorkoutProgram> presetWorkouts = new ArrayList<WorkoutProgram>();
	
	public Control(MainFrame mainFrame){
		mf = mainFrame;
		getAPICode();
		pList = new playList();
		en = new EchoNestAPI(echoNestAPICode);
		workouts = new ArrayList<WorkoutProgram>();
		mPanel = new MusicPanel(this);
		setWorkouts();
		
		try {
			functions = new MusicFunctions(en, this);
		} catch (EchoNestException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getAPICode(){
		File codeFile = new File("C://HIITMix//jEN-4.x.p//API Code.txt");
		try {
			Scanner in = new Scanner(codeFile);
			echoNestAPICode = in.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public EchoNestAPI getEchoNestAPI() {return en;}
	public void setEchoNestAPI(EchoNestAPI en) {this.en = en;}
	public MusicPanel getMusicPanel() {return mPanel;}
	public MainFrame getMainFrame(){return mf;}
	public ArrayList<WorkoutProgram> getPresetWorkouts(){return presetWorkouts;}
	
	public void setbPanel(ButtonPanel bPanel){this.bPanel = bPanel;}
	public ButtonPanel getbPanel(){return bPanel;}
	public MusicFunctions getFunctions () {return functions;}
	public File getSelection(TreeSet<File> files, int heartrate) {return null;}
	public ArrayList<WorkoutProgram> getWorkouts() {return workouts;}
	public WorkoutProgram getCurrentWorkout(){return currentWorkout;}
	public playList getpList() {return pList;}
	public void setCurrentWorkout(WorkoutProgram workout){this.currentWorkout = workout;}

	public double getTempo(Track music) throws EchoNestException {
		analysis = music.getAnalysis();
		return analysis.getTempo();
	}
	
	public void findClips(){
		try {
			currentWorkout.setPlaylist(functions.findClips(currentWorkout));
		} catch (EchoNestException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void AddFolder(File rootFolder){
		functions.AddFolder(rootFolder);
		bPanel.activeButtons(1);
	}
	
	public void loadMusic(){
	  new Thread(new Runnable() {
		    public void run() {
			try {
			    functions.LoadMusic();
			} catch (EchoNestException e) {
			    e.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		}).start();
	}
	
	private void setWorkouts() {
		File documents = new JFileChooser().getFileSystemView().getDefaultDirectory();
		File workoutFile = new File(documents.getAbsolutePath() + "//HIITMixDocuments//SampleWorkouts//Interval1.workout");
		WorkoutProgram aWorkout = new WorkoutProgram(workoutFile, this);
		presetWorkouts.add(aWorkout);
		workouts.add(aWorkout);
		workoutFile = new File(documents.getAbsolutePath() + "//HIITMixDocuments//SampleWorkouts//Interval2.workout");
		aWorkout = new WorkoutProgram(workoutFile, this);
		presetWorkouts.add(aWorkout);
		workouts.add(aWorkout);
		workoutFile = new File(documents.getAbsolutePath() + "//HIITMixDocuments//SampleWorkouts//Interval3.workout");
		aWorkout = new WorkoutProgram(workoutFile, this);
		presetWorkouts.add(aWorkout);
		workouts.add(aWorkout);	
		workoutFile = new File(documents.getAbsolutePath() + "//HIITMixDocuments//SampleWorkouts//Waves.workout");
		aWorkout = new WorkoutProgram(workoutFile, this);
		presetWorkouts.add(aWorkout);
		workouts.add(aWorkout);	
		workoutFile = new File(documents.getAbsolutePath() + "//HIITMixDocuments//SampleWorkouts//Basic Intervals.workout");
		aWorkout = new WorkoutProgram(workoutFile, this);
		presetWorkouts.add(aWorkout);
		workouts.add(aWorkout);	
	}
		
	class playList extends JList{
		public playList(){
			super();
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setSelectedIndex(0);
			addListSelectionListener(new ListSelectionListener(){
				@Override
				public void valueChanged(ListSelectionEvent e) {
					SavedClip clip =  (SavedClip) pList.getSelectedValue();
					currentWorkout.setCurrentInterval(clip);
					mPanel.pauseSong();
				}
			});
		}
	}
}
