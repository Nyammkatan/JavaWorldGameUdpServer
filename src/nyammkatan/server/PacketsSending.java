package nyammkatan.server;

import org.json.simple.JSONArray;
import java.lang.Number;

import nyammkatan.game.User;
import nyammkatan.world.World;

public class PacketsSending {
	
	public static void sendChunkLine(Client client, User user, World world) {
		int chunkSize = World.SCREEN_WIDTH/World.SCREEN_SCALE/World.TILE_SIZE;
		int j = user.x/World.TILE_SIZE-chunkSize/2;
		int i = user.y/World.TILE_SIZE-chunkSize/2;
		if (i < 0) i = 0;
		if (j < 0) j = 0;
		if (i > world.h-20) i = world.h-20;
		if (j > world.w-20) j = world.w-20;
		for (int ii=i; ii < i+chunkSize; ii++) {
			int[] chunkLineArray = new int[chunkSize];
			int beginjj = j;
			for (int jj=j; jj < j+chunkSize; jj++) {
				chunkLineArray[jj-beginjj] = world.array[ii][jj];
			}
			Packet linePacket = client.createSimplePacket(P_IDS.MAP_CHUNK_LINE);
			linePacket.putData("i", ii);
			linePacket.putData("j", beginjj);
			JSONArray a = new JSONArray();
			for (int c=0; c < chunkLineArray.length; c++) {
				a.add(chunkLineArray[c]);
			}
			linePacket.putData("data", a);
			client.send(linePacket);
			
		}
		
	}

}
