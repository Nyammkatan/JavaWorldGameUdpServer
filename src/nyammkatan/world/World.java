package nyammkatan.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import nyammkatan.server.Server;

public class World {
	
	public int w = 1000;
	public int h = 1000;

	public static final int DIRT = 1;
	public static final int DIRT_FLOOR = 0;
	public static final int ICE = 7;
	public static final int ICE_FLOOR = 6;
	public static final int JUNGLE = 3;
	public static final int JUNGLE_FLOOR = 2;
	public static final int FIRE = 9;
	public static final int FIRE_FLOOR = 8;
	public static final int SAND = 5;
	public static final int SAND_FLOOR = 4;
	public static final int RUINS = 11;
	public static final int RUINS_FLOOR = 10;
	public static final int NEON = 15;
	public static final int NEON_FLOOR = 14;
	public static final int SEW = 13;
	public static final int SEW_FLOOR = 12;
	public static final int WATER = 20;
	public static final int LAVA = 21;
	public static final int WATER_DEEP = 22;
	
	public static final int DS_DETAIL = 10;
	
	public boolean isBlock(int i, int j) {
		if (this.array[i][j] % 2 != 0 && this.array[i][j] != World.LAVA) {
			return true;
		}
			return false;
	}

	public final static HashMap<Integer, Color> BLOCK_COLORS = new HashMap<Integer, Color>();
	static {
		BLOCK_COLORS.put(DIRT, new Color(89, 59, 43));
		BLOCK_COLORS.put(DIRT_FLOOR, new Color(143, 101, 79));
		BLOCK_COLORS.put(JUNGLE, new Color(0, 110, 9));
		BLOCK_COLORS.put(JUNGLE_FLOOR, new Color(33, 173, 44));
		BLOCK_COLORS.put(ICE, new Color(0, 88, 117));
		BLOCK_COLORS.put(ICE_FLOOR, new Color(36, 170, 214));
		BLOCK_COLORS.put(FIRE, new Color(66, 3, 3));
		BLOCK_COLORS.put(FIRE_FLOOR, new Color(152, 123, 124));
		BLOCK_COLORS.put(SAND, new Color(135, 117, 0));
		BLOCK_COLORS.put(SAND_FLOOR, new Color(219, 196, 44));
		BLOCK_COLORS.put(RUINS, new Color(0, 0, 0));
		BLOCK_COLORS.put(RUINS_FLOOR, new Color(100, 100, 100));
		BLOCK_COLORS.put(SEW, new Color(0, 72, 0));
		BLOCK_COLORS.put(SEW_FLOOR, new Color(0, 100, 0));
		BLOCK_COLORS.put(WATER, new Color(0, 0, 255));
		BLOCK_COLORS.put(LAVA, new Color(255, 0, 0));
		BLOCK_COLORS.put(WATER_DEEP, new Color(0, 0, 127));
		
	}

	public String name;
	public int[][] array;
	long seed = 0;
	
	public World(String name, Long seed, int[][] array) {
		this.name = name;
		if (seed != null) {
			Server.random.setSeed(seed);
			this.seed = seed;
			
		} else {
			this.seed = Server.random.nextLong();
			
		}
		if (array == null) {
			this.array = new int[this.h][this.w];
			this.generateWorld(6f);
			this.drawMap();
		} else {
			this.array = array;
			
		}
		
	}
	
