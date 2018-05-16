package gne.assets;

import java.io.File;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
public enum PlayerControl : Why?!
{
  Nothing,Human,Computer
}
*/
public class Player {
	
	private static int nextId;
	int id;
	public String name;
	public Color color;
	
	int typ;//0 empty, 1 player, 2 KI
	
	//Player = new
	public Player() {
		this("player", Color.GRAY);
	}
	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
		id = nextId;
		nextId++;
	}
}



