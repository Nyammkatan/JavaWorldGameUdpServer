package nyammkatan.server;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Packet {
	
	public JSONObject values;
	
	public static final String KEY_IM = "im";
	public static final String KEY_P_ID = "p_id";
	public static final String KEY_NUM = "num";
	
	public static final int SIMPLE = 0;
	public static final int IMPORTANT = 1;
	
	public static final int ID_PACKET = 0;
	public static final int STATE_PACKET = 1;
	public static final int RESPONSE = 2;
	
	public Packet(int im, int p_id, int packetNumber) {
		values = new JSONObject();
		values.put(KEY_IM, im);
		values.put(KEY_P_ID, p_id);
		values.put(KEY_NUM, packetNumber);
		
	}
	
	public Packet(String data) throws ParseException {
		this.values = (JSONObject) Server.jsonParser.parse(data);
		
	}
	
	public void putData(String key, Object o) {
		values.put(key, o);
		
	}
	
	public Object getData(String key) {
		return values.get(key);
		
	}
	
	public String toJson() {
		return values.toJSONString();
		
	}

}
