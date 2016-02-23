
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;




public class snakeCanvas extends Canvas implements Runnable, KeyListener
{
	public int eggcount = -2;
	static ArrayList<Point> eggList = new ArrayList<Point>();
	Iterator<Point> eg = eggList.iterator();
	//determines box height
	private final int BOX_HEIGHT= 15;
	private final int BOX_WIDTH= 15;
	private final int GRID_WIDTH= 25;
	private final int GRID_HEIGHT= 25;
	
	private LinkedList<Point> snake;
	private Point fruit;
	private Point egg;
	
	
	
	private int direction = Direction.NO_DIRECTION;
	
	private Thread runThread;
	//private Graphics globalGraphics;
	private int score = 0;
	private String highScore = "";
	
	private boolean isInMenu = true;
	private boolean hack = true;
	
	public void paint(Graphics g)
	//Draw Method
	{
		if (runThread == null)
		{
			this.setPreferredSize(new Dimension(640, 480));
			this.addKeyListener(this);
			runThread = new Thread(this);
			runThread.start();
		}
		
			if (snake == null)
			{
				snake = new LinkedList<Point>();
				GenerateDefaultSnake();
				
				//PlaceEgg();
			}
			if (highScore.equals(""))
			{
				//init the highscore
				highScore = this.GetHighScore();
			}
			
			
			DrawSnake(g);
			DrawGrid(g);
			DrawFruit(g);
			DrawEgg(g);
			DrawScore(g);
		}
	
		
	public void update(Graphics g)
	{
		
		//default update method, will contain double buffering
		Graphics offScreenGraphics; //these are the graphics we wil use to draw offscreen
		BufferedImage offscreen= null;
		Dimension d = this.getSize();
		
		offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offscreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint(offScreenGraphics);
		
		//flip
		g.drawImage(offscreen, 0, 0, this);
	}
	
	// The game resets
	public void GenerateDefaultSnake()
	{
		if(eggcount<-1){
		PlaceFruit();
		eggcount = +5;
		}
		eggList.clear();
		score = 0;
		snake.clear();
		
		
		
		snake.add(new Point(5,13));
		snake.add(new Point(5,14));
		snake.add(new Point (5,15));
		direction = Direction.NO_DIRECTION;
	
	}

	
		
		
		
		
	
	public void Move()
	{
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch (direction){
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint= new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint= new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint= new Point(head.x + 1, head.y);
			break;
		}
		
		snake.remove(snake.peekLast());
		System.out.println("Fruit is " + fruit);
		System.out.println(newPoint);
		if (newPoint.equals(fruit))
		{
			score+=1;
			//we got the fruit
			Point addPoint = (Point) newPoint.clone();
			
			switch (direction){
			case Direction.NORTH:
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH:
				newPoint= new Point(head.x, head.y + 1);
				break;
			case Direction.WEST:
				newPoint= new Point(head.x - 1, head.y);
				break;
			case Direction.EAST:
				newPoint= new Point(head.x + 1, head.y);
				break;
			}
			
			snake.push(addPoint);
			PlaceFruit();
			if ( score <20)
			{
			PlaceEgg();
			}
			Point fixed;
		}
		else if (snake.contains(egg))
		{
			//We hit the red dot
			CheckScore();
			GenerateDefaultSnake();
			eggcount = -2;
			return;
		}
		
		
		
		else if (snake.contains(eggList))
		{
			CheckScore();
			GenerateDefaultSnake();
			eggcount = -2;
			return;
		}
		
		else if (newPoint.x< 0 || newPoint.x > (GRID_WIDTH -1))
		{
			// we went out of bounds game over
			CheckScore();
			GenerateDefaultSnake();
			eggcount = -2;
			return;
			
		}
		else if (newPoint.y<0 || newPoint.y > (GRID_HEIGHT - 1 ))
		{
			//we went out of bounds game over
			CheckScore();
			GenerateDefaultSnake();
			eggcount = -2;
			return;
		}
		else if (snake.contains(newPoint))
		{
			//we ran into ourselves game over
			CheckScore();
			GenerateDefaultSnake();
			eggcount = -2;
			return;
		}
		
		//still alive
		snake.push(newPoint);
	}
	
	public void  DrawScore(Graphics g)
	{
		g.drawString("Score:" + score, 0, BOX_HEIGHT * GRID_HEIGHT + 10);
		g.drawString("Highscore: " + highScore, 0, BOX_HEIGHT * GRID_HEIGHT + 20 );
	}
	
