package gne;

import javafx.scene.image.Image;

//has world cordinates
public class WorldObject  {

	int id;
	int posX = 0,posY = 0;float posZ = 1;
	protected Texture img;
	public WorldObject(int posX,int posY) {
		this.posX = posX;this.posY=posY;this.img=null;
	}
	public WorldObject(int posX,int posY,int posZ) {
		this.posX = posX;this.posY=posY;this.posZ = posZ;this.img=null;
	}
	public WorldObject(int posX,int posY,Texture img) {
		this.posX = posX;this.posY=posY;this.img=img;
	}
	public WorldObject(int posX,int posY,float posScale,Texture img) {
		this.posX = posX;this.posY=posY;this.posZ = posScale;this.img=img;
	}
	public void setImage(Texture img) {
		this.img =  img;
	}
	public Texture getTexture() {
		return img;
	}
}
