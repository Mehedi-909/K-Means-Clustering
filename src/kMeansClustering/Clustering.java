package kMeansClustering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import java.util.stream.*;
public class Clustering {

	
	static ArrayList<Color>[] cluster;
	static Color centroid[];
	static Color tempCentroid[];
	static int numberOfCluster;
	
	private static double calculateDistance(Color c1, Color c2){
		double result = 0;
		
		result += Math.pow(c1.getRed() - c2.getRed(), 2);
		result += Math.pow(c1.getGreen() - c2.getGreen(), 2);
		result += Math.pow(c1.getBlue() - c2.getBlue(), 2);
		
		return Math.sqrt(result);
	}

	
	private static int bestMatch(Color color){
		int bestFItInCluster = 10000;
		double leastDiff = 10000;
		
		for(int i=0; i<numberOfCluster; i++){
			double diff = calculateDistance(color, centroid[i]); 
			
			if(diff < leastDiff){
				leastDiff = diff;
				bestFItInCluster = i;
			}
		}
		
		return bestFItInCluster;
	}
	
	
	
	
	static double eps=0.0000001;
	static double difference=0.0000000000001;
	
	
	public static void kMeans(){
		
		
		int i=1;
		while(difference<eps){
			
			i++;
			centroid = tempCentroid;
			
			ArrayList<Color>[] tempCluster = new ArrayList[numberOfCluster];
			for(int j=0; j<numberOfCluster; j++){
				tempCluster[j] = new ArrayList<Color>();
			}
			
			for(List<Color> list: cluster){
				for(Color color: list){
					int bestFit = bestMatch(color);
					tempCluster[bestFit].add(color);
				}
			}
			
			cluster = tempCluster;
			
			calculateCentroid();
			for(int m=0; m<numberOfCluster; m++){
				//double dif1 = (int)newCentroid[m]-(int)centroid[m];
				double a=centroid[m].getAlpha();
				double r=centroid[m].getRed();
				double g=centroid[m].getGreen();
				double b=centroid[m].getBlue();
				
				double a2=tempCentroid[m].getAlpha();
				double r2=tempCentroid[m].getRed();
				double g2=tempCentroid[m].getGreen();
				double b2=tempCentroid[m].getBlue();
				
				if(Math.abs(a2-a) < eps && Math.abs(r2-r) < eps && Math.abs(g2-g) < eps && Math.abs(b2-b) < eps){
					difference = 100;
					
				}
			}
			i++;
		}
		
		System.out.println("Total iteration: " + (i));
	}

	public static BufferedImage cluster(BufferedImage image){
		
		if(numberOfCluster==3){
			
			for(int i=0; i<image.getWidth(); i++){
				for(int j=0; j<image.getHeight(); j++){
				
					Color color = new Color(image.getRGB(i, j));
					int pos = bestMatch(color);
				
					Color selectedGroupColor = null;
					
					Color r = new Color(255, 0, 0);
					Color g = new Color(0, 255, 0);
					Color b = new Color(0, 0, 255);
				
					if(pos==0){
						image.setRGB(i, j, r.getRGB());
					}
					else if(pos==1){
						image.setRGB(i, j, g.getRGB());
					}
					else if(pos==2){
						image.setRGB(i, j, b.getRGB());
					}
						//selectedGroupColor = centroid[pos];
						
						//image.setRGB(i, j, selectedGroupColor.getRGB());
				}
			}
		}
		else{
		for(int i=0; i<image.getWidth(); i++){
			for(int j=0; j<image.getHeight(); j++){
			
				Color color = new Color(image.getRGB(i, j));
				int pos = bestMatch(color);
			
				Color selectedGroupColor = null;
			
			
					selectedGroupColor = centroid[pos];
					
					image.setRGB(i, j, selectedGroupColor.getRGB());
			}
		}
		}
		
		return image;
		
		
		
	}


	private static void calculateCentroid(){
		tempCentroid = new Color[numberOfCluster];
	
		for(int i=0; i<numberOfCluster; i++){
		
			int r=0, g=0, b=0;			
		
			for(Color color: cluster[i]){
				r += Math.pow(color.getRed(), 2);
				g += Math.pow(color.getGreen(), 2);
				b += Math.pow(color.getBlue(), 2);
			}
		
			int count = cluster[i].size();
		
			if(count != 0){
				r /= count;
				g /= count;
				b /= count;
			}
		
			r = (int) Math.sqrt(r);
			g = (int) Math.sqrt(g);
			b = (int) Math.sqrt(b);
		
		
		
			tempCentroid[i] = new Color(r, g, b);
		
		}
	}
 


	public static void groupColor(BufferedImage image){
		
		List<Color> allColors = new ArrayList<Color>();
		
		for(int i=0; i<image.getWidth(); i++){
			for(int j=0; j<image.getHeight(); j++){
				Color color = new Color(image.getRGB(i, j));
				allColors.add(color);
			}
		}
	
		
		
		Collections.sort(allColors, new Comparator<Color>() {
			public int compare(Color c1, Color c2){
				return c1.getRGB()-c2.getRGB();
			}
		});
	
		
		int pixelsInAGroup = allColors.size() / numberOfCluster;
	
		int colorIndex = 0;
		for(int i=0; i<numberOfCluster; i++){
			for(int j=0; j<pixelsInAGroup; j++){
				cluster[i].add(allColors.get(colorIndex++));
			}
		}
		
		calculateCentroid();
	}
	
	
	public static void main(String[] args) throws Exception{

	
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter number of cluster : ");
		numberOfCluster = sc.nextInt();
		
		File input = new File("src/kMeansClustering/fruit2.jpg");
		BufferedImage image = ImageIO.read(input);	
		
		File output = new File("src/kMeansClustering/fruit2Output.jpg");
		
		
		cluster = new ArrayList[numberOfCluster];
		
		for(int i=0; i<numberOfCluster; i++){
			cluster[i] = new ArrayList<Color>();
		}
		
		centroid = new Color[numberOfCluster];
		tempCentroid = new Color[numberOfCluster];
		
		
		groupColor(image);
		kMeans();
		image = cluster(image); 
		
		try {
			ImageIO.write(image, "jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(" Done");
		sc.close();
	}
	
}
