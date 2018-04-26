package autopilotLibrary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import enums.PhaseEnum;
import interfaces.AutopilotOutputs;

public class GUIAutopilot {
	
	JPanel mainPanel = new JPanel(new BorderLayout());
	
    JPanel pnlDrone = new JPanel(new FlowLayout());
    JPanel pnlDroneHeader = new JPanel(new BorderLayout());
    JPanel pnlDroneInfo = new JPanel(new BorderLayout());
    
    JLabel lblLIncl = new JLabel();
    JLabel lblRIncl = new JLabel();
    JLabel lblHIncl = new JLabel();
    JLabel lblVIncl = new JLabel();
    JLabel lblState = new JLabel();
    
    Font font = new Font("Verdana", Font.BOLD, 20);
    
    
    
    HashMap<Integer,JLabel> drones = new HashMap<Integer,JLabel>();
    
    JFrame frame = new JFrame();
	
	public GUIAutopilot() {
		
		mainPanel.add(pnlDrone);
		
		//pnlDrone.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"DRONE"));
		pnlDrone.setLayout(new BoxLayout(pnlDrone, BoxLayout.X_AXIS));
        
        //HEADER FOR PANELDRONE
        
        //pnlDroneHeader.setBorder(BorderFactory.createTitledBorder(""));
		addToHeader("LEFTINCL :");
		addToHeader("RIGHTINCL:");
		addToHeader("HORSTAB  :");
		addToHeader("VERSTAB  :");
		addToHeader("STATE    :");
		
        
        pnlDroneHeader.setLayout(new BoxLayout(pnlDroneHeader, BoxLayout.Y_AXIS));
        pnlDrone.add(pnlDroneHeader, BorderLayout.WEST);
        
        lblLIncl.setFont(font);
        lblRIncl.setFont(font);
        lblHIncl.setFont(font);
        lblVIncl.setFont(font);
        lblState.setFont(font);
        
        lblLIncl.setText("INIT");
        lblRIncl.setText("INIT");
        lblHIncl.setText("INIT");
        lblVIncl.setText("INIT");
        lblState.setText("INIT");
        
        pnlDroneInfo.add(lblLIncl);
        pnlDroneInfo.add(lblRIncl);
        pnlDroneInfo.add(lblHIncl);
        pnlDroneInfo.add(lblVIncl);
        pnlDroneInfo.add(lblState);
        
       
        
        pnlDroneInfo.setLayout(new BoxLayout(pnlDroneInfo, BoxLayout.Y_AXIS));
        //pnlDroneInfo.add(new JLabel("HELLO WORLD"));
        
        pnlDrone.add(pnlDroneInfo, BorderLayout.CENTER);
        
        /////////////////////////////////HEADER FOR PANELPACKAGE//////////////////////////////////
        
        
        
        
        
        
        //addDrone(1);

        frame.add(mainPanel); 
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(400,300); //220
        frame.setLocation(10, 10);
        frame.setVisible(true);
        frame.setResizable(false);
    
	}

	public static void main(String[] args) { 
		new GUIAutopilot();
    }
	
	
	
	public void addToHeader(String txt) {
		JLabel l = new JLabel(" " + txt + "  ");
		l.setFont(font);
		pnlDroneHeader.add(l);
		
	}
	
	public void updateGUI(AutopilotOutputs outputs) {
		setLeftInclination(outputs.getLeftWingInclination());
		setRightInclination(outputs.getRightWingInclination());
		setHorizontalStabInclination(outputs.getHorStabInclination());
		setVerticalStabInclination(outputs.getVerStabInclination());
	}
	
	public void setLeftInclination(Float incl) {
		lblLIncl.setText(incl.toString());
	}
	
	public void setRightInclination(Float incl) {
		lblRIncl.setText(incl.toString());
	}

	public void setHorizontalStabInclination(Float incl) {
		lblHIncl.setText(incl.toString());
	}
	
	public void setVerticalStabInclination(Float incl) {
		lblVIncl.setText(incl.toString());
	}
	
	public void setState(PhaseEnum p) {
		lblState.setText(p.toString());
	}
	
	public void changeStateDrone(PhaseEnum s, Integer index) {
		JLabel l = (JLabel) ((JPanel) pnlDroneInfo.getComponent(index)).getComponent(2);
		switch(s) {
		case INIT: {
			l.setText("            " + "INIT" + "    ");
			break;
		}
		case OPSTIJGEN: {
			l.setText("     " + "TAKEOFF" + " ");
			break;
		}
		case RIJDEN :{
			l.setText("       " + "DRIVING" + " ");
			break;
		}
		case STABILISEREN: {
			l.setText("  " + "STABELIZE" + " ");
			break;
		}
		case STABILISEREN1: {
			l.setText("  " + "STABELIZE" + " ");
			break;
		}
		case LANDEN: {
			l.setText("      " + "LANDING" + " ");
			break;
		}
		case TAXIEN: {
			l.setText("        " + "TAXIING" + " ");
			break;
		}
		default: {
			l.setText("         " + "FLYING" + " ");
		}
		}
	}
	
	
	

	
}
