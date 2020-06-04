package userrecognition;

import org.opencv.imgcodecs.Imgcodecs;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.Face;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.face.BasicFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.face.FaceRecognizer;

public class MultiMatch {
	
	public static void run(String inFile, String templateFile, String outFile, int match_method) {
	    System.out.println("\nRunning Template Matching");

	    Mat img = Imgcodecs.imread(inFile);
	    Mat templ = Imgcodecs.imread(templateFile);

	    // / Create the result matrix
	    int result_cols = img.cols() - templ.cols() + 1;
	    int result_rows = img.rows() - templ.rows() + 1;
	    Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

	    // / Do the Matching and Normalize
	    Imgproc.matchTemplate(img, templ, result, match_method);
	    Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

	    while(true)
	    {
	    // / Localizing the best match with minMaxLoc
	    Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

	    Point matchLoc;
	    if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
	        matchLoc = mmr.minLoc;
	    } else {
	        matchLoc = mmr.maxLoc;
	    }      

	        if(mmr.maxVal >=0.97)
	         {
	            // / Show me what you got
	            Imgproc.rectangle(img, matchLoc, 
	                new Point(matchLoc.x + templ.cols(),matchLoc.y + templ.rows()), 
	                new    Scalar(0,255,0),2);
	            //Imgproc.putText(img, "Edited by me",
	              //  new Point(matchLoc.x + templ.cols(),matchLoc.y + templ.rows()),
	                //Core.FONT_HERSHEY_PLAIN, 1.0 ,new  Scalar(0,255,255));
	            Imgproc.rectangle(result, matchLoc, 
	                new Point(matchLoc.x + templ.cols(),matchLoc.y + templ.rows()), 
	                new    Scalar(0,255,0),-1);                 
	         }
	         else
	         {
	             break; //No more results within tolerance, break search
	         }
	    }

	    // / Show me what you got
	    //Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(), 
	    //matchLoc.y + templ.rows()), new Scalar(0, 255, 0),2);

	    // Save the visualized detection.
	    System.out.println("Writing "+ outFile);
	    Imgcodecs.imwrite(outFile, img);

	}



	public static void main(String[] args) {
	    System.loadLibrary("opencv_java310");
	    run("C:\\temp\\detection\\images.jpg", "C:\\temp\\detection\\images1.jpg",  "C:\\temp\\detection\\images.jpg", Imgproc.TM_CCOEFF_NORMED);
	    run("C:\\temp\\detection\\images.jpg", "C:\\temp\\detection\\images2.png",  "C:\\temp\\detection\\images.jpg", Imgproc.TM_CCOEFF_NORMED);
	}

}
