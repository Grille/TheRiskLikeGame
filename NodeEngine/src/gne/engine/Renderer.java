package gne.engine;

import gne.assets.*;

import java.io.File;
import java.util.*;

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
	
	private World world;
	private Camera cam;
	private Stage primaryStage;
	private GraphicsContext ctx;
	private AnimationTimer animationTimer;
	
	public Renderer(Stage primaryStage,GraphicsContext ctx) {
		this.primaryStage = primaryStage;
		this.ctx = ctx;	
		initTimer();
	}
	
	
	public void render() {
    	int width = (int)primaryStage.getWidth(), height = (int)primaryStage.getHeight();
    	//clear scene
    	//ctx.clearRect(0, 0, width, height);
    	ctx.setFill(new Color(0.9,0.85,0.6,1));
    	ctx.fillRect(0, 0, width, height);

    	drawObjects(world.getDeco(0));
    	drawObjects(world.getDeco(1));
    	drawNodeConnections(world.getNodes());
    	drawNodes(world.getNodes());
    	drawObjects(world.getDeco(2));
	}
	
	private void drawNodeConnections(Node[] nodes) {
		//draw connections: loop all nodes
		float scale = cam.scale;
    	for (int i1 = 0;i1<nodes.length;i1++) {
    		Node[] connections = nodes[i1].getConnections();
    		if (connections == null)continue;
    		for (int i2 = 0;i2<connections.length;i2++) {
    			if (connections[i2] == null)continue;
    			Node node1 = nodes[i1],node2 = connections[i2];
    			int drawPosX1 = cam.transformX(node1.posX);int drawPosY1 = cam.transformY(node1.posY);
    			int drawPosX2 = cam.transformX(node2.posX);int drawPosY2 = cam.transformY(node2.posY);
    			if (node1.owner != null && node1.owner == node2.owner) ctx.setStroke(node1.owner.color);
    			else ctx.setStroke(Color.GRAY);
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
    		
    		if (drawPosX < 0 || drawPosX > width || drawPosY < 0)continue;
    		else if (drawPosY > height-0)return;
    		//draw ground/get color
    		ctx.setFill(Color.WHITE);
    		if (scale > 0.9)
    		ctx.fillOval(drawPosX-32*scale, drawPosY-16*scale, 64*scale, 32*scale);
    		if (node.owner != null) {ctx.setFill(node.owner.color);ctx.setStroke(node.owner.color);}
    		else {ctx.setFill(Color.GRAY);ctx.setStroke(Color.GRAY);}
        	ctx.setLineWidth(2*scale);
        	if (scale > 0.5)
        	ctx.strokeOval(drawPosX-32*scale, drawPosY-16*scale, 64*scale, 32*scale);
        	
        	//drawGraphics
        	Image img = node.getImage();
        	if (img != null)
        	ctx.drawImage(img, drawPosX-32*scale,drawPosY-32*scale-16*scale,img.getWidth()*scale,img.getHeight()*scale);
        	
        	//node title/info
        	ctx.setFont(new Font("consolas", 15));
        	ctx.setTextAlign(TextAlignment.CENTER);
        	ctx.fillText(node.name, drawPosX, drawPosY+32*scale+2);
    	}
	}
	private void drawObjects(WorldObject[] worldObjects) {
    	//draw nodes: loop all nodes
		float scale = cam.scale;
    	for (int i = 0;i<worldObjects.length;i++) {
    		if (worldObjects[i].posZ == 0)scale = cam.scale;
    		else scale = cam.scale*(float)worldObjects[i].posZ;
    		if (scale > 10)continue;
    		int drawPosX = cam.transformX(worldObjects[i].posX,scale);
    		int drawPosY = cam.transformY(worldObjects[i].posY,scale);
        	Image img = worldObjects[i].getImage();
        	if (img != null)
        	ctx.drawImage(img, drawPosX-32*scale,drawPosY-32*scale-16*scale,img.getWidth()*scale,img.getHeight()*scale);
    	}
	}
	
	public void startRenderTimer(World world,Camera cam) {
		this.world = world;this.cam = cam;
		animationTimer.start();
	}
	public void stopRenderTimer() {
		animationTimer.stop();
	}
	private void initTimer() {
		animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	render();
            }
        };
	}
}
