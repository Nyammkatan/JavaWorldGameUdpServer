package nyammkatan.server;

import java.util.Set;
import java.util.stream.Collectors;

public class Game extends GameHandler {

	Game() {
		super(new Server(9999, 1000));
		System.out.println("Starting server");
		this.startGameLogic();
		
	}

	@Override
	public void newClientJoined(Client c) {
		System.out.println("new client "+c.addr.toString());
		
	}

	@Override
	protected void getSimplePacket(Client client, Packet packet) {
		client.ready = true;
		
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

	@Override
	public void update(float delta) {
		
		
	}

	@Override
	public void stateAction() {
		
		
	}

}
