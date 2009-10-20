package client;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GameCanvas extends Canvas implements MouseMotionListener, MouseListener
{
	EuchreApplet owner;
	int ex = 10;
	int why = 10;
	URL url, url2;

	BufferedImage img = null;
	BufferedImage card = null;
	Dimension offDimension;
	Image offImage;
	Graphics offGraphics;
	ArrayList<card> Cards;
	boolean cardSelected = false;
	card selectedCard = null;
	int xCon = 0;
	int yCon = 0;

	
	public void setOwner(EuchreApplet apl)
	{
	
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
	
	public void paint(Graphics g)
	{
		update(g);

		
	}

	public void update(Graphics g) 
	{
		if(true)
		{	
			//Draw background
			g.drawImage(img, 0, 0, null);
			
			for(card c: Cards)
				g.drawImage(c.getImage(), c.getX(), c.getY(), null);
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
				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
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
