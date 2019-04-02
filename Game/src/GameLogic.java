import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import gne.Camera;
import gne.Game;
import gne.Node;
import gne.NodeGroup;
import gne.Player;
import gne.PlayerControl;
import gne.Texture;
import gne.World;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class GameLogic {
	GameWindow window;
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
	
	public GameLogic(GameWindow window) {
		this.window = window;
		canvas = window.canvas;
		rnd = new Random();
		renderer = new Renderer(canvas,this);
		renderer.setBackColor(new Color(0.05,0.1,0.15,1));
		camera = new Camera();
		initGame(new Player[] {new Player()});
	}
	public void initGame(Player[] player) {
		//world = Util.initWorld();
		world = new World("world.nwf");
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
		/*
		int attack = (int)(attackUnits>3?3:attackUnits);
		int defend = (int)(attackUnits>2?2:attackUnits);
		switch (attack) {
			case 0: return 0;
			case 1:
				switch (defend) {
				case 0: return 1;
				case 1: return 0.42f;
				case 2: return 0.26f;
				}
			case 2:
				switch (defend) {
				case 0: return 1;
				case 1: return 0.58f;
				case 2: return 0.23f;
			}
			case 3:
				switch (defend) {
				case 0: return 1;
				case 1: return 0.66f;
				case 2: return 0.37f;
			}
		}
		return 0;
		*/
		 
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
		//renderer.render();
	}
	public void initRound() {
		selectNode(null);
		gamePhase = 0;
		this.activePlayer = game.getActivePlayer();
		if (activePlayer!=null) {
			window.dLpc.setFill(activePlayer.getColor());
			window.dMpc.setFill(activePlayer.getColor());
			window.dRpc.setFill(activePlayer.getColor());
			window.tbName.setText(activePlayer.getName());
		}
		selectetUnits = initUnits(game.getActivePlayer());
		if (game.getActivePlayer().getControl() == PlayerControl.Computer)computerMove();
	}
	public int initUnits(Player player) {
		if (player == null)return 0;
		int selectetUnits = (int)(world.getNumberOfNodesOwnedByPlayer(player)/3);
		NodeGroup[] groups = world.getNodeGroups();
		for (int i = 0;i<groups.length;i++)
			if (world.areNodesOwndedByPlayer(groups[i].getNodes(), player))
				selectetUnits+=groups[i].getUnits();
		if (selectetUnits < 3) selectetUnits = 3;
		return selectetUnits;
	}
	public void computerMove() {
		Player player = game.getActivePlayer();
		Player bigestEnemy = null;
		int maxPoints = 0;
		int[] list;
		for (int i = 0;i<players.length;i++) {
			int newPoints = initUnits(players[i])*4;
			Node[] nodes = world.getNodes();
			for (int in = 0;in<nodes.length;in++) {
				if (nodes[in].getOwner()==players[i]) {
					newPoints+=nodes[in].getUnits();
				}
			}
			if (players[i]!=player && newPoints>maxPoints) {
				bigestEnemy = players[i];
				maxPoints = newPoints;
			}
		}
		//System.out.println(player.getName() +" / "+bigestEnemy.getName() + "("+maxPoints+")");
		
		// phase 1 distribute new units
		list = randomIntList(world.getNodes().length);
		for (int i = 0;i<5;i++) {
			for (int in = 0;in<list.length;in++) {
				Node node = world.getNodes()[list[in]];
				switch(i) {
				case 0: // add 1 unit to unowned neighbor node
					if (node.getUnits() <= 1 && node.getOwner() == player && world.areNodesContainsPlayer(node.getConnections(), null)) {
						addUnits(node,1);
						in = list.length;
					}
					break;
				case 1:// add devend units to threatened node
					if (node.getOwner() == player && node.getUnits() < maxEnemyUnits(node) && world.areNodesContainsPlayer(node.getConnections(), bigestEnemy)) {
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
					if (!world.areNodesOwndedByPlayer(node.getConnections(), player)) {	
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
					if (world.areNodesContainsPlayer(node.getConnections(), null)) {	
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
									if (node2.getOwner() == player && !world.areNodesOwndedByPlayer(node2.getConnections(), player)) {
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
		
/*
		final Duration timeout = Duration.ofSeconds(30);
		ExecutorService executor = Executors.newSingleThreadExecutor();

		@SuppressWarnings("unchecked")
		final Future<String> handler = executor.submit(new Callable() {
		    @Override
		    public Number call() {
		    	nextRound();
		    	return 0;
		    }
		});

		try {
			handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
		    handler.cancel(true);
		}

		executor.shutdownNow();
		*/
		/*
		Timer effectTimer = new Timer();
		effectTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				
			}
		}, 200);
		*/
		nextRound();



		
	}
	public void click (boolean pMbt,boolean sMbt,boolean mMbt) {
		//if (camera.moving) return;
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
