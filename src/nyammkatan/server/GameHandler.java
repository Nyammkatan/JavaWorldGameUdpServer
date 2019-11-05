package nyammkatan.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class GameHandler {
	
	public abstract void newClientJoined(Client c);
	protected abstract void getSimplePacket(Client client, Packet packet);
	protected abstract void getImportantPacket(Client client, Packet packet);
	protected abstract void disconnectOfClient(Client client);
	
	long lastFrameTime = System.currentTimeMillis();
	float timerStateUpdate = 0;
	HashMap<Integer, GameObject> allGameObjects = new HashMap<Integer, GameObject>();
	float stateUpdateTimeInterval = 0.1f;
	float timeBetweenResendingImportant = 0.08f;
	float resendingTimer = 0;
	boolean gameWorking = true;
	
	public Server server;
	GameHandler(Server server){
        this.server = server;
        this.server.setGame(this);
        
	}

    public void startGameLogic() {
    	long lastLoopTime = System.nanoTime();
    	final int TARGET_FPS = 60;
    	final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;   
    	
        while (gameWorking) {
        	try {
				server.reading();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);
            float dt = (float) delta/60;
            update(dt);
            this.resendingTimer+=dt;
            this.updateStates(dt);
            if (resendingTimer > timeBetweenResendingImportant) {
                resendingTimer -= timeBetweenResendingImportant;
                server.writing();
                
            }
        	
        }   
        
    }
    
    public void updateStates(float dt) {
        timerStateUpdate += dt;
        if (timerStateUpdate > stateUpdateTimeInterval) {
        	timerStateUpdate -= stateUpdateTimeInterval;
            sendIdAndState();
        	
        }   
            
    }
    
    public abstract Set<Integer> getFilterIdKeys(Client client, Set<Integer> idListKeys);
    public abstract void stateAction();

    public void sendIdAndState() {
    	for (Client client : this.server.getClientList().values()) {
            if (client.ready) {
                Set<Integer> idListKeys = getFilterIdKeys(client, this.allGameObjects.keySet());
                Packet packet = client.createSimplePacket(P_IDS.ID_PACKET);
                JSONArray id_array = new JSONArray();
                idListKeys.forEach((e)->{
                	id_array.add(e);
                });
                packet.putData("id", id_array);
                client.send(packet);
                for (Integer key: idListKeys) {
                	packet = client.createSimplePacket(P_IDS.STATE_PACKET);
                    packet = allGameObjects.get(key).getState(packet);
                    client.send(packet);
                	
                }
            }
    	}
        stateAction();
    	
    }
    
    public abstract void update(float delta);
	
}
