import gne.Node;
import gne.Texture;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Renderer extends gne.Renderer {

	float winChance = 0f;
	GameLogic gameLogic;
	Texture nodeTexture;
	public Renderer(Canvas canvas,GameLogic gameLogic) {
		super(canvas);
		this.gameLogic = gameLogic;
		nodeTexture = new Texture("file:/../data/png/town/town.png");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void drawNode(Node node,int drawPosX,int drawPosY) {
		float scale = camera.scale;
		float size = 58*scale; 
    	int units = node.getUnits();
		int selectetUnits = gameLogic.selectetUnits;
		
    	Image img = nodeTexture;
		Node nearestNode = camera.getNearestNode(70);
		Node selectetNode = gameLogic.selectetNode;
		
		Color nodeColor;
		
		
		if (node.getOwner() != null) 
			nodeColor = node.getOwner().getColor();
		else 
			nodeColor = Color.GRAY;
		gc.setStroke(nodeColor);gc.setFill(nodeColor);

		gc.fillOval(drawPosX-size/2, drawPosY-size/2, size, size);
    	
        if (node == selectetNode) {
        	units-=selectetUnits;
        	gc.setLineWidth(16*scale);
        	gc.strokeOval(drawPosX-32*scale, drawPosY-64*camera.tilt*scale/2, 64*scale, 64*camera.tilt*scale);
        }
        else if (node.isConectetWidthNode(selectetNode)) {
        	if (nearestNode==node)gc.setStroke(game.getActivePlayer().getColor());
        	else if (node.getOwner() == null || node.getOwner() == game.getActivePlayer())gc.setStroke(Color.GRAY);
        	else gc.setStroke(node.getOwner().getColor());
        	gc.setLineWidth(8*scale);
        	gc.strokeOval(drawPosX-32*scale, drawPosY-64*camera.tilt*scale/2, 64*scale, 64*camera.tilt*scale);
        }
        else if (node.getOwner() == game.getActivePlayer()) {
        	if (nearestNode==node) {
        		gc.setStroke(game.getActivePlayer().getColor());
        		gc.setLineWidth(8*scale);
        		gc.strokeOval(drawPosX-32*scale, drawPosY-64*camera.tilt*scale/2, 64*scale, 64*camera.tilt*scale);
        	}
        }
		gc.setFill(Color.BLACK);
    	gc.setFont(new Font("unispace", 35*camera.scale));
    	gc.setTextAlign(TextAlignment.CENTER);
    	if (node.getOwner() != null && scale > 0.1) gc.fillText(""+(units), drawPosX, drawPosY+14*camera.scale);
        	
    	//drawGraphics
    	gc.drawImage(img, drawPosX-img.getWidth()/2*scale,drawPosY-img.getHeight()/2*scale,img.getWidth()*scale,img.getHeight()*scale);
    	
    	//node title/info
		gc.setStroke(Color.BLACK);gc.setFill(nodeColor);
    	gc.setFont(new Font("unispace", 20*camera.scale));
    	gc.setTextAlign(TextAlignment.CENTER);
    	if (scale > 0.5) {
    		gc.fillText(node.getName(), drawPosX, drawPosY+64*camera.scale/camera.tilt);
    		gc.setLineWidth(1*scale);
    	}
	}
	
	@Override
	protected void drawMouse(int posX,int posY) {
    	int size = 30;
		int selectetUnits = gameLogic.selectetUnits;
		Node selectetNode = gameLogic.selectetNode;
		Node nearestNode = camera.getNearestNode(70);
		
		String text = "";
		
    	gc.setFont(Font.font("unispace", FontWeight.BOLD, 30));
    	gc.setTextAlign(TextAlignment.LEFT);
    	
    	gc.setStroke(game.getActivePlayer().getColor());
		gc.setFill(game.getActivePlayer().getColor());
    	gc.setLineWidth(2);
    	
		gc.strokeOval(posX-size/2, posY-size/2, size, size);
    	//gc.setLineWidth(1);
    	size +=4;
    	gc.setStroke(Color.BLACK);
		gc.strokeOval(posX-size/2, posY-size/2, size, size);
    	
		if (selectetUnits > 0) {
			Node node = nearestNode;
			
			if (node == null || (selectetNode != null &&!(selectetNode.isConectetWidthNode(node))) ) {
				text += " \\"+selectetUnits;
			}
			else if(node.getOwner() == game.getActivePlayer() || node.getOwner() == null) {
				if (gameLogic.gamePhase == 0)
					text += "+\\"+selectetUnits;
				else 
					text += "<\\"+selectetUnits;
			}
			else if (gameLogic.gamePhase == 1) {
				text += "<\\"+selectetUnits;
				text += "\n  "+(int)(winChance*100)+"%";
			}
			else 
				text += " \\"+selectetUnits;
			
			gc.fillText(text, posX, posY);
			gc.strokeText(text, posX, posY);
		}
	}
	
	

}
