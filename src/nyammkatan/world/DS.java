package nyammkatan.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class DS {
	
	public int size;
	public int max;
	public float[][] map;
	
	public Random r = new Random();
	
	public DS setRandom(Random r) {
		this.r = r;
		return this;
		
	}
	
	public DS setSeed(long seed) {
		this.r.setSeed(seed);
		return this;
		
	}
	
	public DS(int detail) {
		this.size = (int) (Math.pow(2, detail)+1);
		this.max = this.size - 1;
		this.map = new float[size][size];
		
	}
	
	public DS bringToZeroToOne() {
		float maxValue = 0;
		for (int i=0; i < this.size; i++) {
			for (int j=0; j < this.size; j++) {
				if (this.map[i][j] > maxValue) {
					maxValue = this.map[i][j];
					
				}
				
			}
			
		}
		float minValue = 2000;
		for (int i=0; i < this.size; i++) {
			for (int j=0; j < this.size; j++) {
				if (this.map[i][j] < minValue) {
					minValue = this.map[i][j];
					
				}
				
			}
			
		}
		for (int i=0; i < this.size; i++) {
			for (int j=0; j < this.size; j++) {
				this.map[i][j] = li(0f, 1f, minValue, this.map[i][j], maxValue);
				
			}
			
		}
		return this;
		
	}
	
	public float li(float fx1, float fx2, float x1, float x, float x2) {
		if (x >= x2)
			return fx2;
		if (x < x1)
			return fx1;
		return fx1+(fx2 - fx1)*(x - x1)/(x2 - x1);
	
	}
	
	public void set(int x, int y, float value) {
		this.map[y][x] = value;
		
	}
	
	public float get(int x, int y) {
		if (x < 0 || x >= this.size || y < 0 || y >= this.size)
			return -1;
		return this.map[y][x];
		
	}
	
	static class Point{
		int x;
		int y;
		Point(int x, int y){
			this.x = x;
			this.y = y;
			
		}
		
		public void set(int x, int y) {
			this.x = x;
			this.y = y;
			
		}
		
	}
	
	public DS inverse() {
		for (int i=0; i < this.size; i++) {
  			for (int j=0; j < this.size; j++) {
  				this.map[i][j] = Math.abs(this.map[i][j]-1f);
  			}
		}
		return this;
		
	}
	
	public DS maskCircle(float mdist) {
		for (int i=0; i < this.size; i++) {
  			for (int j=0; j < this.size; j++) {
  				float distance_x = Math.abs(j - this.size * 0.5f);
  				float distance_y = Math.abs(i - this.size * 0.5f);
  				float dist = (float) Math.sqrt(distance_x*distance_x + distance_y*distance_y);
  				float max_width = this.size * 0.5f + mdist;
  				float delta = dist / max_width;
  				float gradient = delta * delta;
  				this.map[i][j] *= Math.max(0.0f, 1.0f - gradient);
  			}
		}
		return this;
		
	}
	
	public DS generateLeft(float roughness, DS parent) {
		DS self = this;
		//this.set(0, 0, self.max / 2);
		//this.set(0, this.max, self.max / 2);
		for (int i = 0; i < self.size; i++) {
			self.map[0][i] = parent.map[i][this.max];
		}
		
		this.set(this.max, 0, self.max / 2);
		this.set(this.max, this.max, self.max / 2);
		
		divide(this.max, roughness);
		return this;
		
	}
	
	public DS generate(float roughness) {
		DS self = this;
		this.set(0, 0, self.max / 2);
		this.set(this.max, 0, self.max / 2);
		this.set(this.max, this.max, self.max / 2);
		this.set(0, this.max, self.max / 2);
		
		divide(this.max, roughness);
		return this;
		
	}
	
	public void divide(int size, float roughness) {
		  DS self = this;
		  int x, y, half = size / 2;
		  float scale = roughness * size;
		  if (half < 1) return;
	
		  for (y = half; y < self.max; y += size) {
		    for (x = half; x < self.max; x += size) {
		      square(x, y, half, (float) (r.nextFloat() * scale * 2 - scale));
		    }
		  }
		  for (y = 0; y <= self.max; y += half) {
		    for (x = (y + half) % size; x <= self.max; x += size) {
		      diamond(x, y, half, (float) (r.nextFloat() * scale * 2 - scale));
		    }
		  }
		  divide(size / 2, roughness);
	  
	}
	
	public float average(float[] values) {
		ArrayList<Float> valuesList = new ArrayList<Float>();
		for (float num : values) valuesList.add(num);
		List<Float> valuesList2 = valuesList.stream().filter((val) -> val != -1).collect(Collectors.toList());
        float total = valuesList2.stream().reduce(0f, (sum, val) -> sum + val);
        return total / valuesList2.size();
        
      }

      public void square(int x, int y, int size, float offset) {
    	DS self = this;
        float ave = average(new float[]{
          self.get(x - size, y - size),   // upper left
          self.get(x + size, y - size),   // upper right
          self.get(x + size, y + size),   // lower right
          self.get(x - size, y + size)    // lower left
        });
        self.set(x, y, ave + offset);
      }

      public void diamond(int x, int y, int size, float offset) {
    	DS self = this;
        float ave = average(new float[] {
          self.get(x, y - size),      // top
          self.get(x + size, y),      // right
          self.get(x, y + size),      // bottom
          self.get(x - size, y)       // left
        });
        self.set(x, y, ave + offset);
      }
      
      public DS drawTestImage(String title) {
  		BufferedImage im = new BufferedImage(this.size, this.size, BufferedImage.TYPE_INT_RGB);
  		for (int i=0; i < this.size; i++) {
  			for (int j=0; j < this.size; j++) {
  				try {
  				im.setRGB(j, i, new Color(0, 0, (int)(map[i][j]*255)).getRGB());
  				} catch (Exception e) {
  					System.out.println(map[i][j]);
  					System.exit(0);
  					
  				}
  				
  			}
  			
  		}
  		try {
  			ImageIO.write(im, "PNG", new File(title+".png"));
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		return this;
  		
  	}
	

}
