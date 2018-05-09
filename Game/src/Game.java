import gne.assets.*;
import gne.*;

import java.io.File;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class Game extends Application{
	NodeEngine nodeEngine;
	Player[] players;
	Image img;
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		nodeEngine = new NodeEngine(primaryStage);
		
		loadData();
		initPlayer();

		nodeEngine.setWorld(initWorld());
		nodeEngine.showWindow();
	}	
	
	private void initPlayer(){
		players = new Player[]{
			new Player("player1",Color.BLUE),
			new Player("player1",Color.RED)
		};
	}
	
	private void loadData(){
    	String curPath = "file:"+this.getClass().getClassLoader().getResource("").getPath()+"\\..\\";
    	img = new Image(curPath + "data/png/test.png");	
	}
	
	private World initWorld() {
		World world = new World();
	
		Node[] nodes = new Node[]{
			new Node(000,000,"jaytown"),
			new Node(400,130,"lulabi"),
			new Node(076,320,"cricity"),
			new Node(830,000,"darmstown"),
			new Node(460,290,"hillis"),
			new Node(760,390,"noob")
		};

		nodes[1].conectWithNodes(new Node[] {nodes[0],nodes[2],nodes[3],nodes[4]});
		nodes[4].conectWithNodes(new Node[] {nodes[5]});
		nodes[0].owner = nodes[1].owner = nodes[2].owner = nodes[3].owner = players[0];
		nodes[4].owner = nodes[5].owner = players[1];
		
		world.addNodes(nodes);

		
		for (int i = 0;i<100;i++)
		world.addDeco(new WorldObject(i,0,i,img), 0);
	
		world.finalProcessing();
		return world;
	}
	
}
