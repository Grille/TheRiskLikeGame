package gne;
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
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class Window {

	private Stage primaryStage;
	private Camera cam;
	private Group root;
	private Scene scene;
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
        if (fullscreen) {
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(fullscreen);
        } 
	}

	public GraphicsContext getGraphicsContext() {
		return ctx;
	}
	
	private void initWindow() {
        primaryStage.setTitle("Game");
        root = new Group();	
        canvas = new Canvas(1920, 1080);
        primaryStage.setScene(scene = new Scene(root));
		ctx = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);   
	}
	private void addEvents() {
		
		scene.setOnKeyPressed(e -> {
			System.out.println("fcghj");
			switch (e.getCode()) {
				case RIGHT:cam.addCamPos(100,0);break;
				case LEFT:cam.addCamPos(-100,0);break;
				case UP:cam.addCamPos(0,-100);break;
				case DOWN:cam.addCamPos(0,100);break;
				case Q:if(e.isControlDown())primaryStage.close();break;
			}
		});
		
        canvas.setOnMouseDragged(e -> {
        	if (e.isPrimaryButtonDown()) {
        		cam.addCamPos(oldX-(int)e.getX(), oldY-(int)e.getY());
        	}
        	oldX = (int)e.getX();oldY=(int)e.getY();
        });
        canvas.setOnMouseMoved(e -> {
        	oldX = (int)e.getX();oldY=(int)e.getY();
        });
        
        canvas.setOnMouseClicked(e -> {
        	if (e.isPrimaryButtonDown());
        	if (e.isSecondaryButtonDown());
        	if (e.isMiddleButtonDown());
        });
        canvas.setOnMousePressed(e -> {
        	if (e.isPrimaryButtonDown())pMouseDown = true;
        	if (e.isSecondaryButtonDown())pMouseDown = true;
        	if (e.isMiddleButtonDown())pMouseDown = true;
        });
        canvas.setOnScroll(e -> {
        	double posX = -cam.posX + (e.getX()-canvas.getWidth()/2)/cam.scale;
        	double posY = -cam.posY + (e.getY()-canvas.getHeight()/2)/cam.scale;
        	
        	cam.addCamScale((float)e.getDeltaY());
        	if (cam.scale>1)cam.scale=1f;
        	
        	cam.addPos(
        		(float)(cam.posX-(-posX +(canvas.getWidth()/2*(e.getX()/canvas.getWidth()*2-1))/cam.scale)),
        		(float)(cam.posY-(-posY +(canvas.getHeight()/2*(e.getY()/canvas.getHeight()*2-1))/cam.scale))/cam.tilt
        	);
        	

        });
	}

}
