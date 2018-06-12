import gne.assets.*;
import gne.*;

import java.io.File;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class GameWindow extends Application{
	Group root;
	Scene scene;
	Canvas canvas;
	Button b1,b2;
	Stage primaryStage;
	Camera camera;
	
	GameLogic gameLogic;
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		this.primaryStage = primaryStage;
        primaryStage.setTitle("Game");
        root = new Group();	
        primaryStage.setScene(scene = new Scene(root));
        
        canvas = new Canvas(1920, 1080);
        root.getChildren().add(canvas);   
        
		b1 = new Button();
		b1.setStyle("-fx-font: 20 Unispace; -fx-base: #ee2211;border-radius: 8px;");
		b1.setPrefWidth(200);b1.setText("Menu");
		
		b2 = new Button();
		b2.setStyle("-fx-font: 20 Unispace; -fx-base: #ee2211;border-radius: 8px;");
		b2.setLayoutX(200);b2.setPrefWidth(200);b2.setText("Next Round");
		
		root.getChildren().add(b1);
		root.getChildren().add(b2);
        
        camera = (gameLogic = new GameLogic(canvas)).camera;
        
		addEvents();
		
        primaryStage.show();
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
	}	
	
	boolean pMouseDown,sMouseDown,mMouseDown;
	private void addEvents() {
		
		canvas.setCursor(Cursor.CROSSHAIR);
		
		b2.setOnMouseClicked(e -> {
			gameLogic.nextRound();
			b2.setStyle("-fx-font: 20 Unispace; -fx-base: #fff;border-radius: 8px;");
		});
	
		scene.setOnMouseDragged(e -> {if (mMouseDown) camera.addMouseDrag(e);});
		scene.setOnMouseMoved(e -> {gameLogic.move();camera.addMouseMove(e);});
		scene.setOnScroll(e -> {camera.addScroll(e);});
		
		scene.setOnMousePressed(e -> {
        	if (e.isPrimaryButtonDown())pMouseDown = true;
        	if (e.isSecondaryButtonDown())sMouseDown = true;
        	if (e.isMiddleButtonDown())mMouseDown = true;
        });
		scene.setOnMouseReleased(e -> {
			gameLogic.click(pMouseDown, sMouseDown, mMouseDown);
			pMouseDown = sMouseDown = mMouseDown = false;
        	if (e.isPrimaryButtonDown())pMouseDown = true;
        	if (e.isSecondaryButtonDown())sMouseDown = true;
        	if (e.isMiddleButtonDown())mMouseDown = true;
        });
		
		scene.setOnKeyPressed(e -> {
			gameLogic.isShiftDown = e.isShiftDown();
			switch (e.getCode()){
				case RIGHT:camera.addScaledPos(100,0);break;
				case LEFT:camera.addScaledPos(-100,0);break;
				case UP:camera.addScaledPos(0,-100);break;
				case DOWN:camera.addScaledPos(0,100);break;
				case Q:if(e.isControlDown())primaryStage.close();break;
			}
		});
		scene.setOnKeyReleased(e -> {
			gameLogic.isShiftDown = e.isShiftDown();
		});
		
	}
	
	
}
