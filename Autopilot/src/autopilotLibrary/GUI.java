package autopilotLibrary;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GUI extends Frame {
	
	private static final long serialVersionUID = 1L;
	//Declare variables
    private Panel pnlDrones;
    private Panel pnlLogs;
	
	public GUI() {
		//setLayout(new FlowLayout());
		setLayout(null);
		//Set its size to 500x500 pixels
		setSize(500,500);
		setTitle("Autopilot");
		//INIT pnlDrones
		pnlDrones = new Panel();
		pnlDrones.setLocation(10, 10);
		pnlDrones.setSize(200, 490);
		add(pnlDrones);
		pnlDrones.setVisible(true);
		//INIT pnlLogs
		pnlLogs = new Panel();
		pnlLogs.setLocation(260, 10);
		pnlLogs.setSize(200, 490);
		add(pnlDrones);
		pnlLogs.setVisible(true);
		//TEST
		addDrone(2);
		//SET FRAME TO VISIBLE
		setVisible(true);
	}
		    
	public static void main (String args[]){
		new GUI().run();
	}
	
	public void run() {
		setLayout(null);
		//Label lbl = new Label("Drone 1");
		//pnlDrones.add(lbl);
		//lbl.setLocation(10, 50);
		//lbl.setSize(50, 10);
	
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent we) {
	            dispose();
	         }
	    	}
		);
	}

	public void addPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		
	}

	public void addDrone(Integer index) {
		//INIT new label
		Label lbl = new Label("Drone " + index);
		pnlDrones.add(lbl);
		lbl.setLocation(20, 10);
		lbl.setSize(50, 10);
	}

}
