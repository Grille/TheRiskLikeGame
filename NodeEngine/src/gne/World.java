package gne;
import java.io.*;
import java.nio.file.Files;
import java.util.stream.Stream;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
//NodeList
public class World {
	
	String name;
	int width,height;
	Node[] nodes;
	WorldObject[][] decoLayers;
	Player[] players;
	
	NodeGroup[] groups;
	//Node[][] groups;
	//String[] groupNames;
	
	public boolean repeatX = false,repeatY = false;
	Node[] conectionsDrawList;
	
	Texture backgroundImage;
	Texture waterImage;
	
	private boolean[] arrayFinalized;
	public World() {
		this(0,0);
	}
	public World(int width,int height) {
		this(width,height,new Node[0]);
	}
	public World(int width,int height,Node[] nodes) {
		this.name = "World";
		this.width = width;
		this.height = height;
		this.nodes = nodes;
		groups = new NodeGroup[0];
		decoLayers = new WorldObject[3][0];
		arrayFinalized = new boolean[4];
	}
	public World(String path) {
		load(path);
	}
	
	public void clear() {
		this.name = "World";
		this.width = 0;
		this.height = 0;
		this.nodes = new Node[0];
		groups = new NodeGroup[0];
		decoLayers = new WorldObject[3][0];
		arrayFinalized = new boolean[4];
	}
	
	private <T> void addWorldObject(T[] source,T[] traget,T addObject) {
		for (int i = 0;i<source.length;i++) traget[i] = source[i];
		traget[source.length] = addObject;
	}
	private <T> void addWorldObjects(WorldObject[] source,WorldObject[] traget,WorldObject[] addObjects) {
		int index = 0;
		for (int i = 0;i<source.length;i++) traget[index++] = source[i];
		for (int i = 0;i<addObjects.length;i++) traget[index++] = addObjects[i];
	}
	
	public void addDeco(WorldObject addDeco,int layer) {
		addWorldObject(decoLayers[layer],decoLayers[layer]=new WorldObject[decoLayers[layer].length+1],addDeco);
		arrayFinalized[layer] = false;
	}
	public void addDecos(WorldObject[] addDeco,int layer) {
		addWorldObjects(decoLayers[layer],decoLayers[layer]=new WorldObject[decoLayers[layer].length+1],addDeco);
		arrayFinalized[layer] = false;
	}
	public void addNode(Node addNode) {
		addWorldObject(nodes,nodes = new Node[nodes.length+1],addNode);
		arrayFinalized[3] = false;
	}
	public void groupNodes(String name,String color,int units,Node[] nodes) {
		NodeGroup group = new NodeGroup(name,color,units,nodes);
		addWorldObject(groups,groups = new NodeGroup[groups.length+1],group);
		//for (int i = 0;i<source.length;i++) traget[i] = source[i];
		//traget[source.length] = addObject;
	}
	public void addNodes(Node[] addNodes, String groupName) {
		addNodes(addNodes);/*
		Node[][] newNodes = new Node[groups.length+1][];
		String[] newNames = new String[groupNames.length+1];
		for (int i = 0;i<groupNames.length;i++) {
			newNodes[i] = groups[i];
			newNames[i] = groupNames[i];
		}
		groups = newNodes;
		groupNames = newNames;
		groups[groups.length-1] = addNodes;
		groupNames[groupNames.length-1] = groupName;
		addNodes(addNodes);*/
	}
	public void addNodes(Node[] addNodes) {
		addWorldObjects(nodes,nodes = new Node[nodes.length+addNodes.length],addNodes);
		arrayFinalized[3] = false;
	}

	private <T extends WorldObject> void deepSorting(T[] cords) {
		if (cords == null||cords.length<2)return;
		int clutters;
		do {
			clutters = 0;
			for (int i = 0;i<cords.length-1;i++) {
				if (cords[i].posY>cords[i+1].posY) {
					T backup = cords[i];
					cords[i] = cords[i+1];
					cords[i+1] =  backup;
					clutters++;
				}
			}
		} while (clutters != 0);
	}
	
	void deepSorting() {
		if (!arrayFinalized[0]) {
			deepSorting(decoLayers[0]);
			arrayFinalized[0] = true;
		}
		if (!arrayFinalized[1]) {
			deepSorting(decoLayers[1]);
			arrayFinalized[1] = true;
		}
		if (!arrayFinalized[2]) {
			deepSorting(decoLayers[2]);
			arrayFinalized[2] = true;
		}
		if (!arrayFinalized[3]) {
			deepSorting(nodes);
			for (int i = 0;i<nodes.length;i++) nodes[i].id = i;
			arrayFinalized[3] = true;
		}
	}
	
