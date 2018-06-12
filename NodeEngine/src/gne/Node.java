package gne;

import javafx.scene.image.Image;

public class Node extends WorldObject{
	
	String name;
	Player owner;
	Node[] connections;
	Building[] buildings;
	
	int id;
	int renderOption = 0;
	int units = 0;
	
	public String getName() {
		return name;
	}
	public void setOwner(Player set) {owner = set;}
	public Player getOwner() {return owner;}
	
	public void setRenderOption(int set) {renderOption = set;}
	public int getRenderOption() {return renderOption;}
	
	public void setUnits(int set) {units = set;}
	public void addUnits(int add) {units += add;}
	public int getUnits() {return units;}

	public Node(int posX,int posY) {this(posX,posY,null,"");}
	public Node(int posX,int posY,Texture img) {this(posX,posY,img,"");}
	public Node(int posX,int posY,String name) {this(posX,posY,null,name);}
	public Node(int posX,int posY,Texture img,String name) {
		super(posX,posY,img);
		this.connections = new Node[0];
		this.name = name;
	}

	public Node[] getConnections() {return connections;}
	
	public boolean isConectetWidthNode(Node node) {
		for (int i = 0;i<connections.length;i++) {
			if (connections[i] == node) return true;
		}  
	return false;
	}
	private void conectToNode(Node refNode) {
		if (refNode == null)return;
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
