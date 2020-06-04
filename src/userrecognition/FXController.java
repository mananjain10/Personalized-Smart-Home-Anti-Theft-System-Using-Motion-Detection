package userrecognition;

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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

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




/**
 * 
 * 		
 */
public class FXController
{
	
	String filelocation="C:\\temp\\videos\\test10.mp4";
	
	// FXML buttons
	@FXML
	private Button cameraButton;
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;
	// checkboxes for enabling/disabling a classifier
	@FXML
	private CheckBox haarClassifier;
	@FXML
	private CheckBox lbpClassifier;
	@FXML
	private CheckBox isPlayVideo;
	@FXML
	private CheckBox newUser;
	@FXML
	private TextField newUserName;
	@FXML
	private Button newUserNameSubmit;
	
	private int count;
	
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;
	
	// face cascade classifier
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;
	
	public int index = 0;
	public int ind = 0;
	
	// New user Name for a training data
	public String newname;
	
	// Names of the people from the training set
	public HashMap<Integer, String> names = new HashMap<Integer, String>();
	
	// Random number of a training set
	public int random = (int )(Math.random() * 20 + 3);
	
	/**
	 * Init the controller, at start time
	 */
	public void init()
	{
		this.capture = new VideoCapture();
		this.faceCascade = new CascadeClassifier();
		this.absoluteFaceSize = 0;
		
		// disable 'new user' functionality
		this.newUserNameSubmit.setDisable(true);
		this.newUserName.setDisable(true);
		// Takes some time thus use only when training set
		// was updated 
		trainModel();
	}
	

