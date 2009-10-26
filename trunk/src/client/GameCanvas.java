package client;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class GameCanvas extends Canvas implements MouseMotionListener, MouseListener
{
	final int PLAYER_CARD_Y = 320;
	EuchreApplet owner;
	int ex = 10;
	int why = 10;
	URL url, url2;
	char suit = 'n';

	BufferedImage img = null;
	BufferedImage card = null;
	Dimension offDimension;
	Image offImage;
	Graphics offGraphics;
	ArrayList<card> Cards;
	ArrayList<text> words = new ArrayList<text>();
	ArrayList<text> wordsVertical = new ArrayList<text>();
	boolean cardSelected = false;
	card selectedCard = null;
	int xCon = 0;
	int yCon = 0;
	int origX, origY;
	HashMap<Integer, Integer> validLocations = new HashMap<Integer, Integer>();
	BufferedImage suitImage;

	
	public void setOwner(EuchreApplet apl)
	{		
		validLocations.put(85, PLAYER_CARD_Y);
		validLocations.put(164, PLAYER_CARD_Y);
		validLocations.put(244, PLAYER_CARD_Y);
		validLocations.put(322, PLAYER_CARD_Y);
		validLocations.put(401, PLAYER_CARD_Y);
		validLocations.put(245, 215);
		
		
		Cards = new ArrayList<card>();
		owner = apl;
		addMouseMotionListener(this);
		addMouseListener(this);
		try 
		{
			URL url = new URL(owner.getCodeBase(), "background.jpg");
			img = ImageIO.read(url);


    	} 
		catch (IOException e) 
		{
		}
	}
	
	public void addCard(card c)
	{
		Cards.add(c);
	}
	
	public void addCard(char theSuit, int val, int xPos, int yPos)
	{
		Cards.add(new card(theSuit, val, xPos, yPos, owner));
	}

	public void drawText(String txt, int x, int y, EuchreApplet owner)
	{
		words.add(new text(txt, x, y, owner));
		repaint();
	}

	public void drawTextVertical(String txt, int x, int y, EuchreApplet owner)
	{
		wordsVertical.add(new text(txt, x, y, owner));
		repaint();
	}
	
	public void setSuit(char theSuit)
	{
		suit = theSuit;
	}
	
	public void removeSuit()
	{
		suit = 'n';
	}
	
	public void paint(Graphics g)
	{
		//Draw background
		g.drawImage(img, 0, 0, null);
		
		for(text t: words)
		{
			ArrayList<Image> images = t.getImages();
			int lastX = 0;
			int x = 0;
			for(Image i: images)
			{
				g.drawImage(i, t.getX() + x * 18, t.getY(), null);
				lastX = 18;
				++x;
			}
		}
		
		for(text t: wordsVertical)
		{
			ArrayList<Image> images = t.getImages();
			int lastY = 0;
			int y = 0;
			for(Image i: images)
			{
				g.drawImage(i, t.getX(), t.getY() + y * 18, null);
				lastY = 18;
				++y;
			}
		}
		
		if(!cardSelected)
		{	
			
			drawOtherPlayers(g);
			
			drawSuit(g);	
			
			for(card c: Cards)
				g.drawImage(c.getImage(), c.getX(), c.getY(), null);
		}
		else
		{
			drawOtherPlayers(g);
			
			drawSuit(g);			
			
			for(card c: Cards)
			{
				if(c != selectedCard)
					g.drawImage(c.getImage(), c.getX(), c.getY(), null);
			}
			g.drawImage(selectedCard.getImage(), selectedCard.getX(), selectedCard.getY(), null);
		}		
		

	}

	public void update(Graphics g) 
	{
		 BufferedImage image = (BufferedImage) createImage(getWidth(), getHeight());
		 Graphics2D imgGraphics = image.createGraphics();
		 
	     imgGraphics.setRenderingHint(
	             RenderingHints.KEY_ANTIALIASING,
	             RenderingHints.VALUE_ANTIALIAS_ON);
	     
	     paint(imgGraphics);
	     
	     g.drawImage(image, 0, 0, this);
		

	}
	
	public void drawSuit(Graphics g)
	{
		if(suit != 'n')
		{
			try 
			{
				URL suitURL = new URL(owner.getCodeBase(), suit +".gif");
				suitImage = ImageIO.read(suitURL);
			} 
			catch (MalformedURLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			g.drawImage(suitImage, 253, 140, null);
		}
	}
	
	public void drawOtherPlayers(Graphics g)
	{
		try
		{
			URL tempURL = new URL(owner.getCodeBase(), "cards/b2fh.gif");
			URL tempURL2 = new URL(owner.getCodeBase(), "cards/b2fv.gif");
			g.drawImage(ImageIO.read(tempURL), -40, 110, null);
			g.drawImage(ImageIO.read(tempURL), -40, 140, null);
			g.drawImage(ImageIO.read(tempURL), -40, 170, null);
			g.drawImage(ImageIO.read(tempURL), -40, 200, null);
			g.drawImage(ImageIO.read(tempURL), -40, 230, null);

			g.drawImage(ImageIO.read(tempURL), 500, 110, null);
			g.drawImage(ImageIO.read(tempURL), 500, 140, null);
			g.drawImage(ImageIO.read(tempURL), 500, 170, null);
			g.drawImage(ImageIO.read(tempURL), 500, 200, null);
			g.drawImage(ImageIO.read(tempURL), 500, 230, null);

			g.drawImage(ImageIO.read(tempURL2), 85, -60, null);
			g.drawImage(ImageIO.read(tempURL2), 164, -60, null);
			g.drawImage(ImageIO.read(tempURL2), 244, -60, null);
			g.drawImage(ImageIO.read(tempURL2), 322, -60, null);
			g.drawImage(ImageIO.read(tempURL2), 401, -60, null);
		}
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void mouseDragged(MouseEvent e) {

		if(cardSelected)
		{
			selectedCard.setX(e.getX() - xCon);
			selectedCard.setY(e.getY() - yCon);
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{

		for(card c: Cards)
		{
			if((e.getX() > c.getX()) && (e.getX() < c.getX() + 71))
			{
				if((e.getY() > c.getY()) && (e.getY() < c.getY() + 96))
				{
					xCon = e.getX() - c.getX();
					yCon = e.getY() - c.getY();
					cardSelected = true;
					selectedCard = c;
					origX = c.getX();
					origY = c.getY();
				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(cardSelected)
		{
			int xLoc = e.getX() - xCon;
			int yLoc = e.getY() - yCon;
			boolean broke = false;
			boolean contin = true;
	
			for(int x = -18; x < 18; x++)
			{
				for(int y = -18; y < 18; y++)
				{
					Integer value = validLocations.get(xLoc + x);
					if((value != null) && (value + y == yLoc))
					{		
						selectedCard.setX(xLoc + x);
						selectedCard.setY(value);
						y = 20;
						x = 20;
						broke = true;
					}
				}
			}
			if(broke)
			{
				for(card c:Cards)
				{
					if((c.getX() == selectedCard.getX()) && (c.getY() == selectedCard.getY()) && contin && c != selectedCard)
					{
						contin = false;
						c.setX(origX);
						c.setY(origY);
					}
				}
			}
			if(!broke)
			{
				if(selectedCard != null)
				{
					selectedCard.setX(origX);
					selectedCard.setY(origY);
				}
			}
			repaint();
		}
		
		cardSelected = false;
	}

}

class card
{
	char suit;
	int value, x, y;
	Image img;

	public card(char theSuit, int val, EuchreApplet owner)
	{
		suit = theSuit;
		value = val;
		URL url2;
		try {
			url2 = new URL(owner.getCodeBase(), "cards/" + theSuit + val + ".gif");
			img = ImageIO.read(url2);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setY(int y2) 
	{
		y = y2;
		
	}

	public void setX(int x2) 
	{
		x = x2;
		
	}

	public Image getImage() {
		return img;
	}

	public card(char theSuit, int val, int xPos, int yPos, EuchreApplet owner)
	{
		suit = theSuit;
		value = val;
		x = xPos;
		y = yPos;

		URL url2;
		try {
			url2 = new URL(owner.getCodeBase(), "cards/" + theSuit + val + ".gif");
			img = ImageIO.read(url2);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
}

class text
{
	String txt;
	int x, y;
	ArrayList<Image> letters = new ArrayList<Image>();
	
	public text(String word, int ex, int why, EuchreApplet owner)
	{
		for(int x = 0; x < word.length(); x++)
		{
			if(x != ' ')
			{
				char temp = word.charAt(x);
				URL url2;
				Image img;
				try {
					url2 = new URL(owner.getCodeBase(), "letters/" + temp + ".gif");
					img = ImageIO.read(url2);
					letters.add(img);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		txt = word;
		x = ex;
		y = why;
	}
	
	public ArrayList<Image> getImages()
	{
		return letters;
	}
	
	public String getWord()
	{
		return txt;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