	public WorldObject[] getDeco(int layer) {
		return decoLayers[layer];
	}
	public Node getNode(int id) {
	if (id<0||id > nodes.length)return null;
		return nodes[id];
	}
	public Node getNode(String name) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].name == name) return nodes[i];
		}
		return null;
	}
	public Node[] getNodes() {
		return nodes;
	}
	public NodeGroup[] getNodeGroups() {
		return groups;
	}
	public NodeGroup getNodeGroup(String name) {
		for (int i = 0;i<groups.length;i++) {
			if (name == groups[i].name) return groups[i];
		}
		System.err.println("group "+name+" not found!");
		return null;
	}
	public void setBackgroundGraphic(Texture background) {
		backgroundImage = background;
	}
	public void setWaterGraphic(Texture water) {
		waterImage = water;
	}
	public void killPlayer(Player deadPlayer) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].owner == deadPlayer) nodes[i].owner = null;
		}
	}
	public void replacePlayer(Player oldPlayer,Player newPlayer) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].owner == oldPlayer) nodes[i].owner = newPlayer;
		}
	}
	public int getNumberOfNodesOwnedByPlayer(Player player) {
		int result = 0;
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].owner == player)result++;
		}
		return result;
	}
	public boolean areNodesContainsPlayer(Node[] nodes,Player player) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].getOwner() == player)return true;
		}
		return false;
	}
	public boolean areNodesOwndedByPlayer(Node[] nodes,Player player) {
		for (int i = 0;i<nodes.length;i++) {
			if (nodes[i].getOwner() != player)return false;
		}
		return true;
	}
	
	public void save(String path) {
		for (int i = 0;i<nodes.length;i++)
			nodes[i].id = i;
		
		BinaryFileWriter writer = new BinaryFileWriter(path);
		writer.writeString(name);
		writer.writeString(backgroundImage.path);
		writer.writeInt16(width);
		writer.writeInt16(height);
		writer.writeBool(repeatX);
		writer.writeBool(repeatY);
		writer.writeUint8(nodes.length);
		for (int i = 0;i<nodes.length;i++) {
			Node node = nodes[i];
			writer.writeString(node.name);
			writer.writeInt16(node.posX);
			writer.writeInt16(node.posY);
			writer.writeUint8(node.connections.length);
			for (int j = 0;j<node.connections.length;j++) {
				writer.writeUint8(node.connections[j].id);
			}
		}
		
		writer.writeUint8(groups.length);
		for (int i = 0;i<groups.length;i++) {
			NodeGroup group = groups[i];
			writer.writeString(group.name);
			writer.writeString(group.color);
			writer.writeUint8(group.units);
			writer.writeUint8(group.nodes.length);
			for (int j = 0;j<group.nodes.length;j++) {
				writer.writeUint8(group.nodes[j].id);
			}
		}
		
		writer.flush();
	}
	public void load(String path) {
		clear();
		BinaryFileReader reader = new BinaryFileReader(path);
		name = reader.readString();
		setBackgroundGraphic(new Texture(reader.readString()));
		width = reader.readInt16();
		height = reader.readInt16();
		repeatX = reader.readBool();
		repeatY = reader.readBool();
		
		int nodesLength = reader.readUint8();
		int[][] connections = new int[nodesLength][];
		nodes = new Node[nodesLength];
		for (int i = 0;i<nodesLength;i++) {
			String name = reader.readString();
			nodes[i] = new Node(reader.readInt16(),reader.readInt16(),name);
			int connectionsLength = reader.readUint8();
			connections[i] = new int[connectionsLength];
			for (int j = 0;j<connectionsLength;j++) {
				connections[i][j] = reader.readUint8();
			}
		}
		for (int i = 0;i<nodesLength;i++) {
			for (int j = 0;j<connections[i].length;j++) {
				nodes[i].conectWithNode(nodes[connections[i][j]]);
			}
		}
		
		int groupsLength = reader.readUint8();
		groups = new NodeGroup[groupsLength];
		for (int i = 0;i<groupsLength;i++) {
			String name = reader.readString();
			String color = reader.readString();
			int units = reader.readUint8();
			int groupNodesLength = reader.readUint8();
			Node[] groupNodes = new Node[groupNodesLength];
			for (int j = 0;j<groupNodesLength;j++) {
				groupNodes[j] = nodes[reader.readUint8()];
			}
			groups[i] = new NodeGroup(name,color,units,groupNodes);
		}
	}
}
