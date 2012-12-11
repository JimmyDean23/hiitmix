package projectHIITMix;

import java.awt.Color;
import java.awt.Graphics;
import projectHIITMix.SavedSong.SavedClip;

public class Interval {
	private int length;
	private int heartRate;
	private int startX;
	private int startY;
	private int width;
	private int height;
	private boolean isActive;
	private SavedSong.SavedClip songClip;
	
	public Interval(int length, int heartRate) {
		this.length = length;
		this.heartRate = heartRate;
		startX = 0;
		startY = 0;
		width = 0;
		height = 0;
		isActive = false;
		songClip = null;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getHeartRate() {
		return heartRate;
	}
	
	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}
	
	public void setSongClip(SavedClip clip){
		songClip = clip;
	}
	
	public SavedClip getSongClip(){
		return songClip;
	}
	
	public void setDrawing(int startX, int startY, int width, int height){
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

	public void draw(Graphics g, Color color){
		g.setColor(color);
		g.fillRect(startX, startY, width, height);
		if(isActive){
			g.setColor(Color.MAGENTA);
			g.drawRect(startX+1, startY+1, width-2, height-2);
			g.drawRect(startX, startY, width, height);
			g.drawRect(startX-1, startY-1, width+2, height+2);
			g.drawRect(startX-2, startY-2, width+4, height+4);
		}
		else{
			g.setColor(Color.BLACK);
			g.drawRect(startX, startY, width, height);
		}
	}

	public boolean containsClick(int x, int y) {
		return (startX <= x && startY <= y && startX+width >= x && startY+height >= y);
	}

	public void setActive(boolean active) {
		isActive = active;
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	//To string functions for the GUI
	public String toString(){
		return (length + " sec @ " + heartRate + " % Max HR");
	}
	//output string for the file output. 
	public String toOutput(){
		return (length + " " + heartRate);
	}
	
}
