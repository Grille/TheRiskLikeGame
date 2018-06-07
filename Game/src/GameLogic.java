import java.util.Random;

import gne.Camera;
import gne.Game;
import gne.Node;
import gne.Player;
import gne.Renderer;
import gne.Texture;
import gne.World;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class GameLogic {
	Canvas canvas;
	
	public 	Player[] players;
	public 	Camera camera;
	public Game game;
	Node selectetNode;
	Player activePlayer;
	int seletetUnits = 0;
	
	int gamePhase = 0;
	
	public GameLogic(Canvas canvas) {
		Renderer renderer = new Renderer(canvas);
		renderer.setBackColor(new Color(0.05,0.1,0.15,1));
		World world = Util.initWorld();
		players = initPlayer();
		camera = new Camera();
		game = new Game(world,players);
		renderer.setRenderSource(game,camera);
		camera.centerWorld();
		renderer.startRendering();
		
		Random rnd = new Random();
		
		for (int i = 0;i< world.getNodes().length;i++) {
			float result = rnd.nextFloat();
			//if (result<0.5f)nodes[i].owner = null;
			if (result<0.5f)world.getNodes()[i].setOwner(players[0]);
			else world.getNodes()[i].setOwner(players[1]);
		}
	}
	private Player[] initPlayer(){
		return new Player[]{
			new Player("player1",Color.RED),
			new Player("player1",Color.BLUE)
		};
	}
	public void click (boolean pMbt,boolean sMbt,boolean mMbt) {
    	if (pMbt) {
    		Node node = camera.getNearestNode(70);
    		if (node == null) {
    			selectNode(null);
    		}
    		else {
				if (selectetNode == node) {
					if (node.units - seletetUnits > 1)
					seletetUnits++;
				}
				else if(node.getOwner() == game.getActivePlayer()){
					selectNode(node);
					seletetUnits=1;
				}
				else {
					selectNode(null);
				}
    		}
    	}
    	if (mMbt);
    	if (sMbt) {
			System.out.println("s");
    		Node node = camera.getNearestNode(100);
    		if (selectetNode != null && node != null) {
    			for (int i = 0;i<selectetNode.getConnections().length;i++) {
    				if (selectetNode.getConnections()[i] == node) {
    					sendUnitsToNode(selectetNode,node);
    					seletetUnits = 0;
    					return;
    				}
    			}      			
    		}
    	}
	}
	private void sendUnitsToNode(Node src,Node dst) {
		if (src.units <= 1) return;
		
		if (dst.getOwner() == null) {
			dst.units = seletetUnits;src.units-=seletetUnits;dst.setOwner(src.getOwner());  
		}
		else if (dst.getOwner() == src.getOwner()) {
			dst.units += seletetUnits;src.units-=seletetUnits;
			game.nextPlayer();
			selectNode(null);
		}
		else {
			dst.units -= 1;src.units-=1;
			if (dst.units <= 0)dst.setOwner(null);
		}
		
	}
	private void selectNode(Node node) {
		if (selectetNode != null) {
			selectetNode.setRenderOption(0);
			for (int i = 0;i<selectetNode.getConnections().length;i++) {
				selectetNode.getConnections()[i].setRenderOption(0);
			}
		}
		selectetNode = node;
		if (node != null) {
			selectetNode.setRenderOption(1);
			for (int i = 0;i<selectetNode.getConnections().length;i++) {
				selectetNode.getConnections()[i].setRenderOption(2);
			}
		}
	}
}
