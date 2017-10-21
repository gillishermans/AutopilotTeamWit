
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


public class Beeldherkenning {

	private final static int WIDTH = 200;
	private final static int HEIGHT = 200;
	private static float focalLength = 0.01f;
	private static float objectSize = 2000f;
    private static float[] radius = new float[1];
	private static Point center = new Point();
	private static Point screenCenter = new Point(100,100);
	
	//Main functie om te testen
	public static void main(String[] args) throws Exception{
        imageRecognition(null);
      
	}
	
	public static void imageRecognition(byte[] data) throws Exception{
	  //Laad de openCV library in
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        
      //Lees image in (om te testen, anders komt data als input binnen
        data = Files.readAllBytes(new File("C:\\Image\\pixels.txt").toPath());
      //Zet data = byte[] om in Mat
        Mat flipped = new Mat(WIDTH, HEIGHT, CvType.CV_8UC3);
        flipped.put(0, 0, data);
        Mat image = new Mat();
        Core.flip(flipped, image, 0);
        
      //Maak images aan
        Mat hsvImage = new Mat();
        Mat mask = new Mat();
        
        
      //convert the frame to HSV
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        
      //Zoek kleur tussen deze ranges (rood)
        Scalar minValues = new Scalar(0,100,100);
        Scalar maxValues = new Scalar(10,255,255);
        Core.inRange(hsvImage, minValues, maxValues, mask);
        
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

      //Zoek de contouren van de mask
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
        {
                
                for (int i = 0; i >= 0; i = (int) hierarchy.get(0, i)[0])
                {
                		//Teken de contouren in blauw
                        Imgproc.drawContours(image, contours, i, new Scalar(255, 0, 0));
                        //Bepaal minimal enclosing circle en teken hem op blurredImage
                        MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );                            
                        Imgproc.minEnclosingCircle(contour2f, center, radius);
                        int radiusInt = Math.round(radius[0]);
                        Imgproc.circle(image, center, radiusInt, new Scalar( 255,0 , 0 ));
                }
        }
        
        displayImage( Mat2BufferedImage(mask));
        Mat RGB_img = new Mat();
        Imgproc.cvtColor(image,RGB_img, Imgproc.COLOR_BGR2RGB);
        displayImage( Mat2BufferedImage(RGB_img));
        System.out.println(distanceToObject(2*radius[0]));
        System.out.println(horizontalAngle(center));
    }

	//Bereken afstand van een object tot de camera
	public static float distanceToObject(float objectHeightOnSensor){
		System.out.println(objectHeightOnSensor);
		return objectSize / (objectHeightOnSensor * focalLength);
		
	}
	
	//Bereken de horizontale hoek tussen middelpunt van de afbeelding en het object
	public static double horizontalAngle(Point center){
		double distancePointCenter = Math.abs(center.x - screenCenter.x);
		return Math.atan(distancePointCenter/focalLength);
	}
	
	//Bereken de verticale hoek tussen middelpunt van de afbeelding en het object
	public double verticalAngle(Point center){
		double distancePointCenter = Math.abs(center.y - screenCenter.y);
		return Math.atan(distancePointCenter/focalLength);
	}
	
	public static BufferedImage Mat2BufferedImage(Mat m){
		// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		// Fastest code
		// The output can be assigned either to a BufferedImage or to an Image

		    int type = BufferedImage.TYPE_BYTE_GRAY;
		    if ( m.channels() > 1 ) {
		        type = BufferedImage.TYPE_3BYTE_BGR;
		    }
		    int bufferSize = m.channels()*m.cols()*m.rows();
		    byte [] b = new byte[bufferSize];
		    m.get(0,0,b); // get all the pixels
		    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		    System.arraycopy(b, 0, targetPixels, 0, b.length);  
		    return image;
		}
	//Display the window
	public static void displayImage(Image img2){   
	    ImageIcon icon=new ImageIcon(img2);
	    JFrame frame=new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    JLabel lbl=new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}
