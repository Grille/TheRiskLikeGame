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
	
	private Window window;
	private Renderer renderer;
	private Camera cam;
	private World world;
	
	int inde = 0;
	
	Image img;	
	
	public void showWindow() {
		window.show();
		renderer.startRenderTimer(world, cam);
	}
	public void setWorld(World world) {
		this.world = world;
	}
	public NodeEngine(Stage primaryStage)  {
		String curPath = "file:"+this.getClass().getClassLoader().getResource("").getPath()+"\\..\\";
		
		cam = new Camera(primaryStage,0,0,1f);
		window = new Window(primaryStage,cam);
		renderer = new Renderer(primaryStage,window.getGraphicsContext());
	
		
	}

}
