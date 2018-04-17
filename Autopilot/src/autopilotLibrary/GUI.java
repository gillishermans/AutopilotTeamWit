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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUI extends Application {
	
	Group root = new Group();
	
	private Stage primaryStage;
	
	private int yPos = 20;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Autopilot");
        Canvas canvas = new Canvas(300, 400);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
	}
	
	
	public static void main(String args[]){           
		launch(args);      
	}
	
	public void draw() {
		//drawArc(200,200,70,70,90,90);
		//drawCube(200,200,10);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		Line line = new Line();
		line.setStartX(x1); 
		line.setStartY(y1); 
		line.setEndX(x2); 
		line.setEndY(y2);
		root.getChildren().add(line);
	}

	public void addPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		
	}

	public void addDrone(Integer index) {
		Text txt = new Text(10,yPos,"Drone " + index);
		//root.getChildren().add(txt);
		yPos = yPos + 20;
		update(txt);
	}
	
	public void addToRoot(Node node) {
		root.getChildren().add(node);
	}
	
	
	public void update(Node node) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	try {
		    		root.getChildren().add(node);
		    		primaryStage.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}


}
