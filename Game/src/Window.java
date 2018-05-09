import javafx.application.Application;
import javafx.stage.Stage;

public class Window extends Application{
	private Stage primaryStage;

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		this.primaryStage = primaryStage;
	}
	
	public void show() {
        primaryStage.show(); 
	}
	public void show(boolean fullscreen) {
        primaryStage.show();
        primaryStage.setFullScreen(fullscreen);
	}

}
