package gne.engine;
import java.io.File;
import java.util.*;

import java.awt.Point;

import gne.assets.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class Window {

	private Stage primaryStage;
	private Camera cam;
	private Group root;
	private Canvas canvas;
	private GraphicsContext ctx;
	
	private int oldX,oldY;

	private boolean pMouseDown,sMouseDown,mMouseDown;
	
	
	public Window(Stage primaryStage,Camera cam) {
		this.cam = cam;
		this.primaryStage = primaryStage;

		initWindow();
		addEvents();
	}
	
	public Group getRoot() {
		return root;
	}
	public void show() {
        primaryStage.show(); 
	}
	public void show(boolean fullscreen) {
        primaryStage.show();
        primaryStage.setFullScreen(fullscreen);
	}

	public GraphicsContext getGraphicsContext() {
		return ctx;
	}
	
	private void initWindow() {
        primaryStage.setTitle("Game");
        root = new Group();	
        canvas = new Canvas(1920, 1080);
        primaryStage.setScene(new Scene(root));
		ctx = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);  
        
	}
	private void addEvents() {
		canvas.setOnKeyPressed(e -> {
			//if (e.getCode() == KeyCode.LEFT) {
				cam.addPos(100,0);
			//}
		});
        canvas.setOnMouseDragged(e -> {
        	if (e.isPrimaryButtonDown()) {
        		cam.addPos(oldX-(int)e.getX(), oldY-(int)e.getY());
        	}
        	oldX = (int)e.getX();oldY=(int)e.getY();
        });
        canvas.setOnMouseMoved(e -> {
        	oldX = (int)e.getX();oldY=(int)e.getY();
        });
        
        canvas.setOnMousePressed(e -> {
        	if (e.isPrimaryButtonDown())pMouseDown = true;
        	if (e.isSecondaryButtonDown())pMouseDown = true;
        	if (e.isMiddleButtonDown())pMouseDown = true;
        });
        canvas.setOnScroll(e -> {
        	cam.addScale((float)e.getDeltaY());
        	if (cam.scale>10)cam.scale=10f;
        });
	}

}
