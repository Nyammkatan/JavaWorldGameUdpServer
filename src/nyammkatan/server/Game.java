package nyammkatan.server;

import java.util.Set;
import java.util.stream.Collectors;

import nyammkatan.game.User;
import nyammkatan.world.World;

public class Game extends GameHandler {
	
	public World world;

	Game(World world) {
		super(new Server(9999, 1000));
		System.out.println("Starting server");
		this.world = world;
		this.startGameLogic();
		
	}

	@Override
	public void newClientJoined(Client client) {
		System.out.println("new client "+client.addr.toString());
		if (client.binding == null) {
			User user = new User();
			client.binding = user;			
		}
		
	}

	@Override
	protected void getSimplePacket(Client client, Packet packet) {
		client.ready = true;
		User user = (User) client.binding;
		user.set(Server.getIntFromPacket(packet.getData("x")), Server.getIntFromPacket(packet.getData("y")));
		
	}

	@Override
	protected void getImportantPacket(Client client, Packet packet) {
		client.ready = true;
		
	}

	@Override
	protected void disconnectOfClient(Client client) {
		System.out.println("Disconnect "+client.addr.toString());
		
	}

	@Override
	public Set<Integer> getFilterIdKeys(Client client, Set<Integer> idListKeys) {
		//return idListKeys.stream().filter(id -> true).collect(Collectors.toSet());
		return idListKeys;
		
	}
	
	float timer = 0;
	@Override
	public void update(float delta) {
		timer += delta;
		if (timer >= 0.1f) {
			timer -= 0.1f;
			for (Client c: this.server.getClientList().values()) {
				PacketsSending.sendChunkLine(c, (User)c.binding, world);
				
			}
			
		}
		
	}

	@Override
	public void stateAction() {
		
		
	}

}
