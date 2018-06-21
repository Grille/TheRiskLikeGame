import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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
		rnd = new Random();
		renderer = new Renderer(canvas,this);
		renderer.setBackColor(new Color(0.05,0.1,0.15,1));
		camera = new Camera();
		initGame(new Player[] {new Player()});
	}
	public void initGame(Player[] player) {
		world = Util.initWorld();
		players = player;
		game = new Game(world,players);
		renderer.setRenderSource(game,camera);
		camera.centerWorld();
		int sa = world.getNodes().length/players.length;
		for (int i = 0;i<1;i++) {
			for (int ip = 0;ip<players.length;ip++) {
				if (player[ip].getControl() == PlayerControl.Empty) continue;
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
		initRound();
	}

	int[] randomIntList(int max) {
		int[] ret = new int[max];
		for (int i = 0;i< ret.length;i++) {
			ret[i] = -1;
		}
		ret[0] = (int) (Math.random()*max);
		for (int i = 1;i<max;i++) {
			boolean run = true;
			while (run) {
				run = false;
				int newValue = (int) (Math.random()*max);
				for (int i2 = 0;i2<max;i2++) {
					if (ret[i2]==newValue) run = true;
				}
				ret[i] = newValue;
			}
		}
		return ret;
	}
	int maxEnemyUnits(Node node) {
		return maxEnemyUnits(node,null,true);
	}
	int maxEnemyUnits(Node node,Node not,boolean future) {
		Node[] connections = node.getConnections();
		Node node2 = connections[(int) (connections.length*Math.random())];
		int maxEnemyUnits = 0;
		for (int ic = 0;ic<connections.length;ic++) {
			int enemyUnits = connections[ic].getUnits();
			if (future)enemyUnits+=initUnits(connections[ic].getOwner());
			if (connections[ic] != not && connections[ic] != node2 && connections[ic].getOwner() != game.getActivePlayer() && maxEnemyUnits < enemyUnits)
				maxEnemyUnits = enemyUnits;
		}
		return maxEnemyUnits;
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
		initRound();
	}
	public void initRound() {	
		selectNode(null);
		gamePhase = 0;
		
		selectetUnits = initUnits(game.getActivePlayer());
		if (game.getActivePlayer().getControl() == PlayerControl.Computer)computerMove();
	}
	public int initUnits(Player player) {
		if (player == null)return 0;
		int selectetUnits = (int)(world.getNumberOfNodesOwnedByPlayer(player)/3);
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("na"), player)) selectetUnits+=5;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("sa"), player)) selectetUnits+=2;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("eu"), player)) selectetUnits+=5;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("af"), player)) selectetUnits+=3;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("as"), player)) selectetUnits+=7;
		if (world.isNodesOwndedByPlayer(world.getNodeGroup("au"), player)) selectetUnits+=2;
		if (selectetUnits < 3) selectetUnits = 3;
		return selectetUnits;
	}
	public void computerMove() {
		Player player = game.getActivePlayer();
		Player bigestEnemy = null;
		int units = 0;
		int[] list;
		for (int i = 0;i<players.length;i++) {
			int initUnits = initUnits(players[i]);
			if (players[i]!=player && initUnits>units) {
				bigestEnemy = players[i];
				units = initUnits;
			}
		}
		// phase 1 distribute new units
		list = randomIntList(world.getNodes().length);
		for (int i = 0;i<5;i++) {
			for (int in = 0;in<list.length;in++) {
				Node node = world.getNodes()[list[in]];
				switch(i) {
				case 0: // add 1 unit to unowned neighbor node
					if (node.getUnits() <= 1 && node.getOwner() == player && world.isNodesContainsPlayer(node.getConnections(), null)) {
						addUnits(node,1);
						in = list.length;
					}
					break;
				case 1:// add devend units to threatened node
					if (node.getOwner() == player && node.getUnits() < maxEnemyUnits(node) && world.isNodesContainsPlayer(node.getConnections(), bigestEnemy)) {
						addUnits(node,maxEnemyUnits(node)-node.getUnits()+1);
					}
					break;
				case 2:// add devend units to threatened node
					if (node.getOwner() == player && node.getUnits() < maxEnemyUnits(node)) {
						addUnits(node,maxEnemyUnits(node)-node.getUnits()+1);
					}
					break;
				case 3: // add remaining units to front node
					if (node.getOwner() == player && maxEnemyUnits(node) > 0) {
						addUnits(node,selectetUnits);
					}
					break;
				case 4: // add remaining units to random node
						addUnits(node,selectetUnits);
					break;
				}
			}
		}
		// phase 2 attack nodes
		list = randomIntList(world.getNodes().length);
		for (int i = 0;i<2;i++) {
			for (int in = 0;in<list.length;in++) {
				Node node = world.getNodes()[list[in]];
				if (node.getOwner() == game.getActivePlayer() ) {
					if (!world.isNodesOwndedByPlayer(node.getConnections(), player)) {	
						Node[] connections = node.getConnections();
						int[] listC = randomIntList(connections.length);
						for (int ic = 0;ic<connections.length;ic++) {
							Node node2 = connections[ic];
							int sendUnits = node2.getUnits();
							switch (i) {
							case 0: // risky attack dangerous player
								if (maxEnemyUnits(node2)>sendUnits)
									sendUnits = (int) (maxEnemyUnits(node2)*0.75f);
								if (node2.getOwner() == bigestEnemy && node.getUnits()>=node2.getUnits()) {
									if (node.getUnits()>node2.getUnits()) {
										selectNode(node);
										selectNodeUnits(node,sendUnits);
										sendUnitsToNode(node,node2);
									}
								}
								break;
							case 1: // save attack other player
								if (maxEnemyUnits(node2)>sendUnits)
									sendUnits = (int) (maxEnemyUnits(node2)*0.75f);
								if (node2.getOwner() != null && node2.getOwner() != player && maxEnemyUnits(node,node2,false) <= node.getUnits()-sendUnits) {
									if (node.getUnits()>node2.getUnits()) {
										selectNode(node);
										selectNodeUnits(node,sendUnits);
										sendUnitsToNode(node,node2);
									}
								}
								break;
							}
						}
					}
				}
			}
		}
		// phase 3 move units
		list = randomIntList(world.getNodes().length);
		for (int i = 0;i<3;i++) {
			for (int in = 0;in<list.length;in++) {
				Node node = world.getNodes()[list[in]];
				if (node.getOwner() == player) {
					if (world.isNodesContainsPlayer(node.getConnections(), null)) {	
						Node[] connections = node.getConnections();
						int[] listC = randomIntList(connections.length);
						for (int ic = 0;ic<connections.length;ic++) {
							Node node2 = connections[ic];
							switch (i) {
								case 0: // move to unowned node
									if (node2.getOwner() == null) {
										selectNode(node);
										selectNodeUnits(node,node.getUnits()/2);
										sendUnitsToNode(node,node2);
									}
								break;
								case 1: // move to front node
									if (node2.getOwner() == player && !world.isNodesOwndedByPlayer(node2.getConnections(), player)) {
										selectNode(node);
										selectNodeUnits(node,node.getUnits());
										sendUnitsToNode(node,node2);
									}
								break;
								case 2: // move to random node
									if (node2.getOwner() == player) {
										selectNode(node);
										selectNodeUnits(node,node.getUnits());
										sendUnitsToNode(node,node2);
									}
								break;
							}
						}
					}
				}
			}
		}
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
		if (units < 0) return;
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
