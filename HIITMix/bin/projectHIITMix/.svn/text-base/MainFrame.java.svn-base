package projectHIITMix;

import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings({ "unchecked", "rawtypes", "serial", "unused"})
public class MainFrame extends JFrame{
	private Control theControl;
	private JList workouts;
	private JScrollPane workoutPane, listPane, playlistPane;
	private DefaultListModel workoutList = new DefaultListModel();
	private WorkoutProgram currentWorkout;
	private ImageIcon hiitmixIcon = new ImageIcon("C://HIITMix//Resources//images//icon.png");
	
	/*
	 * This is the main frame, all primary classes are declared here. 
	 */
	public MainFrame() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		setSize(1280,800);
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(null);
		setTitle("HIIT Mix");
		setLayout(new BorderLayout());
		setIconImage(hiitmixIcon.getImage());
		
		theControl = new Control(this);
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		JMenuBar menuBar = new MenuBar(this);
		setJMenuBar(menuBar);
		WorkoutSelect selectionListener = new WorkoutSelect();
		
		// workout list
		for(WorkoutProgram program : theControl.getWorkouts()){
			workoutList.addElement(program);
		}
		workouts = new JList(workoutList);
		workouts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workouts.setSelectedIndex(0);
		workouts.addListSelectionListener(selectionListener);
		listPane = new JScrollPane();
		listPane.setViewportView(workouts);
		listPane.setPreferredSize(new Dimension(150, 600));
		add(listPane, BorderLayout.WEST);
		
		// button panel
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(2, 1));
		ButtonPanel panel = new ButtonPanel(theControl);
		southPanel.add(theControl.getMusicPanel());
		southPanel.add(panel);
		add(southPanel, BorderLayout.SOUTH);
		
		// graph of workout
		currentWorkout = (WorkoutProgram) workouts.getSelectedValue();
		theControl.setCurrentWorkout(currentWorkout);
		currentWorkout.setPreferredSize(new Dimension(800, 300));
		workoutPane = new JScrollPane();
		workoutPane.setViewportView(currentWorkout);
		add(workoutPane, BorderLayout.CENTER);
		
		// playlists
		
		playlistPane = new JScrollPane();
		playlistPane.setViewportView(theControl.getpList());
		playlistPane.setPreferredSize(new Dimension(150, 200));
		add(playlistPane, BorderLayout.EAST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		validate();
		setVisible(true);
	}

	private class WorkoutSelect implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			remove(currentWorkout);
			if(!workoutList.isEmpty()){
				resetWorkoutPanel();
			} else {
				workoutPane.setVisible(false);
			}
		}
	};
			
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		MainFrame frame = new MainFrame();
	}
	/*
	 * Sets the center window of the program to display the workout
	 */
	public void resetWorkoutPanel(){
		workoutPane.setVisible(true);
		currentWorkout = (WorkoutProgram) workouts.getSelectedValue();
		theControl.setCurrentWorkout(currentWorkout);
		workoutPane.setViewportView(currentWorkout);
		playlistPane.setViewportView(theControl.getpList());
		theControl.getMusicPanel().makeBlank();
		validate();
		repaint();
	}
	
	public Control getControl() {return theControl;}
	public JList getWorkouts() {return workouts;}
	public DefaultListModel getWorkoutList(){return workoutList;}
	public JScrollPane getPlaylistPane() {return playlistPane;}
	public WorkoutProgram getCurrentWorkout(){return currentWorkout;}
}
