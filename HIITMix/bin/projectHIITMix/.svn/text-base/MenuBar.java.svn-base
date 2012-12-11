package projectHIITMix;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.*;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class MenuBar extends JMenuBar {
    	
    private MainFrame mf;
	int counter = 1;
	boolean playlistVisible = true;
	
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem newItem = new JMenuItem("New");
	private JMenuItem openItem = new JMenuItem("Open");
	private JMenuItem saveItem = new JMenuItem("Save");
	private JMenuItem saveAsItem = new JMenuItem("Save As...");
	private JMenuItem exitItem = new JMenuItem("Exit");
	
	private JMenuItem loadSaved = new JMenuItem("Load Saved Music");
	private JMenu editMenu = new JMenu("Edit");
	private JMenuItem addFolder = new JMenuItem("Add Music Folder");
	private JMenuItem deleteWorkout = new JMenuItem("Delete Workout");

	
	private JMenu viewMenu = new JMenu("View");
	private JMenuItem playlist = new JMenuItem("Hide Playlist");
	
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem readme = new JMenuItem("Show Readme");
	
	private JFileChooser fileChooser = new JFileChooser();
	private JComboBox sets;
	private JCheckBox warmup = new JCheckBox();
	private JCheckBox cooldown = new JCheckBox();
	private JComponent[] inputs;
	private Vector<Integer> numbers = new Vector<Integer>();
	
	public MenuBar(MainFrame mf){
	    this.mf = mf;
	    
	    JMenuItem[] menuItems = {newItem, openItem, saveItem, saveAsItem, 
	    		exitItem, addFolder, deleteWorkout,
	    		playlist, readme, loadSaved
	    };
	    
	    for (int i = 0; i < 20; i++){
	    	numbers.add(i+1);
	    }
	    
	    sets = new JComboBox(numbers);
	    
	    inputs = new JComponent[] {
                new JLabel("Sets"),
                sets,
                new JLabel("Warmup?"),
                warmup,
                new JLabel("Cooldown?"),
                cooldown
	    };
	    
	    for(int i = 0; i < 6; i++){
	    	fileMenu.add(menuItems[i]);
	    }
	    editMenu.add(addFolder);
	    editMenu.add(loadSaved);
	    editMenu.add(deleteWorkout);
	    viewMenu.add(playlist);
	    helpMenu.add(readme);
		
		MenuItemListener listener = new MenuItemListener();
		for (JMenuItem item : menuItems){
			item.addActionListener(listener);
		}
		
		add(fileMenu);
		add(editMenu);
		add(viewMenu);
		add(helpMenu);
	}
	
	public MainFrame getMainFrame(){
		return mf;
	}
	
	class WorkoutFilter extends FileFilter{
	    public boolean accept(File file) {
	        String filename = file.getName();
	        return filename.endsWith(".workout");
	    }
	    public String getDescription() {
	        return "*.workout";
	    }
	}
	
	class MenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == newItem){
				JOptionPane.showMessageDialog(null, inputs, "New Custom Workout", JOptionPane.PLAIN_MESSAGE);
				WorkoutProgram newWorkout = new WorkoutProgram((Integer) sets.getSelectedItem(), 
						warmup.isSelected(), cooldown.isSelected(), 
						"New Workout " + counter, mf.getControl());
				if(mf.getWorkoutList().isEmpty()){
					mf.getWorkouts().setSelectedIndex(0);
				}
				mf.getWorkouts().add(newWorkout);
				mf.getWorkouts().setSelectedValue(newWorkout, true);
				mf.getWorkoutList().addElement(newWorkout);
				mf.getWorkouts().setSelectedIndex(mf.getWorkoutList().getSize() -1);
				counter++;
				mf.validate();
				mf.repaint();
				
			} else if (e.getSource() == openItem){
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("Open");
				fileChooser.setDialogTitle("Open");
				fileChooser.addChoosableFileFilter(new WorkoutFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					WorkoutProgram openedWorkout = new WorkoutProgram(fileChooser.getSelectedFile(), 
							mf.getControl());
					if(mf.getWorkoutList().isEmpty()){
						mf.getWorkouts().setSelectedIndex(0);
					}
					mf.getWorkouts().add(openedWorkout);
					mf.getWorkouts().setSelectedValue(openedWorkout, true);
					mf.getWorkoutList().addElement(openedWorkout);
					mf.getWorkouts().setSelectedIndex(mf.getWorkoutList().getSize() -1);
					counter++;
					mf.validate();
					mf.repaint();
				} else {
					System.out.println("No Selection ");
				}
				
			} else if (e.getSource() == saveItem){
				if(mf.getCurrentWorkout().getSaved()){
					mf.getCurrentWorkout().saveWorkout();
				} else {
					fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("Save As...");
					fileChooser.setDialogTitle("Save As...");
					fileChooser.setAcceptAllFileFilterUsed(true);
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
						mf.getCurrentWorkout().saveAsWorkout(fileChooser.getSelectedFile());
					} else {
						System.out.println("No Selection ");
					}
				}
				
			} else if (e.getSource() == saveAsItem){
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("Save As...");
				fileChooser.setDialogTitle("Save As...");
				fileChooser.setAcceptAllFileFilterUsed(true);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					mf.getCurrentWorkout().saveAsWorkout(fileChooser.getSelectedFile());
				} else {
					System.out.println("No Selection ");
				}
				
			} else if(e.getSource() == exitItem){
				System.exit(0);
				
			} else if(e.getSource() == addFolder){
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setApproveButtonText("Open");
				fileChooser.setDialogTitle("Add Music Folder");
				fileChooser.setAcceptAllFileFilterUsed(true);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					mf.getControl().AddFolder(fileChooser.getSelectedFile());
				}
				else {
					System.out.println("No Selection ");
				}

			}else if(e.getSource() == loadSaved){
				mf.getControl().getFunctions().loadFromSave();

			} else if(e.getSource() == deleteWorkout) {
				WorkoutProgram currentWorkout = (WorkoutProgram) mf.getWorkouts().getSelectedValue();
				
				if(mf.getControl().getPresetWorkouts().contains(currentWorkout)){
					JOptionPane.showMessageDialog(null, "Can't delete example workouts!", "Error", JOptionPane.ERROR_MESSAGE);
				} else if(!mf.getWorkoutList().isEmpty() && mf.getWorkoutList().getSize() > 1) {
					if(!currentWorkout.deleteWorkout()){
						mf.getWorkouts().remove(currentWorkout);
						mf.getWorkoutList().removeElement(currentWorkout);
					} else {
						if(currentWorkout == mf.getWorkoutList().firstElement()) {
							if (currentWorkout != mf.getWorkoutList().lastElement()){
								mf.getWorkouts().setSelectedValue(mf.getWorkoutList().get(1), true);
							}
						} else {
							mf.getWorkouts().setSelectedValue(mf.getWorkoutList().firstElement(), true);
						}
						mf.getWorkouts().remove(currentWorkout);
						mf.getWorkoutList().removeElement(currentWorkout);
					}
				} else if (mf.getWorkoutList().getSize() == 1){
					JOptionPane.showMessageDialog(null, "Can't delete the last workout!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Nothing to delete!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			} else if(e.getSource() == playlist){
				if(!playlistVisible) {
					playlist.setText("Hide Playlist");
					playlist.validate();
				} else {
					playlist.setText("Show Playlist");
					playlist.validate();
				}
				playlistVisible = !playlistVisible;
			    mf.getPlaylistPane().setVisible(playlistVisible);
			    mf.validate();
				mf.repaint();
				
			} else if (e.getSource() == readme){
				try {
					Runtime.getRuntime().exec("notepad C://HIITMix//readme.txt");
				}catch(IOException ex){
					ex.printStackTrace();
				}
				
			} else {
				// what in the world did you do?!?!?
			}
		}
	}
}
