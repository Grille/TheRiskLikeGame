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
	World world;
	Camera cam;
	Stage primaryStage;
	GraphicsContext ctx;
	
	//render used
	AnimationTimer animationTimer;
	//Image[] images;
	//Texture[] textureList;
	
	//Options
	Color backColor = Color.BLACK;
	
	//statistics
	int fpsCounter=0,fps=0;
	long fpsDelta=0;
	
	Renderer(Stage primaryStage,GraphicsContext ctx) {
		this.primaryStage = primaryStage;
		this.ctx = ctx;	
		animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	render();
            	if (cam.tilt > 0.5f)
            		cam.tilt*=0.9f;
            }
        };
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
    	
    	int width = (int)primaryStage.getWidth(), height = (int)primaryStage.getHeight();
    	if (world != null) {
	    	world.finalProcessing();
	    	if (world.repeatX) drawWorld(-world.width,0);
	    	drawWorld(0,0);
	    	if (world.repeatX) drawWorld(world.width,0);
	    	
	    	ctx.setFill(backColor);
	    	if (!world.repeatY) {
	    	ctx.fillRect(0, 0, width, cam.transformY(0));
	    	ctx.fillRect(0, cam.transformY(world.height), width, height);
	    	}
	    	if (!world.repeatX) {
	    	ctx.fillRect(0, 0, cam.transformX(0), height);
	    	ctx.fillRect(cam.transformX(world.width), 0, width, height);
	    	}
    	}
    	else {
        	ctx.setFill(backColor);
        	ctx.fillRect(0, 0, width, height);
    	}
    	ms -= (int)System.currentTimeMillis();
		ctx.setFill(Color.BLACK);
    	ctx.setFont(new Font("consolas", 15));
    	ctx.setTextAlign(TextAlignment.LEFT);
    	ctx.fillText("Debug: "+ms + "\nFPS: "+fps, 0, 15);
	}
	public void startRendering() {animationTimer.start();}
	public void stopRendering() {animationTimer.stop();}
	
	//render functions
	private void drawWorld(int posX,int posY) {
    	float oldX = cam.posX,oldY = cam.posY;
    	cam.posX += posX;cam.posY += posY;
    	if (world.backgroundImage.image != null)
    	ctx.drawImage(
    		world.backgroundImage.image
    		, 0, 0, world.backgroundImage.getWidth(), world.backgroundImage.getHeight()
    		, cam.transformX(0), cam.transformY(0), world.width*cam.scale+1, world.height*cam.scale*cam.tilt+1
    		);
    	if (cam.scale > 0.02) {
    	drawObjects(world.getDeco(0));
    	drawObjects(world.getDeco(1));
    	drawNodeConnections(world.getNodes());
    	drawNodes(world.getNodes());
    	drawObjects(world.getDeco(2));
    	}
    	cam.posX = oldX;cam.posY=oldY;
	}
	private void drawNodeConnections(Node[] nodes) {
		//draw connections: loop all nodes
		int width = (int)primaryStage.getWidth(), height = (int)primaryStage.getHeight();
		float scale = cam.scale;
    	for (int i1 = 0;i1<nodes.length;i1++) {
    		Node[] connections = nodes[i1].getConnections();
    		if (connections == null)continue;
    		for (int i2 = 0;i2<connections.length;i2++) {
    			if (connections[i2] == null)continue;
    			Node node1 = nodes[i1],node2 = connections[i2];
    			if (node1.posX<node2.posX)continue;
    			else if (node1.posX==node2.posX && node1.posY<node2.posY)continue;
    			int drawPosX1 = cam.transformX(node1.posX),drawPosY1 = cam.transformY(node1.posY),drawPosX2,drawPosY2;
    			if (world.repeatX && node1.posX-node2.posX<-world.width/2) drawPosX2 = cam.transformX(node2.posX-world.width);
    			else if (world.repeatX && node1.posX-node2.posX>world.width/2) drawPosX2 = cam.transformX(node2.posX+world.width);
    			else drawPosX2 = cam.transformX(node2.posX);
    			if (world.repeatY && node1.posY-node2.posY<-world.height/2) drawPosY2 = cam.transformY(node2.posY-world.height);
    			else if (world.repeatY && node1.posY-node2.posY>world.height/2) drawPosY2 = cam.transformY(node2.posY+world.height);
    			else drawPosY2 = cam.transformY(node2.posY);
    			if ((drawPosX1<0||drawPosX1>width||drawPosY1<0||drawPosY1>height)&&(drawPosX2<0||drawPosX2>width||drawPosY2<0||drawPosY2>height)) continue;
    			if (node1.owner != null && node1.owner == node2.owner) 
    			ctx.setStroke(new Color(node1.owner.color.getRed(),node1.owner.color.getGreen(),node1.owner.color.getBlue(),0.75f));
    			else ctx.setStroke(new Color(0.5f,0.5f,0.5f,0.5f));
    			ctx.setLineWidth(2*scale);
    			ctx.strokeLine(drawPosX1, drawPosY1, drawPosX2, drawPosY2);
    		}
    	}
	}
	private void drawNodes(Node[] nodes) {
		
    	//draw nodes: loop all nodes
		int width = (int)primaryStage.getWidth(), height = (int)primaryStage.getHeight();
		float scale = cam.scale;
    	for (int i = 0;i<nodes.length;i++) {
    		//get ref/drawPos
    		Node node =  nodes[i];
    		int drawPosX = cam.transformX(node.posX);
    		int drawPosY = cam.transformY(node.posY);
    		
    		//is alive?, is in view range
        	Image img = node.getImage().image;
        	if (img == null) continue;	
    		if (drawPosX < -img.getWidth()/2 || drawPosX > width + img.getWidth()/2 || drawPosY < -img.getHeight()/2)continue;
    		else if (drawPosY > height+img.getHeight()/2)return;
    		
    		//draw ground/get color
    		ctx.setFill(Color.WHITE);
    		if (scale > 0.9)
    		ctx.fillOval(drawPosX-32*scale, drawPosY-64*cam.tilt*scale/2, 64*scale, 64*cam.tilt*scale);
    		if (node.owner != null) {ctx.setFill(node.owner.color);ctx.setStroke(node.owner.color);}
    		else {ctx.setFill(Color.GRAY);ctx.setStroke(Color.GRAY);}
        	ctx.setLineWidth(3*scale);
        	if (scale > 0.3)
        	ctx.strokeOval(drawPosX-32*scale, drawPosY-64*cam.tilt*scale/2, 64*scale, 64*cam.tilt*scale);
        	
        	//drawGraphics
        	ctx.drawImage(img, drawPosX-img.getWidth()/2*scale,drawPosY-img.getHeight()/2*scale,img.getWidth()*scale,img.getHeight()*scale);
        	
        	//node title/info
        	ctx.setFont(new Font("consolas", 15));
        	ctx.setTextAlign(TextAlignment.CENTER);
        	if (scale > 0.5)ctx.fillText(node.name, drawPosX, drawPosY+32*scale+2);
        	ctx.setTextAlign(TextAlignment.LEFT);
        	if (node.owner != null && scale > 0.1)ctx.fillText(""+node.units, drawPosX + img.getWidth()/2*scale, drawPosY-32*scale+2);
    	}
	}
	private void drawObjects(WorldObject[] worldObjects) {
    	//draw nodes: loop all nodes
		int width = (int)primaryStage.getWidth(), height = (int)primaryStage.getHeight();
		float scale = cam.scale;
    	for (int i = 0;i<worldObjects.length;i++) {
    		if (worldObjects[i].posZ == 0)scale = cam.scale;
    		else scale = cam.scale*(float)worldObjects[i].posZ;
    		if (scale > 10)continue;
    		
    		int drawPosX = cam.transformX(worldObjects[i].posX,scale);
    		int drawPosY = cam.transformY(worldObjects[i].posY,scale);
    		
    		//is alive?, is in view range
        	Image img = worldObjects[i].getImage().image;
        	if (img == null) continue;	
    		if (drawPosX < -img.getWidth()/2 || drawPosX > width + img.getWidth()/2 || drawPosY < -img.getHeight()/2)continue;
    		else if (drawPosY > height+img.getHeight()/2)return;
    		
    		//drawGraphics
        	ctx.drawImage(img, drawPosX-img.getWidth()/2*scale,drawPosY-img.getHeight()/2*scale,img.getWidth()*scale,img.getHeight()*scale);
    	}
	}
	

}
