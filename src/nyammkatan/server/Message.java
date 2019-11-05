package nyammkatan.server;

import java.net.SocketAddress;

public class Message {
	
	private SocketAddress addr;
	private String data;
	
	public Message(SocketAddress addr, String data) {
		this.addr = addr;
		this.data = data;
		
	}
	
	public SocketAddress getSocketAddress() {
		return addr;
		
	}
	
	public String getData() {
		return data;
		
	}
	
	public void setData(String s) {
		this.data = s;
		
	}

}
