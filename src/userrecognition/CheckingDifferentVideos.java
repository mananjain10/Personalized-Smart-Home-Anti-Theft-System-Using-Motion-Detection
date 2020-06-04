package userrecognition;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * I am gonna pass two images and I am gonna recort only the differences
 * trying to catch if there is a different object or not int the scene
 * @author maikon
 */
public class CheckingDifferentVideos {
	public static String fromImage="C:\\temp\\detection\\capture_.jpg";
	public static String toImage="C:\\temp\\detection\\capture_1.jpg";
    public static long check(String fromImage1,String toImage1)
    {
    	//System.out.println("To Image="+toImage1);
    	//System.out.println("From Image="+fromImage1);
    	
    	 if(fromImage1!=null && fromImage1.trim().equals(""))
         	return 0;
         if(toImage1!=null && toImage1.trim().equals(""))
         	return 0;
         
        BufferedImage im1 = null;
        BufferedImage im2 = null;
        try {
                //loading the two pictures 
                //read and load the image
                BufferedImage input = ImageIO.read(new File(toImage1));
                //build an image with the same dimension of the file read
                im1 =
                    new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
                //object create to draw into the bufferedImage
                Graphics2D g2d = im1.createGraphics();
                //draw input into im
                g2d.drawImage(input, 0,0, null);
            //making all again for the second image
                
                BufferedImage input2 = ImageIO.read(new File(fromImage1));
                //build an image with the same dimension of the file read
                im2 =
                    new BufferedImage(input2.getWidth(), input2.getHeight(), BufferedImage.TYPE_INT_ARGB);
                //object create to draw into the bufferedImage
                Graphics2D g2d2 = im2.createGraphics();
                //draw input into im
                g2d2.drawImage(input2, 0,0, null);
        } catch (IOException ex) {
            Logger.getLogger(CheckingDifferentVideos.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return showDifference(im1,im2);
        
    }
    
    public static long check()
    {
    	System.out.println("To Image="+toImage);
    	System.out.println("From Image="+fromImage);
    	
        BufferedImage im1 = null;
        BufferedImage im2 = null;
        try {
                //loading the two pictures 
                //read and load the image
                BufferedImage input = ImageIO.read(new File(toImage));
                //build an image with the same dimension of the file read
                im1 =
                    new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
                //object create to draw into the bufferedImage
                Graphics2D g2d = im1.createGraphics();
                //draw input into im
                g2d.drawImage(input, 0,0, null);
            //making all again for the second image
                
                BufferedImage input2 = ImageIO.read(new File(fromImage));
                //build an image with the same dimension of the file read
                im2 =
                    new BufferedImage(input2.getWidth(), input2.getHeight(), BufferedImage.TYPE_INT_ARGB);
                //object create to draw into the bufferedImage
                Graphics2D g2d2 = im2.createGraphics();
                //draw input into im
                g2d2.drawImage(input2, 0,0, null);
        } catch (IOException ex) {
            Logger.getLogger(CheckingDifferentVideos.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return showDifference(im1,im2);
        
    }
    public static int showDifference(BufferedImage im1, BufferedImage im2)
    {
        BufferedImage resultImage = 
                new BufferedImage(im1.getWidth(), im2.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        double THR = 50;
        int area = 0;
        for(int h=0; h < im1.getHeight(); h++)
        {
            for(int w=0; w < im1.getWidth(); w++)
            {
                    int pix1=0;
                    int alpha1 = 0xff &(im1.getRGB(w, h)>>24);
                    int red1 = 0xff &(im1.getRGB(w, h)>>16);
                    int green1 = 0xff & (im1.getRGB(w, h)>>8);
                    int blue1 = 0xff & im1.getRGB(w, h);  
                    
                    int pix2=0;
                    int alpha2 = 0xff &(im2.getRGB(w, h)>>24);
                    int red2 = 0xff &(im2.getRGB(w, h)>>16);
                    int green2 = 0xff & (im2.getRGB(w, h)>>8);
                    int blue2 = 0xff & im2.getRGB(w, h);  
                    
                    //euclidian distance to estimate the simil.
                    double dist =0;
                    dist = Math.sqrt(Math.pow((double)(red1-red2), 2.0) 
                            +Math.pow((double)(green1-green2), 2.0)
                            +Math.pow((double)(blue1-blue2), 2.0) );
                    if(dist >THR)
                    {
                        resultImage.setRGB(w, h, im2.getRGB(w, h));
                        area++;
                    }
                    else
                    {
                        resultImage.setRGB(w, h, 0);
                    }
            } //w
        } //h
        System.out.println("area="+area);
        if(area>100){
	        try {
	        	long no=System.currentTimeMillis();
	            ImageIO.write(resultImage, "PNG", new File(Constants.missingObjectLocation+"\\out"+no+".png"));
	        } catch (IOException ex) {
	            Logger.getLogger(CheckingDifferentVideos.class.getName()).log(Level.SEVERE, null, ex);
	            ex.printStackTrace();
	        }
        }
        
        return area;
        
    }//end function
    
    
    public static void main(String args[])
    {
    	
    	System.out.println(CheckingDifferentVideos.check("c:\\temp\\detection\\capture_1550987093239.jpg","c:\\temp\\detection\\original.jpg"));
    }
    
}
