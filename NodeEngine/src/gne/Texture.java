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


public class Texture extends Image{
	int id;
	String path;
	
	/*
	public Texture() {
		super();
		this.path = "";
	}
	*/
	public Texture(String path) {
		super(path);
		this.path = path;
	}

	public String getPath() {return path;}
}
