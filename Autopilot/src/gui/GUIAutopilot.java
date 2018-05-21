package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import autopilotLibrary.CommunicatieTestbed;
import enums.PhaseEnum;
import interfaces.AutopilotOutputs;
import util.Vector;

public class GUIAutopilot {
	
	private CommunicatieTestbed com;
	
	JPanel mainPanel = new JPanel(new BorderLayout());
	
    JPanel pnlDrone = new JPanel(new FlowLayout());
    JPanel pnlDroneHeader = new JPanel(new BorderLayout());
    JPanel pnlDroneInfo = new JPanel(new BorderLayout());
    JPanel pnlButton = new JPanel(new BorderLayout());
    
    JLabel lblLIncl = new JLabel();
    JLabel lblRIncl = new JLabel();
    JLabel lblHIncl = new JLabel();
    JLabel lblVIncl = new JLabel();
    JLabel lblState = new JLabel();
    JLabel lblInfo  = new JLabel();
    JLabel lblCube  = new JLabel();
    
    JButton btnStab = new JButton("STAB");
    JButton btnHeading = new JButton("HEADING");
    
    Font font = new Font("Verdana", Font.BOLD, 20);
    
    JPanel mainDraw = new JPanel(new BorderLayout());
    
    
    
    HashMap<Integer,JLabel> drones = new HashMap<Integer,JLabel>();
    
    JFrame frame = new JFrame();
	
	public GUIAutopilot(CommunicatieTestbed t) {
		this.com = t;
		
		mainPanel.add(pnlDrone);
		mainPanel.add(pnlButton, BorderLayout.SOUTH);
		
		//pnlDrone.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(),"DRONE"));
		pnlDrone.setLayout(new BoxLayout(pnlDrone, BoxLayout.X_AXIS));
        
        //HEADER FOR PANELDRONE
        
        //pnlDroneHeader.setBorder(BorderFactory.createTitledBorder(""));
		addToHeader("LEFTINCL :");
		addToHeader("RIGHTINCL:");
		addToHeader("HORSTAB  :");
		addToHeader("VERSTAB  :");
		addToHeader("STATE    :");
		addToHeader("INFO     :");
		addToHeader("Cube     :");
		
        
        pnlDroneHeader.setLayout(new BoxLayout(pnlDroneHeader, BoxLayout.Y_AXIS));
        pnlDrone.add(pnlDroneHeader, BorderLayout.WEST);
        
        lblLIncl.setFont(font);
        lblRIncl.setFont(font);
        lblHIncl.setFont(font);
        lblVIncl.setFont(font);
        lblState.setFont(font);
        lblInfo.setFont(font);
        lblCube.setFont(font);
        
        lblLIncl.setText("INIT");
        lblRIncl.setText("INIT");
        lblHIncl.setText("INIT");
        lblVIncl.setText("INIT");
        lblState.setText("INIT");
        lblInfo.setText("INIT");
        lblCube.setText("INIT");
        
        pnlDroneInfo.add(lblLIncl);
        pnlDroneInfo.add(lblRIncl);
        pnlDroneInfo.add(lblHIncl);
        pnlDroneInfo.add(lblVIncl);
        pnlDroneInfo.add(lblState);
        pnlDroneInfo.add(lblInfo);
        pnlDroneInfo.add(lblCube);
        
       
        
        pnlDroneInfo.setLayout(new BoxLayout(pnlDroneInfo, BoxLayout.Y_AXIS));
        //pnlDroneInfo.add(new JLabel("HELLO WORLD"));
        
        pnlDrone.add(pnlDroneInfo, BorderLayout.CENTER);
        
        /////////BUTTONS//////////////////////
        pnlButton.setLayout(new BoxLayout(pnlButton, BoxLayout.X_AXIS));
        
        pnlButton.add(btnStab);
        pnlButton.add(btnHeading);
        
        
        btnStab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnHeading.setOpaque(false);
				setStab();
				btnStab.setOpaque(true);
				btnStab.setBackground(Color.GREEN);
			}
       
         });
        
        btnHeading.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setHeading();
				btnStab.setOpaque(false);
				btnHeading.setOpaque(true);
				btnHeading.setBackground(Color.GREEN);
			}          
         });
        
        
        //addDrone(1);

        frame.add(mainPanel); 
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(400,500); //220
        frame.setLocation(10, 10);
        frame.setVisible(true);
        frame.setResizable(false);
    
	}

	public static void main(String[] args) { 
		new GUIAutopilot(new CommunicatieTestbed());
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
	
	public void setState(String p) {
		lblState.setText(p.toString());
	}

	public void setInfo(String info) {
		lblInfo.setText(info);
	}
	
	public void setCube(Vector v) {
		lblCube.setText(v.x + " " + v.y + " " + v.z);
	}
	
	public void setStab() {
		com.setStab();
	}
	
	public void setHeading() {
		com.setHeading();
	}
	

	
}
