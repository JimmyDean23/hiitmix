package projectHIITMix;

import java.io.*;

import javax.swing.JOptionPane;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class MP3Player {
	private AudioMediaPlayerComponent audioPlayer;
	
	public MP3Player(){
		String programFiles32bit = "C://HIITMix//lib//vlc_32bit";
		String programFiles64bit = "C://HIITMix//lib//vlc_64bit";
		
		// uses 64bit VLC if user has 64bit OS and 64bit JRE, otherwise uses 32bit VLC
		if(new File("C://Program Files (x86)//Java").exists() && new File(programFiles64bit).exists()){
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), programFiles64bit);
			Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		} else if(new File("C://Program Files//Java").exists() && new File(programFiles32bit).exists()){
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), programFiles32bit);
			Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		} else {
			// user does not have Java installed
			JOptionPane.showMessageDialog(null, "There was a problem loading VLC and/or Java.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		audioPlayer = new AudioMediaPlayerComponent() {
		      @Override
		      public void finished(MediaPlayer mediaPlayer) {
		    	  System.exit(0);
		      }
		      @Override
		      public void error(MediaPlayer mediaPlayer) {
		    	  System.out.println("Failed to play media");
		    	  System.exit(1);
		      }
		};
	}
	
	public void loadSong(File file){
		audioPlayer.getMediaPlayer().playMedia(file.getAbsolutePath());
		audioPlayer.getMediaPlayer().stop();
	}
	
	public AudioMediaPlayerComponent getPlayer(){return audioPlayer;}
	public void play(){audioPlayer.getMediaPlayer().play();}
	public void pause(){audioPlayer.getMediaPlayer().pause();}
	
}
