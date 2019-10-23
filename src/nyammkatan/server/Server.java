package nyammkatan.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
	
	public static final JSONParser jsonParser = new JSONParser();
	public static final Random random = new Random();
	
	public HashMap<SocketAddress, Client> clientList = new HashMap<SocketAddress, Client>();
    public int maxPacketNumberRange = 1000;
    public int disconnectTime = 2000;
    
    public Selector selector;
    public DatagramChannel channel;
    
    public ByteBuffer receiveBuffer;
    public ByteBuffer sendingBuffer;
    
    public int bufferSize = 1024;
    
    public GameHandler worker;

    public Server(int port, int maxPacketNumberRange){
    	this.maxPacketNumberRange = maxPacketNumberRange;
        receiveBuffer = ByteBuffer.allocate(bufferSize);
        sendingBuffer = ByteBuffer.allocate(bufferSize);
        try {
			init(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public void setGame(GameHandler worker) {
    	this.worker = worker;
    	
    }
    
    public void init(int port) throws IOException {
    	selector = Selector.open();
    	channel = DatagramChannel.open();
    	channel.socket().bind(new InetSocketAddress(port));
    	channel.configureBlocking(false);
    	
    }
    
    public Message listen() throws IOException {
    	receiveBuffer.clear();
	    SocketAddress addr = channel.receive(receiveBuffer);
	    if (addr == null) return null;
	    String result = new String(receiveBuffer.array()).trim();
	    return new Message(addr, result);
    	
    }
    
    public void send(SocketAddress addr, Packet packet) {
    	sendingBuffer.clear();
    	sendingBuffer.put(packet.toJson().getBytes());
    	sendingBuffer.flip();
    	while (sendingBuffer.hasRemaining())
			try {
				channel.send(sendingBuffer, addr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    }
    
    public Client addClient(SocketAddress addr) {
    	Client c = new Client(addr, this);
    	this.clientList.put(addr, c);
    	return c;
    	
    }
    
    public void removeClient(SocketAddress addr) {
    	this.clientList.remove(addr);
    	
    }
    
    public HashMap<SocketAddress, Client> getClientList(){
    	return this.clientList;
    	
    }

    public void reading() throws IOException, ParseException {
        Message m = listen();
        if (m != null) {
        	Client c = null;
            if (!this.getClientList().containsKey(m.getSocketAddress())) {
                c = addClient(m.getSocketAddress());
                worker.newClientJoined(c);
                
            }
            else {
                c = getClientList().get(m.getSocketAddress());
                
            }
            Packet packet = new Packet(m.getData());
            receivingMessageFromClient(c, packet);
            
        }
    }

    public void getSimplePacket(Client client, Packet packet) {
        worker.getSimplePacket(client, packet);
        
    }

    public void getImportantPacket(Client client, Packet packet) {
    	worker.getImportantPacket(client, packet);
        
    }

    public void receivingMessageFromClient(Client client, Packet packet) {
        client.holdConnection();
        if (packet.getData(Packet.KEY_IM).equals((long)Packet.SIMPLE))
            if (client.checkPacketNumber((int)(long)packet.getData(Packet.KEY_NUM)))
                getSimplePacket(client, packet);
        else if (packet.getData(Packet.KEY_IM).equals((long)Packet.IMPORTANT)) {
            if (client.checkIPacketNumber((int)(long)packet.getData(Packet.KEY_NUM)))
                getImportantPacket(client, packet);
            client.send(new Packet(2, 0, (int)(long)packet.getData(Packet.KEY_NUM)));
        }
        else if (packet.getData(Packet.KEY_IM).equals((long)Packet.RESPONSE))
            client.removeFirstIMessage();
    
    }

    public void removeDisconnectedClients() {
    	Set<SocketAddress> keys = this.getClientList().keySet();
    	for (SocketAddress key : keys) {
    		Client client = getClientList().get(key);
    		if (!client.checkConnection(disconnectTime)) {
    			removeClient(key);
                worker.disconnectOfClient(client);
    			
    		}
    		
    	}
                
    }
               
                
    public void writing() {
        removeDisconnectedClients();
    	for (SocketAddress key : this.getClientList().keySet()) {
    		Client client = clientList.get(key);
            if (client.iMessagesExist()) {
            	Packet p = client.getFirstIMessage();
                client.send(p);
            	
            }
    		
    	}
    	
    }

}
