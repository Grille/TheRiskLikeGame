package gne.assets;

import javafx.scene.image.Image;

public class Node extends WorldObject{
	
	public String name;
	public Player owner;
	private Node[] connections;
	private Building[] buildings;

	public Node(int posX,int posY) {this(posX,posY,null,"");}
	public Node(int posX,int posY,Image img) {this(posX,posY,img,"");}
	public Node(int posX,int posY,String name) {this(posX,posY,null,name);}
	
	public Node(int posX,int posY,Image img,String name) {
		super(posX,posY,img);
		this.connections = new Node[10];
		this.name = name;
	}

	public Node[] getConnections() {return connections;}
	
	private void conectToNode(Node refNode) {
		for (int i = 0;i<connections.length;i++) if (connections[i]==refNode)return;
		Node[] newConnections = new Node[connections.length+1];
		for (int i = 0;i<connections.length;i++)  
			newConnections[i] = connections[i];
		newConnections[connections.length] = refNode;
		connections = newConnections;
	}
	public void conectWithNode(Node refNode) {
		this.conectToNode(refNode);
		refNode.conectToNode(this);
	}
	public void conectWithNodes(Node[] refNodes) {
		for (int i = 0;i<refNodes.length;i++) {
			conectWithNode(refNodes[i]);
		}
	}
}