	@FXML
	protected void playVideo()
	{
		
	}
	

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startCamera()
	{
		try{
		// set a fixed width for the frame
		originalFrame.setFitWidth(600);
		// preserve image ratio
		originalFrame.setPreserveRatio(true);
		
		if (!this.cameraActive)
		{
			// disable setting checkboxes
			this.haarClassifier.setDisable(true);
			this.lbpClassifier.setDisable(true);
			
			// disable 'New user' checkbox
			this.newUser.setDisable(true);
			
			// start the video capture
			if(isPlayVideo.isSelected())
			this.capture.open(filelocation);
			else
				this.capture.open(0);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						Image imageToShow = grabFrame();
						originalFrame.setImage(imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.cameraButton.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");
			// enable classifiers checkboxes
			this.haarClassifier.setDisable(false);
			this.lbpClassifier.setDisable(false);
			// enable 'New user' checkbox
			this.newUser.setDisable(false);
			
			// stop the timer
			try
			{
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log the exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
			
			// release the camera
			this.capture.release();
			// clean the frame
			this.originalFrame.setImage(null);
			
			// Clear the parameters for new user data collection
			index = 0;
			newname = "";
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Image grabFrame()
	{
		// init everything
		Image imageToShow = null;
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				//System.out.println("Before Frame "+frame);
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					//System.out.println("Frame is not Empty");
					// face detection
					this.detectAndDisplay(frame);
					
					// convert the Mat object (OpenCV) to Image (JavaFX)
					imageToShow = mat2Image(frame);
				}
				
			}
			catch (Exception e)
			{
				// log the (full) error
				//System.ou.println("ERROR: " + e);
				//e.printStackTrace();
				if(isPlayVideo.isSelected())
				this.capture.open(filelocation);
			}
		}
		
		return imageToShow;
	}
	
	
	private void trainModel () {
		// Read the data from the training set
				File root = new File("resources/trainingset/combined/");
									
				
				FilenameFilter imgFilter = new FilenameFilter() {
		            public boolean accept(File dir, String name) {
		                name = name.toLowerCase();
		                return name.endsWith(".png");
		            }
		        };
		        
		        File[] imageFiles = root.listFiles(imgFilter);
		        
		        List<Mat> images = new ArrayList<Mat>();
		        
		        System.out.println("THE NUMBER OF IMAGES READ IS: " + imageFiles.length);
		        
		        List<Integer> trainingLabels = new ArrayList<>();
		        
		        Mat labels = new Mat(imageFiles.length,1,CvType.CV_32SC1);
		        
		        int counter = 0;
		        
		        for (File image : imageFiles) {
		        	// Parse the training set folder files
		        	Mat img = Imgcodecs.imread(image.getAbsolutePath());
		        	// Change to Grayscale and equalize the histogram
		        	Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
		        	Imgproc.equalizeHist(img, img);
		        	// Extract label from the file name
		        	int label = Integer.parseInt(image.getName().split("\\-")[0]);
		        	// Extract name from the file name and add it to names HashMap
		        	String labnname = image.getName().split("\\_")[0];
		        	String name = labnname.split("\\-")[1];
		        	names.put(label, name);
		        	// Add training set images to images Mat
		        	images.add(img);

		        	labels.put(counter, 0, label);
		        	counter++;
		        }
                                //FaceRecognizer faceRecognizer = Face.createFisherFaceRecognizer(0,1500);
                                FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
                                //FaceRecognizer faceRecognizer = Face.createEigenFaceRecognizer(0,50);
		        	faceRecognizer.train(images, labels);
		        	faceRecognizer.save("traineddata");
	}
	
	/** Method for face recognition
	 * 
	 * grabs the detected face and matches it with
	 * the training set. If recognized the name of
	 * the person is printed below the face rectangle
	 * @return 
	 */
	
	private double[] faceRecognition(Mat currentFace) {
       	
        	// predict the label
        	
        	int[] predLabel = new int[1];
            double[] confidence = new double[1];
            int result = -1;
            
            FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
            faceRecognizer.load("traineddata");
        	faceRecognizer.predict(currentFace,predLabel,confidence);
//        	result = faceRecognizer.predict_label(currentFace);
        	result = predLabel[0];
        	
        	return new double[] {result,confidence[0]};
	}
	
	
	/**
	 * Method for face detection and tracking
	 * 
	 * @param frame
	 *            it looks for faces in this frame
	 */
	private void detectAndDisplay(Mat frame)
	{
		try{
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		Mat firstFrame = new Mat();
		Mat gray = new Mat();
		Mat frameDelta = new Mat();
		Mat thresh = new Mat();
		List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);
		
		String outputfileOriginal = Constants.WithoutMissingObjectLocation+"\\original.jpg";
		String outputfileCount = Constants.WithoutMissingObjectLocation+"\\"+System.currentTimeMillis()+".jpg";
		
		if(capture.read(frame)) {
			//convert to grayscale
			if(count==0)
			{
				 MatOfByte matOfByte = new MatOfByte();
				 Image imageToStore123=mat2Image(frame);
				 BufferedImage image123 = SwingFXUtils.fromFXImage(imageToStore123, null);
		          
		          
				File outputfile = new File(outputfileOriginal);
			       //System.out.println("outputfile--"+outputfile.getAbsolutePath());
			        try {
						ImageIO.write(image123, "jpg", outputfile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        count++;
			}
			else
			{
				count++;
			}
			
			Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
			Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);
			
			//compute difference between first frame and current frame
			Core.absdiff(firstFrame, gray, frameDelta);
			Imgproc.threshold(frameDelta, thresh, 125, 255, Imgproc.THRESH_BINARY);
			
			Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
			Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			
			for(int i=0; i < cnts.size(); i++) {
				System.out.println(Imgproc.contourArea(cnts.get(i)));
				
				if(Imgproc.contourArea(cnts.get(i)) < 100) {
					continue;
				}
				else{
					try{
					Image imageToStore=mat2Image(frame);
					 
					 BufferedImage image = SwingFXUtils.fromFXImage(imageToStore, null);

					 try {
						 File outputfile = new File(outputfileCount);
						 System.out.println(outputfile.getAbsolutePath());
						
							ImageIO.write(image, "jpg", outputfile);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							 CheckingDifferentImages.check(outputfile.getAbsolutePath(),outputfileOriginal);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 
					 System.out.println("Motion detected!!!");
					
					Thread thread=new Thread(new RunnableImplements());
					thread.start();
						
					 
				//System.exit(0);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
			}
				
		}
		else
		{
			if(isPlayVideo.isSelected())
			capture = new VideoCapture(filelocation);
		}
		// compute minimum face size (20% of the frame height, in our case)
		if (this.absoluteFaceSize == 0)
		{
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0)
			{
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		else
		{
			//System.out.println("Motion");
		}
		
		// detect faces
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
				
		// each rectangle in faces is a face: draw them!
		Rect[] facesArray = faces.toArray(); 
		for (int i = 0; i < facesArray.length; i++) {
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);

			// Crop the detected faces
			Rect rectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
			Mat croppedImage = new Mat(frame, rectCrop);
			// Change to gray scale
			Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
			// Equalize histogram
			Imgproc.equalizeHist(croppedImage, croppedImage);
			// Resize the image to a default size
			Mat resizeImage = new Mat();
			Size size = new Size(250,250);
			Imgproc.resize(croppedImage, resizeImage, size);
			
			// check if 'New user' checkbox is selected
			// if yes start collecting training data (50 images is enough)
			if ((newUser.isSelected() && !newname.isEmpty())) {
				if (index<20) {
					Imgcodecs.imwrite("resources/trainingset/combined/" +
					random + "-" + newname + "_" + (index++) + ".png", resizeImage);
				}
			}
//			int prediction = faceRecognition(resizeImage);
			double[] returnedResults = faceRecognition(resizeImage);
			double prediction = returnedResults[0];
			double confidence = returnedResults[1];
			
//			System.out.println("PREDICTED LABEL IS: " + prediction);
			int label = (int) prediction;
			String name = "";
			if (names.containsKey(label) ) {
				name = names.get(label);
				//JOptionPane.showMessageDialog(null, "Owner Has Detected,Application will Exit");
				 //System.exit(0);
			} else {
				name = "Unknown";
			}
			
			// Create the text we will annotate the box with:
//            String box_text = "Prediction = " + prediction + " Confidence = " + confidence;
            
            if (confidence>55) {
            	name= "UNKNOWN";
            }
            String box_text = "Prediction = " + name + " Confidence = " + confidence;
            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
            double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
            // And now put it into the image:
            Imgproc.putText(frame, box_text, new Point(pos_x, pos_y), 
            		Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
            if (name!=null && name.contains("nikita")) {
            	JOptionPane.showMessageDialog(null, "Owner Has Detected,Application will Exit");
            	System.exit(0);
            }
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	

	@FXML
	protected void newUserNameSubmitted() {
		if ((newUserName.getText() != null && !newUserName.getText().isEmpty())) {
			newname = newUserName.getText();
			//collectTrainingData(name);
			System.out.println("BUTTON HAS BEEN PRESSED");
			newUserName.clear();
		}
	}
	

	/**
	 * The action triggered by selecting the Haar Classifier checkbox. It loads
	 * the trained set to be used for frontal face detection.
	 */
	@FXML
	protected void haarSelected(Event event)
	{
		// check whether the lpb checkbox is selected and deselect it
		if (this.lbpClassifier.isSelected())
			this.lbpClassifier.setSelected(false);
			
		this.checkboxSelection("resources/haarcascades/haarcascade_frontalface_alt.xml");
	}
	
	/**
	 * The action triggered by selecting the LBP Classifier checkbox. It loads
	 * the trained set to be used for frontal face detection.
	 */
	@FXML
	protected void lbpSelected(Event event)
	{
		// check whether the haar checkbox is selected and deselect it
		if (this.haarClassifier.isSelected())
			this.haarClassifier.setSelected(false);
			
		this.checkboxSelection("resources/haarcascades/lbpcascade_frontalface.xml");
	}
	
	@FXML
	protected void newUserSelected(Event event) {
		if (this.newUser.isSelected()){
			this.newUserNameSubmit.setDisable(false);
			this.newUserName.setDisable(false);
		} else {
			this.newUserNameSubmit.setDisable(true);
			this.newUserName.setDisable(true);
		}
	}
	
	/**
	 * Method for loading a classifier trained set from disk
	 * 
	 * @param classifierPath
	 *            the path on disk where a classifier trained set is located
	 */
	private void checkboxSelection(String classifierPath)
	{
		// load the classifier(s)
		this.faceCascade.load(classifierPath);
		
		// now the video capture can start
		this.cameraButton.setDisable(false);
	}
	
	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 * 
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	private Image mat2Image(Mat frame)
	{
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
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
	
	
	 public class RunnableImplements implements Runnable
	  {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				SendMailWithoutMissing(Constants.WithoutMissingObjectLocation,"Motion Detected");
				SendMailWithoutMissing(Constants.missingObjectLocation,"Missing Detected");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void SendMailWithoutMissing(String dir,String subject) throws javax.mail.MessagingException 
	    {

	    	String files[]=new File(dir).list(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.endsWith(".jpg") || name.endsWith(".png");
				}
			} );
	    	
	    	
	    	if(files!= null && files.length>0){
	    	
	    	
	        String host = "smtp.gmail.com";
	        String Password = "Nikita@123"; 
	        //password
	        String from = "Nikitalawande4120@gmail.com";       // from address
	        String toAddress = "Nikitalawande4120@gmail.com";  // to address
	        String filename = dir+"\\"+files[0]; // attaching a file
	        String sub=subject;                   //subject
	        String body="Hi... someone has entered your house\n\n\nRegards..\n";// body
	        // Get system properties
	        java.util.Properties props = System.getProperties();
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtps.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
	        Session session = Session.getInstance(props, null);
	        java.io.File file = new java.io.File(filename);
	        long filesize = file.length();
	        long filesizeInKB = filesize / 1024;
	        double megabytes = (filesizeInKB/ 1024);
	        System.out.println("Size of File is: "+ filesizeInKB + " KB");
	  System.out.println("Size of File is: "+ megabytes + " MB");
	  double size1=20.1;
	  if(megabytes>size1) //file size must be below 20MB
	  System.out.println("File Size Must Below 20 MB");
	  else
	   {
	        javax.mail.internet.MimeMessage message = new javax.mail.internet.MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        message.setRecipients(Message.RecipientType.TO, toAddress);
	        java.util.Calendar currentDate = java.util.Calendar.getInstance();
	        java.text.SimpleDateFormat formatter= new java.text.SimpleDateFormat("yyyy-MMM-dd HH:mm:ss a"); 
	        java.lang.String dateToday = formatter.format(currentDate.getTime()).toLowerCase();
	        message.setSubject("Mail: "+dateToday+" "+sub);
	        BodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText(body);
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	        messageBodyPart = new MimeBodyPart();
	        DataSource source = new FileDataSource(filename);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(filename);
	        multipart.addBodyPart(messageBodyPart);
	        message.setContent(multipart);

	        try {
	            javax.mail.Transport tr = session.getTransport("smtps");
	            tr.connect(host, from, Password);
	            System.out.println("Connected to Server......");
	            System.out.println("Now Sending "+filename);
	            tr.sendMessage(message, message.getAllRecipients());
	            System.out.println("Mail Sent Successfully........");
	            tr.close();
	            file.delete();
	        } catch (javax.mail.SendFailedException sfe) 
	        {

	            System.out.println(sfe);
	        }
	   }
	   }
	    }
		  
		  
	  }
	
}
