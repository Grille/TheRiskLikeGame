package gne.assets;

import javafx.scene.image.Image;

//has world cordinates
public class WorldObject {
	public int posX = 0,posY = 0,posZ = 0;
	protected Image img;
	public WorldObject(int posX,int posY) {
		this.posX = posX;this.posY=posY;this.img=null;
	}
	public WorldObject(int posX,int posY,int posZ) {
		this.posX = posX;this.posY=posY;this.posZ = posZ;this.img=null;
	}
	public WorldObject(int posX,int posY,Image img) {
		this.posX = posX;this.posY=posY;this.img=img;
	}
	public WorldObject(int posX,int posY,int posZ,Image img) {
		this.posX = posX;this.posY=posY;this.posZ = posZ;this.img=img;
	}
	public void setImage(Image img) {
		this.img =  img;
	}
	public Image getImage() {
		return img;
	}
}
