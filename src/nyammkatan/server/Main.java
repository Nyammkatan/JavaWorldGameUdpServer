package nyammkatan.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import nyammkatan.world.DS;
import nyammkatan.world.OpenSimplexNoise;
import nyammkatan.world.World;

public class Main {

	public static void main(String[] args) {
		Server.random.setSeed(123234);
		World w = new World("Test World", null, null);
		Game game = new Game(w);
		
	}
	
	//seeds i like
	//123234

}
