package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import org.tribot.api.Screen;
import org.tribot.api.types.colour.Tolerance;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "J J" }, category = "Test", name = "MMtoMS")
public class MMtoMs extends Script implements Painting {
	final int MMX1 = 551, MMY1 = 59, MMXL = 151, MMYL = 151;
	int[][][] MSBoxes = new int[13][8][];
	int[][][] MMBoxes = new int[13][8][];
	
	boolean[][] map = new boolean[151][151];
	Point[] room;
	
	public void onPaint(Graphics g){
		
		if (MSBoxes.length > 0){
			g.setColor(Color.GRAY);
			for (int i = 0; i < MSBoxes.length; i++)
				for (int j = 0; j < MSBoxes[i].length; j++)
					g.drawRect(MSBoxes[i][j][0], MSBoxes[i][j][1], MSBoxes[i][j][2]-MSBoxes[i][j][0], MSBoxes[i][j][3]-MSBoxes[i][j][1]);
		}
		
		
		if (MMBoxes.length > 0){
			g.setColor(Color.RED);
			for (int i = 0; i < MMBoxes.length; i++)
				for (int j = 0; j < MSBoxes[i].length; j++)
					g.drawRect(MMBoxes[i][j][0], MMBoxes[i][j][1], MMBoxes[i][j][2]-MMBoxes[i][j][0], MMBoxes[i][j][3]-MMBoxes[i][j][1]);
		}
			
		/*
		g.setColor(Color.YELLOW);
		if (NPCBoxes.length > 0)
			for (int i = 0; i < NPCBoxes.length; i+=4)
				g.drawRect(NPCBoxes[i+0], NPCBoxes[i+1], NPCBoxes[i+2]-NPCBoxes[i+0], NPCBoxes[i+3]-NPCBoxes[i+1]);
	    */
	}


	/**
	private Point MMtoMS(Point P){ // Credits to Narcle
		int X = P.x - 627;
		int Y = P.y - 85 - 50;
		double yM = 36.0;
		double Yh = Math.abs(Y + yM);
		
		P.x = (int)(263.5 + (X * 9.75 / (12.8 - (Yh / yM * 3.15))) * 9.75);
		P.y = (int)(182.0 + Y * (6.3 + ((Yh / yM) * 2.4)) + 50);
		return P;
	}
	**/
	
	private void getRoom(){
		Color A = new Color(0, 0, 0);
		Color B = new Color(0, 0, 0);

		// Get all pixels that aren't black from the minimap, store in a 2d boolean array
		for (int i = 0; i < map.length; i++){
			for (int j = 0; j < map[i].length; j++){
				double dist = Math.pow(MMX1 + (MMXL/2) - (i + MMX1), 2) + Math.pow(MMY1 + (MMYL/2) - (j + MMY1), 2);
				if (dist < (Math.pow(MMXL/2, 2))){
					B = Screen.getColourAt(new Point(i + MMX1, j + MMY1));
					if (!Screen.coloursMatch(A, B, new Tolerance(10))){
						map[i][j] = true;
					}
				}
			}
		}
		
		// Flood-fill to get the current room
		ArrayList<Point> stack = new ArrayList<Point>();
		stack.add(new Point(627, 135));
		int[][] offset = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
		int i = 0;
		
		while (stack.size() > i){
			Point convert = new Point(stack.get(i).x-MMX1, stack.get(i).y-MMY1);

			for (int j = 0; j < offset.length; j++){
				Point PT = new Point(convert.x + offset[j][0], convert.y + offset[j][1]);
				if (map[PT.x][PT.y]){
					stack.add(new Point(PT.x + MMX1, PT.y + MMY1));
					map[PT.x][PT.y]= false; 
				}
			}
			
			i++;
		}
		
		room = stack.toArray(new Point[stack.size()]);
	}
	
	private Point MMtoMS(Point P){
		int	MSX1  = 4, MSY1 = 54, MSX2 = 515, MSY2 = 387,
			MMX1 = 602, MMY1 = 118, MMX2 = 654, MMY2 = 150;
		ArrayList<Integer> npcList = new ArrayList<Integer>();
		
		for (int i = 0; i <= 12; i++)
			for (int j = 0; j <= 7; j++){
				MSBoxes[i][j] = new int[] { (MSX2 - MSX1) / 14 * i + MSX1 + 22,
						       			    (MSY2 - MSY1) / 9 * j + MSY1 + 16,
						       			    (MSX2 - MSX1) / 14 * i + MSX1 + 22 + ((MSX2 - MSX1) / 14),
						       			    (MSY2 - MSY1) / 9 * j + MSY1 + 16 + ((MSY2 - MSY1) / 9) };
				
				MMBoxes[i][j] = new int[] { (MMX2 - MMX1) / 13 * i + MMX1,
						       			    (MMY2 - MMY1) / 7 * j + MMY1,
						       			    (MMX2 - MMX1) / 13 * i + MMX1 + ((MMX2 - MMX1) / 13),
						       			    (MMY2 - MMY1) / 7 * j + MMY1+ ((MMY2 - MMY1) / 7) };
				
				if (Screen.findColours(new Color(240, 240, 0), MMBoxes[i][j][0], MMBoxes[i][j][1], MMBoxes[i][j][2], MMBoxes[i][j][3], new Tolerance(20)).length > 0){
					println("NPC found around coords: [X1: " + MMBoxes[i][j][0] + " Y1: " + MMBoxes[i][j][1] + " X2: " + MMBoxes[i][j][2] + " Y2: " + MMBoxes[i][j][3]);
					
					for (int k = 0; k <= 3; k++)
						npcList.add(MSBoxes[i][j][k]);
				}
				
			}

		//NPCBoxes = (Integer[]) npcList.toArray(new Integer[npcList.size()]);

		return P;
	}
	
	private boolean findStuff(){
		ArrayList<Point> npcList = new ArrayList<Point>();
		ArrayList<Point> itemList = new ArrayList<Point>();
		Color npc = new Color(240, 240, 0);
		Color item = new Color(245, 15, 15);
		
		Point[] coords = room;
		
		for (int i = 0; i < coords.length; i++){
			int x = coords[i].x;
		    int y = coords[i].y;
			if (Screen.findColour(npc, x, y, x, y, new Tolerance(20)) != null)
				npcList.add(new Point(x, y));
			if (Screen.findColour(item, x, y, x, y, new Tolerance(20)) != null)
				itemList.add(new Point(x, y));
		}
		
		
		
		
		
		return false;
	}
		
	@Override
    public void run(){
		long start = System.currentTimeMillis();
		
		MMtoMS(new Point(50, 50));
		sleep(15000);
		//findStuff();
		
		println("Took: " + (long)(System.currentTimeMillis()-start) + " milliseconds");
	}


}
