import gne.assets.*;
import gne.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.application.Application;
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
	Group root;
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
        primaryStage.setTitle("Game");
        root = new Group();	
        primaryStage.setScene(scene = new Scene(root));
        
        canvas = new Canvas(1920, 1080);
        root.getChildren().add(canvas);   
        
		b1 = new Button();
		b1.setStyle("-fx-font: 20 Unispace;");
		b1.setPrefWidth(200);b1.setText("Menu");
		
		b2 = new Button();
		b2.setStyle("-fx-font: 20 Unispace;");
		b2.setLayoutX(200);b2.setPrefWidth(200);b2.setText("Next Round");
		
		root.getChildren().add(b1);
		root.getChildren().add(b2);
        
        camera = (gameLogic = new GameLogic(canvas)).camera;
        
		addEvents();
	
        
        //primaryStage.show();
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        
        //root.getChildren().add(secondLabel);
        
        startSecond();
        
       
	}	
	private void startSecond() {
        //WTF?!?!?!?
		try {
			Parent root = FXMLLoader.load(new URL("file:"+this.getClass().getClassLoader().getResource("").getPath()+"/../data/fxml/menu.fxml"));
	        Scene secondScene = new Scene(root, 323, 254);
	        secondStage = new Stage();
	        secondStage.setTitle("Main Menu");
	        secondStage.setScene(secondScene);
	        secondStage.show();
	        
	        ObservableList<String> choice = FXCollections.observableArrayList("Empty", "Human", "Computer");
	        ((ChoiceBox<String>)secondScene.lookup("#cb1")).setItems(choice);
	        ((ChoiceBox<String>)secondScene.lookup("#cb2")).setItems(choice);
	        ((ChoiceBox<String>)secondScene.lookup("#cb3")).setItems(choice);
	        ((ChoiceBox<String>)secondScene.lookup("#cb4")).setItems(choice);
	        ((ChoiceBox<String>)secondScene.lookup("#cb5")).setItems(choice);
	        ((ChoiceBox<String>)secondScene.lookup("#cb6")).setItems(choice);
	        
	        ((ChoiceBox<String>)secondScene.lookup("#cb1")).getSelectionModel().select(1);
	        ((ChoiceBox<String>)secondScene.lookup("#cb2")).getSelectionModel().select(2);
	        ((ChoiceBox<String>)secondScene.lookup("#cb3")).getSelectionModel().select(2);
	        ((ChoiceBox<String>)secondScene.lookup("#cb4")).getSelectionModel().select(2);
	        ((ChoiceBox<String>)secondScene.lookup("#cb5")).getSelectionModel().select(2);
	        ((ChoiceBox<String>)secondScene.lookup("#cb6")).getSelectionModel().select(2);
	        
	        ((ColorPicker)secondScene.lookup("#cp1")).setValue(Color.web("#1a3399"));
	        ((ColorPicker)secondScene.lookup("#cp2")).setValue(Color.web("#b31a1a"));
	        ((ColorPicker)secondScene.lookup("#cp3")).setValue(Color.web("#1a4d1a"));
	        ((ColorPicker)secondScene.lookup("#cp4")).setValue(Color.web("#b3b31a"));
	        ((ColorPicker)secondScene.lookup("#cp5")).setValue(Color.web("#4d1a4d"));
	        ((ColorPicker)secondScene.lookup("#cp6")).setValue(Color.web("#336666"));
	        ((Button)secondScene.lookup("#bClose")).setOnMouseClicked(e -> {
	        	Runtime.getRuntime().exit(0);
			});
	        ((Button)secondScene.lookup("#bCancel")).setOnMouseClicked(e -> {
	        	secondStage.hide();
	        	primaryStage.show();
	        	gameLogic.renderer.startRendering();	
			});
	        ((Button)secondScene.lookup("#bStart")).setOnMouseClicked(e -> {
	        	
	    		Player[] player =  new Player[]{
	    				new Player("player1",((ColorPicker)secondScene.lookup("#cp1")).getValue(),PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb1")).getSelectionModel().getSelectedIndex()]),
	    				new Player("player2",((ColorPicker)secondScene.lookup("#cp2")).getValue(),PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb2")).getSelectionModel().getSelectedIndex()]),
	    				new Player("player3",((ColorPicker)secondScene.lookup("#cp3")).getValue(),PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb3")).getSelectionModel().getSelectedIndex()]),
	    				new Player("player4",((ColorPicker)secondScene.lookup("#cp4")).getValue(),PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb4")).getSelectionModel().getSelectedIndex()]),
	    				new Player("player5",((ColorPicker)secondScene.lookup("#cp5")).getValue(),PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb5")).getSelectionModel().getSelectedIndex()]),
	    				new Player("player6",((ColorPicker)secondScene.lookup("#cp6")).getValue(),PlayerControl.values()[((ChoiceBox<String>)secondScene.lookup("#cb6")).getSelectionModel().getSelectedIndex()])
	    			};
	    		
	    		gameLogic.initGame(player);
	        	secondStage.hide();
	        	primaryStage.show();
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
		});
	
		scene.setOnMouseDragged(e -> {
			if (mMouseDown) camera.onMouseDrag(e);
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
