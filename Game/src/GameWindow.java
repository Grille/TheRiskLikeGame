//import gne.assets.*;
import gne.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class GameWindow extends Application{
	Parent root;
	Scene scene;
	Canvas canvas;
	Button b1,b2;
	Stage primaryStage;
	Stage secondStage;
	Camera camera;
	
	GameLogic gameLogic;
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		secondStage = null;
		this.primaryStage = primaryStage;
		initScene(primaryStage);

        camera = (gameLogic = new GameLogic(canvas)).camera;
        
		addEvents();
	
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        
        startSecond();
        
       
	}	
	private void initScene(Stage primaryStage) {
		try {
			primaryStage.setTitle("Game");
			root = FXMLLoader.load(new URL("file:"+new File("data/fxml/gui.fxml").getAbsolutePath()));
	        scene = new Scene(root);
	        scene.getStylesheets().clear();
	        scene.getStylesheets().add("file:///" + new File("data/fxml/style.css").getAbsolutePath().replace("\\", "/"));
	        primaryStage.setScene(scene);
	        canvas = (Canvas)scene.lookup("#canvas");
	        
	        b1 = (Button)scene.lookup("#bMenu");
	        b2 = (Button)scene.lookup("#bNext");
	        //secondScene.getStylesheets().add(new URL("file:"+new File("data/fxml/menu.fxml").getAbsolutePath()).getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Runtime.getRuntime().exit(0);
		}
	}
	@SuppressWarnings("unchecked")
	private void startSecond() {
        //WTF?!?!?!?
		try {
			Parent root = FXMLLoader.load(new URL("file:"+new File("data/fxml/menu.fxml").getAbsolutePath()));
	        Scene secondScene = new Scene(root, 323, 254);
	        secondScene.getStylesheets().clear();
	        secondScene.getStylesheets().add("file:///" + new File("data/fxml/style.css").getAbsolutePath().replace("\\", "/"));
	        secondStage = new Stage();
	        secondStage.setTitle("Main Menu");
	        secondStage.setScene(secondScene);
	        secondStage.show();
	        
	        ObservableList<String> choice = FXCollections.observableArrayList("Empty", "Human", "Computer");
	        for (int i = 0;i<6;i++) {
	        	((ChoiceBox<String>)secondScene.lookup("#cb"+(i+1))).setItems(choice);
	        	((ChoiceBox<String>)secondScene.lookup("#cb"+(i+1))).getSelectionModel().select(2);
	        }
	        ((ChoiceBox<String>)secondScene.lookup("#cb1")).getSelectionModel().select(1);
	        	
	        //String[] colors = new String[] {"#1a3399","#b31a1a","#1a4d1a","#b3b31a","#4d1a4d","#336666"};
	        String[] colors = new String[] {"#00f","#f00","#0f0","#ff0","#f0f","#0ff"};
	        for (int i = 0;i<6;i++)
	        	((ColorPicker)secondScene.lookup("#cp"+(i+1))).setValue(Color.web(colors[i]));

	        ((Button)secondScene.lookup("#bClose")).setOnMouseClicked(e -> {
	        	Runtime.getRuntime().exit(0);
			});
	        ((Button)secondScene.lookup("#bCancel")).setOnMouseClicked(e -> {
	        	secondStage.hide();
	        	primaryStage.show();
	        	gameLogic.renderer.startRendering();	
			});
	        ((Button)secondScene.lookup("#bStart")).setOnMouseClicked(e -> {
	        	
	    		Player[] player =  new Player[6];
	    		for (int i = 0;i<6;i++) {
	    			player[i] = new Player(
    					"Player"+(i+1),
    					((ColorPicker)secondScene.lookup("#cp"+(i+1))).getValue(),
    					PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb"+(i+1))).getSelectionModel().getSelectedIndex()]
    					);
	    		}
	    		gameLogic.initGame(player);
	        	secondStage.hide();
	        	primaryStage.show();
				canvas.setWidth(scene.getWidth());
				canvas.setHeight(scene.getHeight());
	        	gameLogic.renderer.startRendering();	
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Runtime.getRuntime().exit(0);
		}
	}
	
	boolean pMouseDown,sMouseDown,mMouseDown;
	private void addEvents() {
		
		canvas.setCursor(Cursor.CROSSHAIR);
		
		b1.setOnMouseClicked(e -> {
			primaryStage.hide();
			secondStage.show();
			gameLogic.renderer.stopRendering();	
		});
		b2.setOnMouseClicked(e -> {
			gameLogic.nextRound();
			b2.setText("Round "+gameLogic.game.getCurrentRound()+" : Next");
		});
	
		scene.setOnMouseDragged(e -> {
			if (pMouseDown||mMouseDown) camera.onMouseDrag(e);
			});
		scene.setOnMouseMoved(e -> {
			gameLogic.move();camera.onMouseMove(e);
			});
		scene.setOnScroll(e -> {
			camera.onScroll(e);
			});
		
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
