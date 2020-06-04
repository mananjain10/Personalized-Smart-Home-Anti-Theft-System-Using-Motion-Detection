package userrecognition;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class FaceRecog {
	
	public static void main(String args[])
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat matOrig = new Mat();
        VideoCapture capture = new VideoCapture("c:\\temp\\file_example_MP4_480_1_5MG.mp4");
        if( capture.isOpened()){
            while (true){  
                capture.read(matOrig);  
                // get some meta data about frame.              
                double fps = capture.get(Videoio.CAP_PROP_FPS);
                double frameCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
                double h = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
                double w = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
                double posFrames = capture.get(Videoio.CAP_PROP_POS_FRAMES);
                double posMsec = capture.get(Videoio.CAP_PROP_POS_MSEC);
                double speed = capture.get(Videoio.CAP_PROP_SPEED);
                if( !matOrig.empty() ) {
                    // do stuff
                }
            }
        }
        else
        {
        	System.out.println("Not Opening");
        }
	}

}
