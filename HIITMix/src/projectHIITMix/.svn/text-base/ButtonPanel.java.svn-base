package projectHIITMix;

import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.echonest.api.v4.EchoNestException;

import projectHIITMix.SavedSong.SavedClip;

@SuppressWarnings({ "unchecked", "rawtypes", "serial", "unused" })
public class ButtonPanel extends JPanel{
	
	private Control theControl;
	private JButton analyze, delete, add, redo, finish;
	private JButton[] buttonList;
	
	public ButtonPanel(Control theControl){
		this.theControl = theControl;
		theControl.setbPanel(this);
		analyze = new JButton("Analyze Songs");
		analyze.setEnabled(false);
		delete = new JButton("Delete Song");
		delete.setEnabled(false);
		add = new JButton("Add Song");
		redo = new JButton("Generate Playlist");
		redo.setEnabled(false);
		finish = new JButton("Create Playlist");
		finish.setEnabled(false);
		JButton[] tempList = {analyze, delete, add, redo, finish};
		buttonList = tempList;		
		
		ButtonListener listener = new ButtonListener();
		redo.addActionListener(listener);
		delete.addActionListener(listener);
		analyze.addActionListener(listener);
		add.addActionListener(listener);
		finish.addActionListener(listener);
		
		GridLayout thisLayout = new GridLayout(1,0);
		thisLayout.setHgap(3);
		setLayout(thisLayout);
		for(JButton b : buttonList){
			add(b);
		}
	}
	
	public void activeButtons(int choice){
	    switch (choice){
	    case 1:
		analyze.setEnabled(true);
		break;
	    case 2:
		redo.setEnabled(true);
		break;
	    case 3:
		delete.setEnabled(true);
		finish.setEnabled(true);
		break;
	    }
	}
	private class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == redo){
				DefaultListModel songList = new DefaultListModel();
				theControl.findClips();
				for(SavedClip clip : theControl.getCurrentWorkout().getPlaylist()){
					songList.addElement(clip);
				}
				theControl.getpList().setModel(songList);
				finish.setEnabled(true);
			}
			if (e.getSource() == finish){
				new Thread(new Runnable() {
					public void run(){
						Process p;
						String python = "C:\\HIITMix\\lib\\PythonWithEchoNest\\python.exe";
						String splice = "C:\\HIITMix\\lib\\Splicer.py";
						File documents = new JFileChooser().getFileSystemView().getDefaultDirectory();
						String output = documents.getAbsolutePath() + "\\HIITMixDocuments\\WorkoutMusic\\" + theControl.getCurrentWorkout().toString();
						try {
							String inputFile = new File(theControl.getFunctions().saveClips()).getAbsolutePath();
							System.out.println((python + " " + splice + " " + inputFile + " " + output));
							p = Runtime.getRuntime().exec(python + " " + splice + " " + inputFile + " " + output);
						} catch (IOException e1) {
							e1.printStackTrace();
						}	
					}
				}).start();
			}
			if (e.getSource() == analyze){
			    theControl.loadMusic();
			}
			if (e.getSource() == delete){
			    System.out.println("Deleting: ");
			    ArrayList<SavedSong> saves = theControl.getFunctions().getSaves();
			    saves.remove(theControl.getCurrentWorkout().getCurrentInterval().getSongClip().getSong());
			    theControl.getFunctions().setSaves(saves);
			    theControl.getMusicPanel().makeBlank();
			    //Creating a new playlist after deleting a song.
			    DefaultListModel songList = new DefaultListModel();
				theControl.findClips();
				for(SavedClip clip : theControl.getCurrentWorkout().getPlaylist()){
					songList.addElement(clip);
				}
				theControl.getpList().setModel(songList);
				finish.setEnabled(true);
			}
			if (e.getSource() == add){
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.setApproveButtonText("Open");
				fileChooser.setDialogTitle("Add Song");
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "Wav, Mp3, M4a", "mp3", "wav", "m4a");
				fileChooser.setFileFilter(filter);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
					theControl.getFunctions().addSong(fileChooser.getSelectedFile()); 
				}

			}
		}	
	}
	
} 
