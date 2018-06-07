package gne;

import gne.assets.*;

import java.io.File;
import java.util.*;

import com.sun.javafx.sg.prism.EffectFilter;

import javafx.application.Application;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Renderer {
	
	//refs
	Canvas canvas;
	protected GraphicsContext gc;
	World world;
	Player[] players;
	protected Camera camera;
	
	//render used
	AnimationTimer animationTimer;
	//Image[] images;
	//Texture[] textureList;
	
	//Options
	Color backColor = Color.BLACK;
	
	//statistics
	int fpsCounter=0,fps=0;
	long fpsDelta=0;
	
	public Renderer(Canvas canvas) {
		camera = new Camera(0,0,1);
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();	
		animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	render();
            }
        };
	}
	
	
	public void setRenderSource(Game game,Camera camera) {
		this.camera = camera;this.camera.canvas = canvas;this.camera.world = world = game.world;players = game.player;
	}
	//set Options
	public void setBackColor(Color set) {backColor = set;}
	
	//public render calls
	public void render() {
    	int ms = (int)System.currentTimeMillis();
    	
    	fpsCounter++;
    	if (fpsDelta < System.currentTimeMillis()-1000) {
    		fpsDelta = System.currentTimeMillis();
    		fps = fpsCounter;
    		fpsCounter = 0;
    	}
    	
    	int width = (int)canvas.getWidth(), height = (int)canvas.getHeight();
    	if (world != null) {
	    	world.finalProcessing();
	    	if (world.repeatX) drawWorld(-world.width,0);
	    	drawWorld(0,0);
	    	if (world.repeatX) drawWorld(world.width,0);
	    	
	    	gc.setFill(backColor);
	    	if (!world.repeatY) {
	    	gc.fillRect(0, 0, width, camera.transformY(0));
	    	gc.fillRect(0, camera.transformY(world.height), width, height);
	    	}
	    	if (!world.repeatX) {
	    	gc.fillRect(0, 0, camera.transformX(0), height);
	    	gc.fillRect(camera.transformX(world.width), 0, width, height);
	    	}
    	}
    	else {
        	gc.setFill(backColor);
        	gc.fillRect(0, 0, width, height);
    	}
    	
    	drawGUI((int)canvas.getWidth(),(int)canvas.getHeight());
    	ms -= (int)System.currentTimeMillis();
    	gc.setFill(Color.LIME);
		gc.setFill(Color.BLACK);
    	gc.setFont(new Font("Unispace", 15));
    	gc.setTextAlign(TextAlignment.LEFT);
    	gc.fillText("Debug: "+ms + "\nFPS: "+fps+"\nPosX: "+camera.getCurrentPosX(), 0, 15+40);
    	
    	
    	gc.fillText("X: "+camera.getNearestNode(9999).name, camera.mouseX, camera.mouseY+15);
	}
	public void startRendering() {animationTimer.start();}
	public void stopRendering() {animationTimer.stop();}
	
	private void drawImage(Image img,int sx,int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
		PixelReader reader = img.getPixelReader();
		PixelWriter writer = gc.getPixelWriter();
		for (int ix = 0;ix<sw;ix++) {
			for (int iy = 0;iy<sh;iy++) {
				Color color = reader.getColor(sx+ix, sy+iy);
				if (color.isOpaque()) {
					writer.setColor(dx+ix, dy+iy, color);
				}
			}
		}
	}
	
	//render functions
	private void drawWorld(int posX,int posY) {
    	float oldX = camera.posX,oldY = camera.posY;
    	camera.posX += posX;camera.posY += posY;
    	if (world.backgroundImage.image != null)
    	gc.drawImage(
    		world.backgroundImage.image
    		, 0, 0, world.backgroundImage.getWidth(), world.backgroundImage.getHeight()
    		, camera.transformX(0), camera.transformY(0), world.width*camera.scale+1, world.height*camera.scale*camera.tilt+1
    		);
    		
    	if (camera.scale > 0.02) {
    	drawObjects(world.getDeco(0));
    	drawObjects(world.getDeco(1));
    	drawNodeConnections(world.getNodes());
    	drawNodes(world.getNodes());
    	drawObjects(world.getDeco(2));
    	}
    	camera.posX = oldX;camera.posY=oldY;
	}
	private void drawNodeConnections(Node[] nodes) {
		//draw connections: loop all nodes
		int width = (int)canvas.getWidth(), height = (int)canvas.getHeight();
		float scale = camera.scale;
    	for (int i1 = 0;i1<nodes.length;i1++) {
    		Node[] connections = nodes[i1].getConnections();
    		if (connections == null)continue;
    		for (int i2 = 0;i2<connections.length;i2++) {
    			if (connections[i2] == null)continue;
    			Node node1 = nodes[i1],node2 = connections[i2];
    			if (node1.posX<node2.posX)continue;
    			else if (node1.posX==node2.posX && node1.posY<node2.posY)continue;
    			int drawPosX1 = camera.transformX(node1.posX),drawPosY1 = camera.transformY(node1.posY),drawPosX2,drawPosY2;
    			if (world.repeatX && node1.posX-node2.posX<-world.width/2) drawPosX2 = camera.transformX(node2.posX-world.width);
    			else if (world.repeatX && node1.posX-node2.posX>world.width/2) drawPosX2 = camera.transformX(node2.posX+world.width);
    			else drawPosX2 = camera.transformX(node2.posX);
    			if (world.repeatY && node1.posY-node2.posY<-world.height/2) drawPosY2 = camera.transformY(node2.posY-world.height);
    			else if (world.repeatY && node1.posY-node2.posY>world.height/2) drawPosY2 = camera.transformY(node2.posY+world.height);
    			else drawPosY2 = camera.transformY(node2.posY);
    			if ((drawPosX1<0||drawPosX1>width||drawPosY1<0||drawPosY1>height)&&(drawPosX2<0||drawPosX2>width||drawPosY2<0||drawPosY2>height)) continue;
    			if (node1.owner != null && node1.owner == node2.owner) 
    			gc.setStroke(new Color(node1.owner.color.getRed(),node1.owner.color.getGreen(),node1.owner.color.getBlue(),0.75f));
    			else gc.setStroke(new Color(0.5f,0.5f,0.5f,0.5f));
    			gc.setLineWidth(4*scale);
    			gc.strokeLine(drawPosX1, drawPosY1, drawPosX2, drawPosY2);
    		}
    	}
	}
	private void drawNodes(Node[] nodes) {
		
    	//draw nodes: loop all nodes
		int width = (int)canvas.getWidth(), height = (int)canvas.getHeight();
		float scale = camera.scale;
    	for (int i = 0;i<nodes.length;i++) {
    		//get ref/drawPos
    		Node node =  nodes[i];
    		int drawPosX = camera.transformX(node.posX);
    		int drawPosY = camera.transformY(node.posY);
    		
    		//is alive?, is in view range
        	Image img = node.getImage().image;
        	if (img == null) continue;	
    		if (drawPosX < -img.getWidth()/2 || drawPosX > width + img.getWidth()/2 || drawPosY < -img.getHeight()/2)continue;
    		else if (drawPosY > height+img.getHeight()/2)return;
    		
    		drawNode(node,drawPosX,drawPosY);
    	}
	}
	private void drawObjects(WorldObject[] worldObjects) {
    	//draw nodes: loop all nodes
		int width = (int)canvas.getWidth(), height = (int)canvas.getHeight();
		float scale = camera.scale;
    	for (int i = 0;i<worldObjects.length;i++) {
    		if (worldObjects[i].posZ == 0)scale = camera.scale;
    		else scale = camera.scale*(float)worldObjects[i].posZ;
    		if (scale > 10)continue;
    		
    		int drawPosX = camera.transformX(worldObjects[i].posX,scale);
    		int drawPosY = camera.transformY(worldObjects[i].posY,scale);
    		
    		//is alive?, is in view range
        	Image img = worldObjects[i].getImage().image;
        	if (img == null) continue;	
    		if (drawPosX < -img.getWidth()/2 || drawPosX > width + img.getWidth()/2 || drawPosY < -img.getHeight()/2)continue;
    		else if (drawPosY > height+img.getHeight()/2)return;
    		
    		//drawGraphics
        	gc.drawImage(img, drawPosX-img.getWidth()/2*scale,drawPosY-img.getHeight()/2*scale,img.getWidth()*scale,img.getHeight()*scale);
    	}
	}
	
	//protected render functions
	protected void drawNode(Node node,int drawPosX,int drawPosY) {
		float scale = camera.scale;
		
    	Image img = node.getImage().image;
		
		Color nodeColor;
		if (node.owner != null) nodeColor = node.owner.color;
		else nodeColor = Color.GRAY;
		gc.setStroke(nodeColor);gc.setFill(nodeColor);
		//draw ground/get color
		//ctx.setFill(Color.WHITE);
		//if (scale > 0.9)
		float size = 58; 
		gc.fillOval(drawPosX-size*scale/2, drawPosY-size*camera.tilt*scale/2, size*scale, size*camera.tilt*scale);

    		
        if (node.renderOption == 1) {
        	gc.setLineWidth(16*scale);
        	gc.strokeOval(drawPosX-32*scale, drawPosY-64*camera.tilt*scale/2, 64*scale, 64*camera.tilt*scale);
        }
        else if (node.renderOption == 2) {
        	gc.setLineWidth(8*scale);
        	gc.strokeOval(drawPosX-32*scale, drawPosY-64*camera.tilt*scale/2, 64*scale, 64*camera.tilt*scale);
        }
		gc.setFill(Color.BLACK);
    	gc.setFont(new Font("unispace", 35*camera.scale));
    	gc.setTextAlign(TextAlignment.CENTER);
    	if (node.owner != null && scale > 0.1) gc.fillText(""+node.units, drawPosX, drawPosY+14*camera.scale);
        	
    	//drawGraphics

    	gc.drawImage(img, drawPosX-img.getWidth()/2*scale,drawPosY-img.getHeight()/2*scale,img.getWidth()*scale,img.getHeight()*scale);
    	
    	//node title/info
		gc.setStroke(Color.BLACK);gc.setFill(nodeColor);
    	gc.setFont(new Font("unispace", 20*camera.scale));
    	gc.setTextAlign(TextAlignment.CENTER);
    	if (scale > 0.5) {
    		gc.fillText(node.name, drawPosX, drawPosY+64*camera.scale/camera.tilt);
    		gc.setLineWidth(1*scale);
    	}
	}
	protected void drawMouse(int drawPosX,int drawPosY) {
		
	}
	protected void drawGUI(int width,int height){
		
	}

}
