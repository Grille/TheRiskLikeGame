package gne;

public class NodeGroup {
	String name;
	String color;
	Node[] nodes;
	int units;
	public NodeGroup(String name,String color,int units,Node[] nodes) {
		this.name = name;this.color = color;this.units = units;this.nodes=nodes;
	}
	public String getName() {
		return name;
	}
	public String getColor() {
		return color;
	}
	public Node[] getNodes() {
		return nodes;
	}
	public int getUnits() {
		return units;
	}
}
