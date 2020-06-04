package userrecognition;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class PlayVideo {
	  public static void main(String[] args) {
		  
		  try {
		    	System.load("c:\\temp\\opencv_java300.dll");
		    	System.load("c:\\temp\\opencv_ffmpeg300_64.dll");
		    } catch (UnsatisfiedLinkError e) {
		      System.err.println("Native code library failed to load.\n" + e);
		      System.exit(1);
		    }

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		  
		  
		  
		    Mat frame = new Mat();
		    VideoCapture camera = new VideoCapture("C:\\temp\\videos\\tree.avi");
		    JFrame jframe = new JFrame("Title");
		    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    JLabel vidpanel = new JLabel();
		    jframe.setContentPane(vidpanel);
		    jframe.setSize(200, 200);
		    jframe.setVisible(true);
		    
		    while (true) {
		        if (camera.read(frame)) {

		            ImageIcon image = new ImageIcon(mat2BufferedImg(frame));
		            vidpanel.setIcon(image);
		            vidpanel.repaint();

		        }
		        else
		        {
		        	System.out.println("Not Woring");
		        }
		    }
		}
	  
	  public static BufferedImage mat2BufferedImg(Mat in)
	    {
	        BufferedImage out;
	        byte[] data = new byte[320 * 240 * (int)in.elemSize()];
	        int type;
	        in.get(0, 0, data);

	        if(in.channels() == 1)
	            type = BufferedImage.TYPE_BYTE_GRAY;
	        else
	            type = BufferedImage.TYPE_3BYTE_BGR;

	        out = new BufferedImage(320, 240, type);

	        out.getRaster().setDataElements(0, 0, 320, 240, data);
	        return out;
	    }
}
