package testing;

import static org.junit.Assert.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import com.echonest.api.v4.EchoNestException;
import projectHIITMix.*;

@SuppressWarnings("unused")
public class SongTesting {
	
	Control theControl;
	File rootFolder, music1, music2, music3;
	TreeSet<File> files;
	
	@Before
	public void initial(){
		theControl = new Control(null);
		File rootFolder = new File("src\\Test Music");
		music1 = new File("src\\Test Music\\Sleep Away.mp3");
		music2 = new File("src\\Test Music\\This Folder\\Maid with the Flaxen Hair.mp3");
		music3 = new File("src\\Test Music\\Another Folder\\Recursive\\Kalimba.mp3");
		//files = theControl.searchFolder(rootFolder);
	}
	
	@Test
	public void testSearch() { //tests recursive search for mp3's -- need to test for other file formats
		Assert.assertEquals(3, files.size());
		Assert.assertTrue(files.contains(music1));
		Assert.assertTrue(files.contains(music2));
		Assert.assertTrue(files.contains(music3));
	}
	
	@Test
	public void testSegment(){
		fail();
	}
	
	@Test
	public void testTempo() throws EchoNestException, IOException{
		Assert.assertEquals(99.276, theControl.getEchoNestAPI().uploadTrack(music1, false).getTempo(), .001);
		Assert.assertEquals(72.019, theControl.getEchoNestAPI().uploadTrack(music2, false).getTempo(), .001);
		Assert.assertEquals(119.766, theControl.getEchoNestAPI().uploadTrack(music3, false).getTempo(), .001);
	}
	


}

