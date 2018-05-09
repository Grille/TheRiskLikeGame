package gne.engine;



import java.awt.Point;
import javafx.stage.Stage;

public class Camera {
	
	private Stage stage;
	
	public float posX,posY;
	public float scale;
	
	public Camera(Stage primaryStage, float posX,float posY,float scale) {
		this.stage = primaryStage;
		this.posX = posX;this.posY = posY;this.scale = scale;
	}
	public void setPos(float posX, float posY) {
		this.posX = posX;this.posY = posY/0.5f;
	}
	public void addPos(float posX, float posY) {
		this.posX += posX/scale;this.posY += posY/scale/0.5;
	}
	public void setScale(float scale) {
		
		float oldX = posX;
		float oldY = posY;
		
		this.scale = scale;
		
		posY = oldX;
		posY = oldY;
	}
	public void addScale(float scale) {
		setScale(this.scale += (scale*this.scale)/500);
	}
	public int transformX(int posX) {
		return (int)(((posX-this.posX)*scale)+stage.getWidth()/2);
	}
	public int transformY(int posY) {
		return (int)(((posY-this.posY)*scale*0.5)+stage.getHeight()/2);
	}
	public int transformX(int posX,float scale) {
		return (int)(((posX-this.posX)*scale)+stage.getWidth()/2);
	}
	public int transformY(int posY,float scale) {
		return (int)(((posY-this.posY)*scale*0.5)+stage.getHeight()/2);
	}
	public Point transformMouse(int mouseX,int mouseY){
		return new Point(0,0);
	}
}
