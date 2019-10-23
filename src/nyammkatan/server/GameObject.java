package nyammkatan.server;

public class GameObject {
	
	float x = 0;
	float y = 0;
	float a = 0;
	
	public void update(float delta) {
		
		
	}
	
	public static float truncate(float num) {
		int number = (int)(num*100);
		return number/100f;
		
	}

	public Packet getState(Packet packet) {
		packet.putData("x", truncate(x));
		packet.putData("y", truncate(y));
		packet.putData("a", truncate(a));
		return null;
	}

}
