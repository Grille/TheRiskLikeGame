package gne;
import java.io.File;
import java.io.IOException;
import java.util.*;

import gne.engine.*;
import gne.assets.*;

import javafx.application.Application;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class NodeEngine{
	/*
	static Timer renderTimer = new Timer();
	TimerTask task = new TimerTask() {public void run() {}};
	renderTimer.schedule(task , 10,20);
	*/
	
	Window window;
	Renderer renderer;
	Camera camera;
	World world;
	Image images;
	int inde = 0;
	
	Image img;	
	
	static Stage primaryStage;
	//static 
	
	public void setWorld(World world) {
		renderer.world = camera.world = this.world = world;
	}
	public World getWorld() {return world;}
	public Renderer getRenderer() {return renderer;}
	public Camera getCamera() {return camera;}
	public Window getWindow() {return window;}
	public NodeEngine(Stage primaryStage)  {
		String curPath = this.getClass().getClassLoader().getResource("").getPath()+"/../";
		NodeEngine.primaryStage = primaryStage;
		camera = new Camera(primaryStage,0,0,1f);
		window = new Window(primaryStage,camera);
		renderer = new Renderer(primaryStage,window.getGraphicsContext());
		renderer.cam = camera;
	}

}