	public float[][] generateNoiseMap(float res) {
		float[][] narray = new float[this.h][this.w];
		OpenSimplexNoise noise = new OpenSimplexNoise(this.seed);
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				float value = (float) noise.eval(j/res, i/res);
				narray[i][j] = (int)((value+1)/2f * 255f + 0.5f);
				narray[i][j]/=255f;
				
			}
			
		}
		return narray;
		
	}

	public void generateCaves(float res) {
		float[][] map = this.generateNoiseMap(res);
		//this.drawTestImage(map, "caves");
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				this.array[i][j] = map[i][j] >= 0.5f ? 1 : 0;
				
			}
			
		}
		
	}
	
	public void setBiom(float[][] biomsMap, float start, float end, int value) {
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				if (biomsMap[i][j] >= start && biomsMap[i][j] < end)
				this.array[i][j] = this.array[i][j] == 1 ? value : value-1;
				
			}
			
		}
		
	}
	
	public float getAmountOfWaterInPerc(float[][] array) {
		float result = 0;
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				if (array[i][j] <= WATER_EDGE) {
					++result;
				}
			}
		}
		return result/(this.w*this.h);
		
	}
	
	public static final float WATER_EDGE = 0.35f;
	
	public float[][] getDSforWater(){
		return new DS(World.DS_DETAIL).setRandom(Server.random).generate(0.02f).bringToZeroToOne().map;
	}
	
	public void setWaterToPerc(float perc, float[][] water) {
		float percWater = this.getAmountOfWaterInPerc(water);
		boolean bigger = percWater > perc;
		while (percWater <= perc-0.05f || percWater > perc+0.05f) {
			for (int i=0; i < this.h; i++) {
				for (int j=0; j < this.w; j++) {
					if (bigger)
						water[i][j] = Math.min(1f, water[i][j]+0.01f);
					else
						water[i][j] = Math.min(1f, water[i][j]-0.01f);
					
				}
			}
			percWater = this.getAmountOfWaterInPerc(water);
			
		}
		
	}
	
	public float[][] generateWater(float perc) {
		float[][] test = getDSforWater();
		this.setWaterToPerc(perc, test);
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				if (test[i][j] < World.WATER_EDGE)
					this.array[i][j] = World.WATER;
				if (test[i][j] < World.WATER_EDGE*0.72f)
					this.array[i][j] = World.WATER_DEEP;
				
			}
		}
		this.generateBeach(test);
		return test;
		
	}
	
	public void generateBeach(float[][] test) {
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				if (this.array[i][j] != World.WATER) {
					if (test[i][j] >= World.WATER_EDGE+0.03f && test[i][j] < World.WATER_EDGE+0.07f)
						if (this.isBlock(i, j))
							this.array[i][j] = World.DIRT;
						else
							this.array[i][j] = World.DIRT_FLOOR;
					if (test[i][j] >= World.WATER_EDGE && test[i][j] < World.WATER_EDGE+0.03f)
						if (this.isBlock(i, j))
							this.array[i][j] = World.SAND;
						else
							this.array[i][j] = World.SAND_FLOOR;
					if (test[i][j] >= World.WATER_EDGE && test[i][j] < World.WATER_EDGE+0.01f)
						this.array[i][j] = World.SAND_FLOOR;
				}
			}
		}
		
	}
	
	public void generateRivers() {
		float[][] riversMap = new DS(World.DS_DETAIL).setRandom(Server.random).generate(1f).bringToZeroToOne().map;
		this.drawTestImage(riversMap, "testtest");
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				if (riversMap[i][j] < World.WATER_EDGE && riversMap[i][j] >= World.WATER_EDGE-0.02f) {
					if (this.array[i][j] != World.FIRE && this.array[i][j] != World.FIRE_FLOOR)
						this.array[i][j] = World.WATER;
					
				}
			}
		}
		//this.generateBeach(riversMap);
		
	}
	
	public void generateBioms(float res) {
		float[][] biomsMap = new DS(World.DS_DETAIL).setRandom(Server.random).generate(1f).bringToZeroToOne().map;
		this.setBiom(biomsMap, 0.9f, 1f, World.ICE);
		this.setBiom(biomsMap, 0.7f, 0.832f, World.FIRE);
		this.setBiom(biomsMap, 0.45f, 0.6f, World.JUNGLE);
		this.setBiom(biomsMap, 0.35f, 0.42f, World.SAND);
		this.setBiom(biomsMap, 0.02f, 0.25f, World.ICE);
		this.generateRivers();
		this.generateRivers();
		this.generateWater(0.32f);
		
	}
	
	public void generateWorld(float res) {
		System.out.println("Generating world");
		this.generateCaves(res);
		this.generateBioms(res);
		
	}
	
	public void drawTestImage(float[][] map, String title) {
		BufferedImage im = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_RGB);
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				im.setRGB(j, i, new Color(0, 0, (int)(map[i][j]*255)).getRGB());
				
			}
			
		}
		try {
			ImageIO.write(im, "PNG", new File(title+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    public void drawMap() {
        System.out.println("Drawing map");
        BufferedImage im = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_RGB);
		for (int i=0; i < this.h; i++) {
			for (int j=0; j < this.w; j++) {
				im.setRGB(j, i, BLOCK_COLORS.get(this.array[i][j]).getRGB());
				
			}
			
		}
		try {
			ImageIO.write(im, "PNG", new File("map.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Map drawn");
        
    }

}
