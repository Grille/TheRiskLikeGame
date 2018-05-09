package gne.assets;

import javafx.scene.image.Image;

public class Node extends WorldObject{
	
	public String name;
	public Player owner;
	private Node[] connections;
	private Building[] buildings;

	public Node(int posX,int posY) {
		this(posX,posY,"");
	}
	public Node(int posX,int posY,Image img) {
		super(posX,posY,img);
		this.connections = new Node[10];
		this.name = "";
	}
	public Node(int posX,int posY,String name) {
		super(posX,posY);
		this.connections = new Node[10];
		this.name = name;
	}

	public Node[] getConnections() {return connections;}
	
	private void conectToNode(Node refNode) {
		int iConect = 0;
		for (int i = 0;i<iConect;i++) if (connections[iConect]==refNode)return;
		connections[iConect++] = refNode;
	}
	public void conectWithNode(Node refNode) {
		this.conectToNode(refNode);
		refNode.conectToNode(this);
	}
	public void conectWithNodes(Node[] refNodes) {
		connections = new Node[refNodes.length+1];
		for (int i = 0;i<refNodes.length;i++) {
			conectWithNode(refNodes[i]);
		}
	}
}
