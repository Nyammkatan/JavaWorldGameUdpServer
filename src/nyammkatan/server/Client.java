package nyammkatan.server;

import java.net.SocketAddress;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public class Client {
	
	public Object binding;
	
	public boolean ready = false;
	ArrayList<Packet> iMessages = new ArrayList<Packet>();

    public int lastPacketNumber = -1;
    public int lastIPacketNumber = -1;

    public int ni_packet_number_counter = 0;
    public int i_packet_number_counter = 0;
    
    public SocketAddress addr;
    public Server server;
    public long lastMessageTime;
    
    public Client(SocketAddress addr, Server server) {
    	this.addr = addr;
        this.server = server;
        this.lastMessageTime = System.currentTimeMillis();
    	
    }
    
    public void addNewIMessage(Packet packet) {
        iMessages.add(packet);
        
    }

    public Packet getFirstIMessage() {
        return iMessages.get(0);
        
    }

    public void removeFirstIMessage() {
        iMessages.remove(0);
        
    }

    public boolean iMessagesExist() {
        return iMessages.size() > 0;
    
    }
        		
    public void holdConnection() {
        lastMessageTime = System.currentTimeMillis();
    
    }
        		
    public boolean checkConnection(long milliseconds) {
        if ((System.currentTimeMillis() - lastMessageTime) > milliseconds)
            return false;
        else
            return true;
        
    }

    public void send(Packet packet) {
    	server.send(addr, packet);
    	
    }

    public int getCounter(boolean important) {
        int value = 0;
        if (!important) {
        	value = ni_packet_number_counter;
            ni_packet_number_counter+=1;
            if (ni_packet_number_counter >= server.maxPacketNumberRange) {
            	ni_packet_number_counter = 0;
            	
            }
        	
        }
        else {
        	value = i_packet_number_counter;
            i_packet_number_counter+=1;
            if (i_packet_number_counter >= server.maxPacketNumberRange) {
            	i_packet_number_counter = 0;
                        		
            }
        	
        }
        return value;
        		
    }
    
    public Packet createSimplePacket(int p_id) {
        Packet packet = new Packet(Packet.SIMPLE, p_id, this.getCounter(false));
        return packet;
        		
    }
    
    public Packet createIPacket(int p_id) {
    	Packet packet = new Packet(Packet.IMPORTANT, p_id, this.getCounter(true));
    	return packet;
    	
    }
    
    public boolean checkPacketNumber(int numberReceived) {
        if ( (numberReceived > lastPacketNumber) || ( (numberReceived < (server.maxPacketNumberRange/2)) && (lastPacketNumber > (server.maxPacketNumberRange/2)) ) ) {
            lastPacketNumber = numberReceived;
            return true;
        }
        return false;
        
    }

    public boolean checkIPacketNumber(int numberReceived) {
        if ( (numberReceived > lastIPacketNumber) || ( (numberReceived < (server.maxPacketNumberRange/2)) && (lastIPacketNumber > (server.maxPacketNumberRange/2)) ) ) {
            lastIPacketNumber = numberReceived;
            return true;
        }
        return false;
        		
    }
    
}
