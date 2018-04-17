package autopilotLibrary;


import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;



public class GUI extends Frame {
	
	private Frame frame = new Frame("Autopilot");
	private Font headerFont = new Font("Verdana", Font.BOLD, 20);
	private Font itemFont = new Font("Verdana", Font.PLAIN, 20);
	
//	private ArrayList<Node> drones = new ArrayList<Node>();
//	private ArrayList<Node> packages = new ArrayList<Node>();
	
	private int yPos = 20;
	
	
	  
	public GUI(){  
		setHeader();
		//addDrone(1);
		frame.setSize(500,700);//frame size 300 width and 300 height  
		frame.setLayout(null);//no layout manager  
		frame.setVisible(true);//now frame will be visible, by default not visible  
		frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {

                System.exit(0);
            }
        } );
	}  
		
	public static void main(String args[]){  
		GUI f=new GUI();  
	} 
	
	public void setHeader() {
		Label lblDrone = new Label("Drones");
		lblDrone.setFont(headerFont);
		lblDrone.setBounds(12, 50, 80, 30);
		frame.add(lblDrone);
		repaint();
	}
	
	public void addDrone(Integer i) {
		Label lbl = new Label(i.toString());
		lbl.setFont(itemFont);
		lbl.setBounds(35, 90, 20, 30);
		frame.add(lbl);
		repaint();
	}
	
	public void drawLine(int x1, int x2, int y1, int y2) {
		
	}
	
	

	public void addPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		
	}




}
