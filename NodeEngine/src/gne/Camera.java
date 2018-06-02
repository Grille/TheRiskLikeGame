package gne;



import java.awt.Point;
import javafx.stage.Stage;

public class Camera {
	
	World world = null;
	private Stage stage;
	
	float posX,posY;
	float scale,tilt = 1f;
	
	public Camera(Stage primaryStage, float posX,float posY,float scale) {
		this.stage = primaryStage;
		this.posX = posX;this.posY = posY;this.scale = scale;
	}

	//basic move functions
	public void setPos(float posX, float posY) {
		this.posX = posX;this.posY = posY;
		
		if (world == null)return;

		if(this.posX<0&&!world.repeatX)this.posX=0;
		else while (this.posX<0&&world.repeatX)this.posX += world.width;
		if(this.posX>world.width&&!world.repeatX)this.posX=world.width;
		else while (this.posX>world.width&&world.repeatX)this.posX -= world.width;
			
		if(this.posY<0&&!world.repeatY)this.posY=0;
		else while (this.posY<0&&world.repeatY)this.posY += world.height;
		if(this.posY>world.height&&!world.repeatY)this.posY=world.height;
		else while (this.posY>world.height&&world.repeatY)this.posY -= world.height;
	}
	public void addPos(float posX, float posY) {
		setPos(this.posX+posX,this.posY+posY) ;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public void addScale(float scale) {
		setScale(this.scale += (scale*this.scale));
	}
	
	//advanced move functions
	public void centerWorld() {
		if (world == null)return;
		posX = world.width/2;posY=world.height/2;
	}
	public void centerNode(WorldObject node) {
		posX = node.posX;posY=node.posY;
	}
	
	//mouse move functions
	void addCamPos(float posX, float posY) {
		setPos(this.posX+posX/scale,this.posY+posY/scale/tilt) ;
	}
	void addCamScale(float scale) {
		setScale(this.scale += (scale*this.scale)/500);
	}
	
	//graphic transforms
	int transformX(int posX) {
		return (int)(((posX-this.posX)*scale)+stage.getWidth()/2);
	}
	int transformY(int posY) {
		return (int)(((posY-this.posY)*scale*tilt)+stage.getHeight()/2);
	}
	int transformX(int posX,float scale) {
		return (int)(((posX-this.posX)*scale)+stage.getWidth()/2);
	}
	int transformY(int posY,float scale) {
		return (int)(((posY-this.posY)*scale*tilt)+stage.getHeight()/2);
	}
}
