package gne;

public class Game {
	World world;
	Player[] player;
	int activePlayer;
	
	
	
	public Game(World world,Player[] player) {
		this.world = world;this.player = player;
		for (int i = 0;i<player.length;i++)player[i].id = i;
		activePlayer = 0;
	}
	public World getWorld() {
		return world;
	}
	public Player[] getPlayer() {
		return player;
	}
	
	public void nextPlayer() {
		if (activePlayer < player.length-1)activePlayer++;
		else activePlayer = 0;
		if (getNumberOfPlayerOwnedNodes(player[activePlayer]) == 0)nextPlayer();
	}
	public Player getActivePlayer() {
		return player[activePlayer];
	}
	public int getNumberOfPlayerOwnedNodes(Player player) {
		int result = 0;
		for (int i = 0;i<world.nodes.length;i++) {
			if (world.nodes[i].owner == player)result++;
		}
		return result;
	}
}
