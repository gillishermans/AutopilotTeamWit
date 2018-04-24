package autopilotLibrary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import enums.OccupationEnum;
import enums.PhaseEnum;

public class GUIAutopilot {
	
	JPanel mainPanel = new JPanel(new BorderLayout());
	
    JPanel pnlDrone = new JPanel(new FlowLayout());
    JPanel pnlDroneInfo = new JPanel(new BorderLayout());
    
    JPanel pnlPackage = new JPanel(new FlowLayout());
    JPanel pnlPackageProc = new JPanel(new BorderLayout());
    JPanel pnlPackageTodo = new JPanel(new BorderLayout());
    
    JPanel panel4 = new JPanel(new FlowLayout());
    
    HashMap<Integer,JLabel> drones = new HashMap<Integer,JLabel>();
    
    JFrame frame = new JFrame();
	
	public GUIAutopilot() {
		
		
		pnlDrone.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"DRONE"));
		pnlDrone.setLayout(new BoxLayout(pnlDrone, BoxLayout.Y_AXIS));
		
		pnlPackage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"PACKAGE"));
		pnlPackage.setLayout(new BoxLayout(pnlPackage, BoxLayout.Y_AXIS));
		
		
        
        //HEADER FOR PANELDRONE
        JPanel pnlDroneHeader = new JPanel(new BorderLayout());
        pnlDroneHeader.setBorder(BorderFactory.createTitledBorder(""));
        
        pnlDroneHeader.add( new JLabel(" ID  "));
        pnlDroneHeader.add( new JLabel(" LOCATION       "));
        pnlDroneHeader.add( new JLabel(" STATE     "));
        
        pnlDroneHeader.setLayout(new BoxLayout(pnlDroneHeader, BoxLayout.X_AXIS));
        pnlDrone.add(pnlDroneHeader, BorderLayout.NORTH);
        
        
       
        
        pnlDroneInfo.setLayout(new BoxLayout(pnlDroneInfo, BoxLayout.Y_AXIS));
        //pnlDroneInfo.add(new JLabel("HELLO WORLD"));
        
        pnlDrone.add(pnlDroneInfo, BorderLayout.CENTER);
        
        /////////////////////////////////HEADER FOR PANELPACKAGE//////////////////////////////////
        
        pnlPackageProc.setBorder(BorderFactory.createTitledBorder("PROCESSING"));
        pnlPackageProc.setLayout(new BoxLayout(pnlPackageProc, BoxLayout.Y_AXIS));
        
        JPanel pnlPackageProcHeader = new JPanel(new BorderLayout());
        pnlPackageProcHeader.setBorder(BorderFactory.createTitledBorder(""));
        
        pnlPackageProcHeader.add( new JLabel(" ID   "));
        pnlPackageProcHeader.add( new JLabel(" FROM  "));
        pnlPackageProcHeader.add( new JLabel("   TO   "));
        
        //Used to store & display packages
        JPanel pnlPackageInfo = new JPanel(new BorderLayout());
        
        
        pnlPackage.add(pnlPackageInfo, BorderLayout.CENTER);
        pnlPackageInfo.setLayout(new BoxLayout(pnlPackageInfo, BoxLayout.X_AXIS));
       
        
        pnlPackageProcHeader.setLayout(new BoxLayout(pnlPackageProcHeader, BoxLayout.X_AXIS));
        pnlPackageProc.add(pnlPackageProcHeader,BorderLayout.NORTH);
		
        
        //IN TO DO
        
        pnlPackageTodo.setBorder(BorderFactory.createTitledBorder("TO DO"));
        pnlPackageTodo.setLayout(new BoxLayout(pnlPackageTodo, BoxLayout.Y_AXIS));
        
        JPanel pnlPackageTodoHeader = new JPanel(new BorderLayout());
        pnlPackageTodoHeader.setBorder(BorderFactory.createTitledBorder(""));
        pnlPackageTodoHeader.setLayout(new BoxLayout(pnlPackageTodoHeader, BoxLayout.X_AXIS));
        
        pnlPackageTodoHeader.add( new JLabel(" ID   "));
        pnlPackageTodoHeader.add( new JLabel(" FROM    "));
        pnlPackageTodoHeader.add( new JLabel(" TO    "));
        
        pnlPackageTodo.add(pnlPackageTodoHeader);
        
        //ADDEN VAN PANELS AAN PACKAGE
        pnlPackage.add(pnlPackageProc, BorderLayout.CENTER);
        pnlPackage.add(pnlPackageTodo, BorderLayout.SOUTH);
        
	    
	    
        //mainPanel.add(panel4, BorderLayout.SOUTH);
        
        
        
	    mainPanel.add(pnlDrone,BorderLayout.WEST);
        mainPanel.add(pnlPackage, BorderLayout.EAST);
        
        //panelPackages.setBorder(BorderFactory.createRaisedBevelBorder());      
        panel4.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        
        
        
        //addDrone(1);

        frame.add(mainPanel); 
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(300,500); //220
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    
	}

	public static void main(String[] args) { 
		new GUIAutopilot();
    }
	
	
	//In panel: 1 -> ID
	//          2 -> Location
	//          3 -> State
	//Wordt opgeslagen volgens i
	public void addDrone(Integer i) {
		JPanel drone = new JPanel();
		JLabel l = new JLabel(" " + i + " ");
		l.setOpaque(true);
		l.setBackground(Color.GREEN);
		drone.add(l,0);
		drone.add(new JLabel("        " + 1.1 + "         "), 1);
		drone.add(new JLabel("            " + "INIT" + "    "), 2);
		
		drone.setLayout(new BoxLayout(drone, BoxLayout.X_AXIS));
		pnlDroneInfo.add(drone, i);
	}
	
	public void changeJobDrone(OccupationEnum s, Integer index) {
		JLabel l = (JLabel) ((JPanel) pnlDroneInfo.getComponent(index)).getComponent(0);
		switch(s) {
		case FREE: {
			l.setBackground(Color.GREEN);
		}
		case PICKING_UP: {
			l.setBackground(Color.ORANGE);
		}
		case OCCUPIED: {
			l.setBackground(Color.RED);
		}
		}
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
	
	public void addToDo(int id, int fromA, int fromG, int toA, int toG) {
		JPanel toDo = new JPanel();
		toDo.add(new JLabel(id + "     "),0);
		toDo.add(new JLabel("" + fromA + "."+ fromG + "       "),1);
		toDo.add(new JLabel("   " + toA + "."+ toG +     "   "),2);
		
		
		toDo.setLayout(new BoxLayout(toDo, BoxLayout.X_AXIS));
		pnlPackageTodo.add(toDo, id);
	}
	
	public void addPackageProc(int id) {
		JPanel l = ((JPanel) pnlPackageTodo.getComponent(id));
		pnlPackageTodo.remove(l);
		pnlPackageProc.add(l, id);
	}
	

	
}
