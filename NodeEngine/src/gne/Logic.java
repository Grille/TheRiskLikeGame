package gne;

import java.util.Timer;
import java.util.TimerTask;

public class Logic {
	Timer timer;
	public Game game;

	public Logic() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				gameTick();
			}
		}, 10,10);
	}
	public void start() {
		
	}
	public void stop() {
		
	}
	public void gameTick() {
		
	}
}
