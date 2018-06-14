import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import gne.Camera;
import gne.Game;
import gne.Node;
import gne.Player;
import gne.PlayerControl;
import gne.Texture;
import gne.World;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class GameLogic {
	Canvas canvas;
	Renderer renderer;
	
	public 	Player[] players;
	public 	Camera camera;
	public Game game;
	Node selectetNode;
	Player activePlayer;
	World world;
	int selectetUnits = 0;
	
	boolean isShiftDown = false;
	
	int gamePhase = 0;
	
	Random rnd;
	
	public GameLogic(Canvas canvas) {
		renderer = new Renderer(canvas,this);
		renderer.setBackColor(new Color(0.05,0.1,0.15,1));
		world = Util.initWorld();
		players = initPlayer();
		camera = new Camera();
		game = new Game(world,players);
		renderer.setRenderSource(game,camera);
		camera.centerWorld();
		renderer.startRendering();
		
		rnd = new Random(110);

		//int world
		int sa = world.getNodes().length/players.length;
		for (int i = 0;i<1;i++) {
			for (int ip = 0;ip<players.length;ip++) {
				Node node;
				while (true) {
					node = world.getNodes()[(int)(Math.random()*world.getNodes().length)];
					boolean isAlone = true;
					if (node.getOwner() != null)isAlone = false;
					
					for (int ic = 0;ic<node.getConnections().length;ic++) {
						if (node.getConnections()[ic].getOwner() != null)isAlone = false;
					}
					
					if (isAlone) {
						break;
					}
				}
				node.setOwner(players[ip]);
				node.setUnits(1);
			}
		}
		world.saveMapScript("file:/../data/maps/test.nms");
		world.loadMapScript("file:/../data/maps/test.nms");
		nextRound();
	}
	private Player[] initPlayer(){
		return new Player[]{
			new Player("player1",Color.BLUE,PlayerControl.Human),
			new Player("player2",Color.RED,PlayerControl.Computer),
			new Player("player3",Color.GREEN,PlayerControl.Computer),
			new Player("player4",Color.YELLOW,PlayerControl.Computer),
			new Player("player5",Color.DEEPPINK,PlayerControl.Computer),
			new Player("player6",Color.TURQUOISE,PlayerControl.Computer)
		};
	}
	
	
	float attackCalc(float attackUnits,float defendUnits) {
		return (attackUnits/(attackUnits+defendUnits))*0.5f+0.25f;
	}
	public void move() {
		Node node = camera.getNearestNode(70);
		if (selectetNode == null || node == null)return;
		renderer.winChance = attackCalc(selectetUnits,node.getUnits());
	}
	
	public void nextRound() {	
		selectNode(null);
		game.nextPlayer();
		System.out.println(game.getActivePlayer().getName());
		gamePhase = 0;
		
		selectetUnits = (int)(world.getNumberOfNodesOwnedByPlayer(game.getActivePlayer())/3);
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("na"), game.getActivePlayer())) selectetUnits+=5;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("sa"), game.getActivePlayer())) selectetUnits+=2;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("eu"), game.getActivePlayer())) selectetUnits+=5;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("af"), game.getActivePlayer())) selectetUnits+=3;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("as"), game.getActivePlayer())) selectetUnits+=7;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("au"), game.getActivePlayer())) selectetUnits+=2;
		if (selectetUnits < 3) selectetUnits = 3;
		
		if (game.getActivePlayer().getControl() == PlayerControl.Computer)computerMove();
	}
	public void computerMove() {
		int tryNumber = 0;
		while (gamePhase == 0) {
			Node node = world.getNodes()[(int) (world.getNodes().length*Math.random())];
			if (!world.isNodesOwndedByPlayer(node.getConnections(), game.getActivePlayer())|| tryNumber < 100)
			addUnits(node,selectetUnits);
		}
		
		for (int i = 0;i<10;i++) {
			for (int iw = 0;iw<world.getNodes().length;iw++) {
				Node node = world.getNodes()[iw];
				Node[] connections = node.getConnections();
				if (node.getOwner() == game.getActivePlayer()) {
					Node node2 = connections[(int) (connections.length*Math.random())];
					int maxEnemyUnits = 0;
					for (int ic = 0;ic<connections.length;ic++) {
						if (connections[ic] != node2 && connections[ic].getOwner() != game.getActivePlayer() && maxEnemyUnits < connections[ic].getUnits())maxEnemyUnits = connections[ic].getUnits();
					}
					if (node2.getOwner() != null && node2.getOwner() != game.getActivePlayer()) {
						selectNode(node);
						selectNodeUnits(node,(int)(node.getUnits()-(maxEnemyUnits+1)));
						if (node2.getOwner() == game.getActivePlayer()||node2.getUnits() <= selectetUnits)
							sendUnitsToNode(node,node2);
					}
				}
			}
		}
		
		
		/*
		while (gamePhase == 1) {
			Node node = world.getNodes()[(int) (world.getNodes().length*Math.random())];
			if (!world.isNodesOwndedByPlayer(node.getConnections(), game.getActivePlayer()))
			addUnits(node,selectetUnits);
		}
		*/
		tryNumber = 0;
		while (gamePhase == 1 && tryNumber < 100) {
			tryNumber++;
			Node node = world.getNodes()[(int) (world.getNodes().length*Math.random())];
			Node[] connections = node.getConnections();
			if (node.getOwner() == game.getActivePlayer()) {
				Node node2 = connections[(int) (connections.length*Math.random())];
				int maxEnemyUnits = 2;
				for (int ic = 0;ic<connections.length;ic++) {
					if (connections[ic] != node2 && connections[ic].getOwner() != game.getActivePlayer() && maxEnemyUnits < connections[ic].getUnits())maxEnemyUnits = connections[ic].getUnits();
				}
				if (node2.getOwner() == null) {
					selectNode(node);
					selectNodeUnits(node,(int)(node.getUnits()-maxEnemyUnits));
					sendUnitsToNode(node,node2);
				}
			}
		}
		tryNumber = 0;
		while (gamePhase == 1 && tryNumber < 100) {
			tryNumber++;
			Node node = world.getNodes()[(int) (world.getNodes().length*Math.random())];
			if (node.getOwner() == game.getActivePlayer() && world.isNodesOwndedByPlayer(node.getConnections(), game.getActivePlayer())) {
				Node node2 = node.getConnections()[(int) (node.getConnections().length*Math.random())];
				selectNode(node);
				selectNodeUnits(node,(int)(node.getUnits()));
				sendUnitsToNode(node,node2);
			}
		}
		/*

		*/
		nextRound();
	}
	public void click (boolean pMbt,boolean sMbt,boolean mMbt) {
		Node node = camera.getNearestNode(70);
		if (gamePhase == 0) {
			if (pMbt || sMbt) {
				if (isShiftDown) addUnits(node,5);
				else addUnits(node,1);
			}
		}
		else if (gamePhase == 1){
	    	if (pMbt) {
				selectNode(node);
				if (isShiftDown) selectNodeUnits(node,5);
				else  selectNodeUnits(node,1);
	    	}
	    	if (mMbt);
	    	if (sMbt) {
    			sendUnitsToNode(selectetNode,node);
	    	}
		}
	}
	private void addUnits(Node node,int units) {
		if (node != null && node.getOwner() == game.getActivePlayer()) {
			if (selectetUnits >= units) {
				node.addUnits(units);
				selectetUnits-=units;
			}
			else {
				node.addUnits(selectetUnits);
				selectetUnits=0;
			}
			if (selectetUnits <= 0) {
				gamePhase = 1;
			}
		}
	}
	private void selectNodeUnits(Node node,int units) {
		if (gamePhase != 1 || node != null && node.getOwner() == game.getActivePlayer()) {
			if (units < 1) units = 1;
			if (node.getUnits() - selectetUnits > units) {
				selectetUnits+=units;
			}
			else {
				selectetUnits=node.getUnits()-1;
			}
		}
	}
	private void sendUnitsToNode(Node src,Node dst) {
		if (gamePhase != 1 || src == null ||dst == null) return;
		if (src.getOwner() != game.getActivePlayer())return;
		
		if (dst == src) {
			System.out.println("self");
			if (selectetUnits >= 1)selectetUnits--;
			return;
		}
		if (src.getUnits() <= 1 || !src.isConectetWidthNode(dst))return;
		
		if (dst.getOwner() == null) {
			dst.setUnits(selectetUnits);
			src.addUnits(-selectetUnits);
			dst.setOwner(src.getOwner());  
			selectetUnits = 0;
			gamePhase = 2;
		}
		else if (dst.getOwner() == src.getOwner()) {
			dst.addUnits(selectetUnits);
			src.addUnits(-selectetUnits);
			gamePhase = 2;
			selectetUnits = 0;
		}
		else {
			src.addUnits(-selectetUnits);
			while (selectetUnits > 0) {
				if (rnd.nextFloat() > attackCalc(selectetUnits,dst.getUnits())) selectetUnits-=1;
				else dst.addUnits(-1);
				if (dst.getUnits() <= 0) {
					dst.setOwner(src.getOwner());
					dst.setUnits(selectetUnits);
					selectetUnits = 0;
					break;
				}
			}
		}
		
	}
	private void selectNode(Node node) {
		if (node == selectetNode)return;
		if (node == null || node.getOwner() == game.getActivePlayer()){
			selectetNode = node;
			selectetUnits=0;
		}
	}
}
