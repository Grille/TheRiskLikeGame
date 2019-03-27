package gne;

import java.util.*;

public class Game {
	World world;
	Player[] player;
	int activePlayer;
	int localPlayer;
	int currentRound;
	
	Timer gameTimer;
	boolean running;
	
	public Game(World world,Player[] player) {
		this.world = world;this.player = player;
		for (int i = 0;i<player.length;i++)player[i].id = i;
		activePlayer = 0;
		currentRound = 1;
		gameTimer = new Timer();
		gameTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (running) {
					tick();
				}
			}
		}, 10,10);
		
	}

	public void tick() {

	}
	public void fight() {
		
	}
	
	public void start() {
		running=true;
	}
	public void stop() {
		running=false;
	}
	
	public World getWorld() {
		return world;
	}
	public Player[] getPlayer() {
		return player;
	}
	
	public void nextPlayer() {
		if (activePlayer == 0) currentRound++;
		if (activePlayer < player.length-1) activePlayer++;
		else activePlayer = 0;
		if (world.getNumberOfNodesOwnedByPlayer(player[activePlayer]) == 0 || player[activePlayer].controlTyp == PlayerControl.Empty) nextPlayer();
	}
	
	public Player getActivePlayer() {
		return player[activePlayer];
	}
	public void setActivePlayer(Player player) {
		activePlayer = player.id;
	}
	
	public Player getLocalPlayer() {
		return player[localPlayer];
	}
	public void setLocalPlayer(Player player) {
		localPlayer = player.id;
	}
	public int getCurrentRound() {
		return currentRound;
	}
	
}
