package gne;

import gne.assets.*;
import gne.*;

import java.io.File;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;


public class Texture {
	int id;
	Image image = null;
	String path;
	
	public Texture() {
		this.path = "";
		image = null;
	}
	public Texture(String path) {
		this.path = path;
		image = new Image(path);
	}

	public String getPath() {return path;}
	public int getWidth() {return (int) image.getWidth();}
	public int getHeight() {return (int) image.getHeight();}
	
	
}