	public void CheckScore()
	{
		System.out.println(highScore);
		if (highScore.equals(""))
			return;
		System.out.println(highScore);
		//format Brandon: score
		if (score > Integer.parseInt((highScore.split(":")[1])))
		{
			//user has set a new record
			String name = JOptionPane.showInputDialog("You set a new highscore, What is your name?" );
			highScore = name + ":" + score;
			
			File scoreFile = new File("highscore.dat");
			if (!scoreFile.exists())
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			FileWriter writeFile= null;
			BufferedWriter writer = null;
			try
			{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highScore);
			}
			catch (Exception e)
			{
				//errors
			}
			finally
			{
				try
				{
					if (writer != null)
						writer.close();
				}
				catch (Exception e) {}
			}
		}
	}
	
	public void DrawGrid(Graphics g)
	{
		//drawing an outside rectangle
		g.drawRect(0, 0, GRID_WIDTH * BOX_WIDTH, GRID_HEIGHT*BOX_HEIGHT);
		//drawing the verticle lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x+=BOX_WIDTH)
		{
			g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
		}
		//drawing the horizontal lines
		for (int y = BOX_HEIGHT; y < GRID_HEIGHT * BOX_HEIGHT; y+=BOX_HEIGHT)
		{
			g.drawLine(0, y, GRID_WIDTH * BOX_WIDTH, y);
		}
	}
	//Draws snake
	public void DrawSnake(Graphics g)
	{
		g.setColor(Color.ORANGE);
		for (Point p : snake)
		{
			g.fillRect(p.x* BOX_WIDTH, p.y* BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}
	

public void DrawFruit(Graphics g)
{
	g.setColor(Color.BLUE);
	g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
}

public void DrawEgg(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}


public void DrawEgg1(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg2(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg3(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg4(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg5(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg6(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg7(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg8(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}

public void DrawEgg9(Graphics g)
{
	for (int x=0; x<eggList.size(); x++){
	g.setColor(Color.RED);
	g.fillOval(eggList.get(x).x * BOX_WIDTH, eggList.get(x).y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
	g.setColor(Color.BLACK);
	}
}
public void PlaceFruit()
{
	Random rand = new Random ();
	int randomX = rand.nextInt(GRID_WIDTH);
	int randomY = rand.nextInt(GRID_HEIGHT);
	Point randomPoint = new Point(randomX, randomY);
	if (snake.contains(randomPoint))
	{
		randomX=rand.nextInt(GRID_WIDTH);
		randomY = rand.nextInt(GRID_HEIGHT);
		randomPoint = new Point(randomX, randomY);
	}
	
	
	fruit = randomPoint;
}



public void PlaceEgg()
{
	Random rand = new Random();
	int randomX = rand.nextInt(GRID_WIDTH);
	int randomY = rand.nextInt(GRID_HEIGHT);
	Point randomPointz = new Point(randomX, randomY);
	if (snake.contains(randomPointz))
	{
		randomX=rand.nextInt(GRID_WIDTH);
		randomY = rand.nextInt(GRID_HEIGHT);
		randomPointz = new Point(randomX, randomY);
	}
	else if (fruit.equals(randomPointz))
	{
		randomX=rand.nextInt(GRID_WIDTH);
		randomY = rand.nextInt(GRID_HEIGHT);
		randomPointz = new Point(randomX, randomY);
	}
	
	egg = randomPointz;
	eggList.add(egg);
	
}

  public static void main(String[] args) {
  	while (true){
  	System.out.println("Hello");	
  	}
  		
  }

@Override
public void run() {
	while (true)
	{
		//runs forever
		if (hack) {
			PlaceFruit();
			hack = false;
			System.out.println(hack);
		}
		//PlaceFruit();
		Move ();
		repaint();
		
		try
		{
			Thread.currentThread();
			Thread.sleep(50);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
public String GetHighScore()
{
	//format: Peyton:1
	FileReader readFile=null;
	BufferedReader reader= null;
	try
	{
	readFile  = new FileReader("highscore.dat");
	reader = new BufferedReader(readFile);
	return reader.readLine();
	}
	catch (Exception e)
	{
		return "Nobody:0";
	}
	finally
	{
		try {
		if (reader != null)
			reader.close();
		} catch (IOException e)  {
			e.printStackTrace();
		}
	}
	}
@Override
public void keyPressed(KeyEvent e) {
	switch (e.getKeyCode())
	{
	case KeyEvent.VK_UP:
		if (direction != Direction.SOUTH)
			direction = Direction.NORTH;
		break;
	case KeyEvent.VK_DOWN:
		if (direction != Direction.NORTH)
			direction = Direction.SOUTH;
		break;
	case KeyEvent.VK_RIGHT:
		if (direction != Direction.WEST)
			direction = Direction.EAST;
		break;
	case KeyEvent.VK_LEFT:
		if (direction !=Direction.EAST)
			direction = Direction.WEST;
		break;
	}
	
}
@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
}
