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
		
		Random rnd = new Random();

		//int world
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
			new Player("player1",Color.BLUE),
			new Player("player2",Color.RED),
			new Player("player3",Color.GREEN),
			new Player("player4",Color.YELLOW),
			new Player("player5",Color.DEEPPINK),
			new Player("player6",Color.TURQUOISE)
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
		gamePhase = 0;
		
		selectNode(null);
		game.nextPlayer();
		
		selectetUnits = (int)(world.getNumberOfNodesOwnedByPlayer(game.getActivePlayer())/3);
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("na"), game.getActivePlayer())) selectetUnits+=5;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("sa"), game.getActivePlayer())) selectetUnits+=2;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("eu"), game.getActivePlayer())) selectetUnits+=5;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("af"), game.getActivePlayer())) selectetUnits+=3;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("as"), game.getActivePlayer())) selectetUnits+=7;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("au"), game.getActivePlayer())) selectetUnits+=2;
		if (selectetUnits < 3) selectetUnits = 3;
	}
	public void KI() {
		game.nextPlayer();
	}
	public void click (boolean pMbt,boolean sMbt,boolean mMbt) {
		Node node = camera.getNearestNode(70);
		if (gamePhase == 0) {
			if (pMbt || sMbt) {
				if (node != null && node.getOwner() == game.getActivePlayer()) {
					if (!isShiftDown) {
						node.addUnits(1);
						selectetUnits--;
					}
					else {
						if (selectetUnits >= 5) {
							node.addUnits(5);
							selectetUnits-=5;
						}
						else {
							node.addUnits(selectetUnits);
							selectetUnits=0;
						}
					}
					if (selectetUnits <= 0) {
						gamePhase = 1;
					}
				}
			}
		}
		else {
	    	if (pMbt) {
	    		if (node == null) {
	    			selectNode(null);
	    			selectetUnits=0;
	    		}
	    		else {
					if (selectetNode == node) {
						selectNodeUnits(node);
					}
					else if(node.getOwner() == game.getActivePlayer()){
						selectNode(node);
						selectNodeUnits(node);
					}
					else {
						selectNode(null);
					}
	    		}
	    	}
	    	if (mMbt);
	    	if (sMbt) {
	    		if (selectetNode != null && node != null) {
	        		if (selectetNode == node) {
        				if (!isShiftDown) selectetUnits--;
        				else {
        					if (selectetUnits >= 5) selectetUnits-=5;
        					else selectetUnits=0;
        				}
	        		}
	        		else {
	    				if (selectetNode.isConectetWidthNode(node)) {
	    					sendUnitsToNode(selectetNode,node);
	    				}
	        		}
	
	    		}
	    	}
		}
	}
	private void selectNodeUnits(Node node) {
		if (!isShiftDown) {
			if (node.getUnits() - selectetUnits > 1) selectetUnits++;
		}
		else {
			if (node.getUnits() - selectetUnits > 5) {
				selectetUnits+=5;
			}
			else {
				selectetUnits=node.getUnits()-1;
			}
		}
	}
	private void sendUnitsToNode(Node src,Node dst) {
		if (src.getUnits() <= 1) return;
		
		if (dst.getOwner() == null) {
			dst.setUnits(selectetUnits);
			src.addUnits(-selectetUnits);
			dst.setOwner(src.getOwner());  
			nextRound();
		}
		else if (dst.getOwner() == src.getOwner()) {
			dst.addUnits(selectetUnits);
			src.addUnits(-selectetUnits);
			nextRound();
		}
		else {
			src.addUnits(-selectetUnits);
			while (selectetUnits > 0) {
				if (Math.random() > attackCalc(selectetUnits,dst.getUnits())) selectetUnits-=1;
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
		selectetNode = node;
	}
}
