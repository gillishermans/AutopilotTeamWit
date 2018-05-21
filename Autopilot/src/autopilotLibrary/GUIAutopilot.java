package autopilotLibrary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import enums.DeliveryEnum;
import enums.OccupationEnum;
import enums.PhaseEnum;

public class GUIAutopilot {
    	
    	JPanel mainPanel = new JPanel(new BorderLayout());	
        JPanel dronePanel = new JPanel(new FlowLayout());  
        JPanel packagePanel = new JPanel(new FlowLayout());
        
        HashMap<Integer,JLabel> drones = new HashMap<Integer,JLabel>();
        
        JFrame frame = new JFrame();
    	
    	public GUIAutopilot() {
    		
    		dronePanel = new JPanel();
    		dronePanel.setLayout(new BoxLayout(dronePanel, BoxLayout.Y_AXIS));
    		dronePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"DRONE"));
    		
    		packagePanel = new JPanel();
    		packagePanel.setLayout(new BoxLayout(packagePanel, BoxLayout.Y_AXIS));
    		packagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"PACKAGE"));
    		
            JPanel pnlDroneHeader = new JPanel(new BorderLayout());
            pnlDroneHeader.setBorder(BorderFactory.createTitledBorder(""));
            
            pnlDroneHeader.add( new JLabel(" ID  "));
            //pnlDroneHeader.add( new JLabel(" LOCATION       "));
            pnlDroneHeader.add( new JLabel(" STATE     "));
            
            pnlDroneHeader.setLayout(new BoxLayout(pnlDroneHeader, BoxLayout.X_AXIS));
            dronePanel.add(pnlDroneHeader);
            
            JPanel pnlPackageInfoHeader = new JPanel(new BorderLayout());
            pnlPackageInfoHeader.setBorder(BorderFactory.createTitledBorder(""));
            
            pnlPackageInfoHeader.add( new JLabel(" ID   "));
            pnlPackageInfoHeader.add( new JLabel(" FROM  "));
            pnlPackageInfoHeader.add( new JLabel("   TO   "));
            pnlPackageInfoHeader.add( new JLabel(" DRONE "));
            
            pnlPackageInfoHeader.setLayout(new BoxLayout(pnlPackageInfoHeader, BoxLayout.X_AXIS));
            packagePanel.add(pnlPackageInfoHeader);
    		
    		mainPanel.add(dronePanel,BorderLayout.WEST);
    		mainPanel.add(packagePanel,BorderLayout.EAST);
    		
    		frame.setLayout(new BorderLayout());
    		frame.getContentPane().add(mainPanel);
            //frame.add(mainPanel); 
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            frame.pack();
            frame.setSize(300,500); //300
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(true);
        
    	}

    	public static void main(String[] args) { 
    		
    		
    		
    		GUIAutopilot g = new GUIAutopilot();
    		
    		g.addPackage(0, 1, 0, 0, 0);
    		g.addPackage(1, 1, 0, 0, 0);
    		
    		g.addDrone(0);
    		g.addDrone(1);
    		g.changeJobDrone(OccupationEnum.PICKING_UP, 0);
    		g.changeStateDrone(PhaseEnum.KUBUS, 1);
    		
    		g.changeStatePackage(DeliveryEnum.TAKEN, 1, 1);
    		g.completeDelivery(0);

    		g.mainPanel.revalidate();
    	    g.mainPanel.repaint();


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
    		//drone.add(new JLabel("        " + 1.1 + "         "), 1);
    		drone.add(new JLabel("            " + "INIT" + "    "), 1);
    		
    		drone.setLayout(new BoxLayout(drone, BoxLayout.X_AXIS));
    		dronePanel.add(drone);
    	}
    	
    	public void changeJobDrone(OccupationEnum s, Integer index) {
    		JLabel l = (JLabel) ((JPanel) dronePanel.getComponent(index+1)).getComponent(0);
    		switch(s) {
    		case FREE: {
    			l.setBackground(Color.GREEN);
    			break;
    		}
    		case PICKING_UP: {
    			l.setBackground(Color.ORANGE);
    			break;
    		}
    		case DELIVERING: {
    			l.setBackground(Color.RED);
    			break;
    		}
    		}
    	}
    	
    	public void changeLocationDrone(Integer index){
    		JLabel l = (JLabel) ((JPanel) dronePanel.getComponent(index+1)).getComponent(0);
    	}
    	
    	public void changeStateDrone(PhaseEnum s, Integer index) {
    		JLabel l = (JLabel) ((JPanel) dronePanel.getComponent(index+1)).getComponent(1);
    		switch(s) {
    		case TEST: {
    			l.setText("     " + "TEST" + " ");
    			break;
    		}
    		case INIT: {
    			l.setText("            " + "INIT" + "    ");
    			break;
    		}
    		case WAITING: {
    			l.setText("      " + "WAITING" + "    ");
    			break;
    		}
    		case NOODREM: {
    			l.setText("      " + "NOODREM" + "    ");
    			break;
    		}
    		case DRAAIEN: {
    			l.setText("      " + "TURNING" + "    ");
    			break;
    		}
    		case REMMEN: {
    			l.setText("      " + "BRAKING" + "    ");
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
    			l.setText("  " + "STABILIZE" + " ");
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
    		case VLIEGEN: {
    			l.setText("        " + "FLYING" + " ");
    			break;
    		}
    		default: {
    			l.setText("         " + "FLYING" + " ");
    		}
    		}
    	}
    	
    	public void addPackage(int id, int fromA, int fromG, int toA, int toG){
    		System.out.println("ADD PACKAGE");
    		System.out.println("ID " + id);
    		JPanel pack = new JPanel();
    		
    		JLabel l = new JLabel(" " + id + " ");
    		l.setOpaque(true);
    		l.setBackground(Color.GREEN);
    		pack.add(l,0);
    		pack.add(new JLabel("    " + fromA + "."+ fromG + "       "),1);
    		pack.add(new JLabel("   " + toA + "."+ toG +     "   "),2);
    		pack.add(new JLabel("      /        "),3);
    		
    		pack.setLayout(new BoxLayout(pack, BoxLayout.X_AXIS));

    		//pack.setName(Integer.toString(id));
    		packagePanel.add(pack);
    		packagePanel.revalidate();
    		packagePanel.repaint();
    		mainPanel.revalidate();
    		mainPanel.repaint();

    	}
    	
    	public void changeStatePackage(DeliveryEnum s, Integer index, Integer drone) {
    		JLabel l = (JLabel) ((JPanel) packagePanel.getComponent(index+1)).getComponent(0);
    		switch(s) {
    		case OPEN: {
    			l.setBackground(Color.GREEN);
    			break;
    		}
    		case TAKEN: {
    			l.setBackground(Color.ORANGE);
    			break;
    		}
    		}
    		
    		JLabel l2 = (JLabel) ((JPanel) packagePanel.getComponent(index+1)).getComponent(3);
    		l2.setText("      " + drone + "       ");
    	}

	public void completeDelivery(Integer indexPackage) {
		JLabel l = (JLabel) ((JPanel) packagePanel.getComponent(indexPackage+1)).getComponent(0);
		l.setBackground(Color.RED);
//		l.setText("<html><strike>" + l.getText() + "</strike></html>");
//		
//		JLabel l1 = (JLabel) ((JPanel) packagePanel.getComponent(indexPackage+1)).getComponent(1);
//		l1.setText("<html><strike>" + l1.getText() + "</strike></html>");
//		
//		JLabel l2 = (JLabel) ((JPanel) packagePanel.getComponent(indexPackage+1)).getComponent(2);
//		l2.setText("<html><strike>" + l2.getText() + "</strike></html>");
//		
//		JLabel l3 = (JLabel) ((JPanel) packagePanel.getComponent(indexPackage+1)).getComponent(3);
//		l3.setText("<html><strike>" + l3.getText() + "</strike></html>");
	}
	
}
