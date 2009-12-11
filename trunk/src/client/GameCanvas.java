package client;

import game.Card;
import game.Player;

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

import utility.Opcode;
import utility.Trace;

public class GameCanvas extends Canvas implements MouseMotionListener, MouseListener
{
	// Locations that cards are able to go.
	final int PLAYER_CARD_Y = 320;
	final int PLAYER_X1 = 85;
	final int PLAYER_X2 = 164;
	final int PLAYER_X3 = 244;
	final int PLAYER_X4 = 322;
	final int PLAYER_X5 = 401;

	// The EuchreApplet that created this.
	EuchreApplet owner;
	int ex = 10;
	int why = 10;
	URL url, url2;
	char suit = 'n';
	text msg, msg2, msg3;

	BufferedImage img = null;
	BufferedImage card = null;
	Dimension offDimension;
	Image offImage;
	Graphics offGraphics;
	ArrayList<card> Cards;
	ArrayList<card> CardsH;
	ArrayList<text> words = new ArrayList<text>();
	ArrayList<text> wordsVertical = new ArrayList<text>();
	ArrayList<Image> button = new ArrayList<Image>();
	ArrayList<Player> players = new ArrayList<Player>();
	boolean cardSelected = false;
	card selectedCard = null;
	int xCon = 0;
	int yCon = 0;
	int origX, origY;
	HashMap<Integer, Integer> validLocations = new HashMap<Integer, Integer>();
	BufferedImage suitImage;
	Player player;
	boolean onOver = false;
	boolean offOver = false;
	card playPos;
	int openX, openY;
	private boolean gameStarted = false;
	private int buttons = 0;
	boolean aOver = false;
	boolean bOver = false;
	boolean cOver = false;
	boolean dOver = false;
	boolean h = true;
	boolean c = true;
	boolean d = true;
	boolean s = true;
	boolean dealtCardLocked = false;
	//used to handle reneges
	public char leadSuit;


	/**
	 * Set owner of this.
	 * 
	 * @param apl The EuchreApplet that created this.
	 */
	public void setOwner(EuchreApplet apl)
	{	
		// Add valid locations for this players card.
		validLocations.put(85, PLAYER_CARD_Y);
		validLocations.put(164, PLAYER_CARD_Y);
		validLocations.put(244, PLAYER_CARD_Y);
		validLocations.put(322, PLAYER_CARD_Y);
		validLocations.put(401, PLAYER_CARD_Y);
		validLocations.put(245, 215);

		// Stores all vertical cards that are on this screen.
		Cards = new ArrayList<card>();

		// Stores all horizontal cards that are on this screen.
		CardsH = new ArrayList<card>();

		owner = apl;

		addMouseMotionListener(this);
		addMouseListener(this);

		// Set background image.
		try 
		{
			URL url = new URL(owner.getCodeBase(), "background.jpg");
			img = ImageIO.read(url);
		} 
		catch (IOException e) 
		{
		}
	}

	/**
	 * Add a card to the screen.
	 * 
	 * @param c Card to add.
	 */
	public void addCard(card c)
	{
		if(c.getSuit() != ' ')
		{
			Cards.add(c);
		}
	}

	/**
	 * Add a card to the screen.
	 * 
	 * @param theSuit Suit of new card.
	 * @param val Value of new card.
	 * @param xPos X position of new card.
	 * @param yPos Y position of new card.
	 */
	public void addCard(char theSuit, int val, int xPos, int yPos)
	{
		if(theSuit != ' ')
		{
			boolean init = false;
			for(card c:Cards)
			{
				if(c.getSuit() == theSuit && c.getVal() == val)
				{
					init = true;
					//Cards.remove(c);
					c.setX(xPos);
					c.setY(yPos);
				}
			}
			//	if(!Cards.contains(new card(theSuit, val, xPos, yPos, owner)))
			if(!init)
				Cards.add(new card(theSuit, val, xPos, yPos, owner));
		}
		repaint();
	}

	/**
	 * Add a horizontal card to the screen.
	 * 
	 * @param theSuit Suit of new card.
	 * @param val Value of new card.
	 * @param xPos X position of new card.
	 * @param yPos Y position of new card.
	 */
	public void addCardH(char theSuit, int val, int xPos, int yPos)
	{
		boolean init = false;
		for(card c:CardsH)
		{
			if(c.getSuit() == theSuit && c.getVal() == val)
			{
				init = true;
				//Cards.remove(c);
				c.setX(xPos);
				c.setY(yPos);
			}
		}
		//	if(!Cards.contains(new card(theSuit, val, xPos, yPos, owner)))
		if(!init)
			CardsH.add(new card(theSuit, val, xPos, yPos, owner));
		repaint();
	}

	/**
	 * Draw text on the screen.
	 * 
	 * @param txt The text
	 * @param x X location
	 * @param y Y location
	 */
	public void drawText(String txt, int x, int y)
	{
		words.add(new text(txt, x, y, owner));
		repaint();
	}

	/**
	 * Draw text on the screen vertically.
	 * 
	 * @param txt
	 * @param x
	 * @param y
	 */
	public void drawTextVertical(String txt, int x, int y)
	{
		wordsVertical.add(new text(txt, x, y, owner));
		repaint();
	}

	/**
	 * Set trump suit. The suit that shows up on the middle of the canvas.
	 * 
	 * @param theSuit
	 */
	public void setSuit(char theSuit)
	{
		suit = theSuit;
		repaint();
	}

	/**
	 * Remove trump suit
	 */
	public void removeSuit()
	{
		suit = 'n';
	}

	/**
	 * Repaint the screen.
	 * 
	 */
	public void paint(Graphics g)
	{
		//Draw background
		g.drawImage(img, 0, 0, null);

		if(button != null)
		{
			if(button.size() == 4)
			{
				if(!onOver)
					g.drawImage(button.get(3), 320, 295, null);
				else
					g.drawImage(button.get(2), 320, 295, null);
				if(!offOver)
					g.drawImage(button.get(1), 364, 295, null);
				else
					g.drawImage(button.get(0), 364, 295, null);
			}
			else if(button.size() == 10)
			{
				if(!h)
				{
					if(!aOver)
						g.drawImage(button.get(8), 370, 295, null);
					else
						g.drawImage(button.get(9), 370, 295, null);
				}
				else if(!aOver)
					g.drawImage(button.get(0), 370, 295, null);
				else
					g.drawImage(button.get(4), 370, 295, null);


				if(!c)
				{
					if(!bOver)
						g.drawImage(button.get(8), 395, 295, null);
					else
						g.drawImage(button.get(9), 395, 295, null);
				}
				else if(!bOver)
					g.drawImage(button.get(1), 395, 295, null);
				else
					g.drawImage(button.get(5), 395, 295, null);

				if(!d)
				{
					if(!cOver)
						g.drawImage(button.get(8), 320, 295, null);
					else
						g.drawImage(button.get(9), 320, 295, null);
				}
				else if(!cOver)
					g.drawImage(button.get(2), 320, 295, null);
				else
					g.drawImage(button.get(6), 320, 295, null);

				if(!s)
				{
					if(!dOver)
						g.drawImage(button.get(8), 345, 295, null);
					else
						g.drawImage(button.get(9), 345, 295, null);
				}
				else if(!dOver)
					g.drawImage(button.get(3), 345, 295, null);
				else
					g.drawImage(button.get(7), 345, 295, null);
			}
		}

		if(msg != null)
		{
			ArrayList<Image> images = msg.getImages();
			int lastX = 0;
			int x = 0;
			int w = 0;
			for(Image i: images)
			{
				g.drawImage(i, msg.getX() + w, msg.getY(), null);
				lastX = 18;
				w = w + i.getWidth(this);
				++x;
			}			
		}

		if(msg2 != null)
		{
			ArrayList<Image> images = msg2.getImages();
			int lastX = 0;
			int x = 0;
			int w = 0;
			for(Image i: images)
			{
				g.drawImage(i, msg2.getX() + w, msg2.getY(), null);
				lastX = 18;
				w = w + i.getWidth(this);
				++x;
			}			
		}


		if(msg3 != null)
		{
			ArrayList<Image> images = msg3.getImages();
			int lastX = 0;
			int x = 0;
			int w = 0;
			for(Image i: images)
			{
				if(i != null)
				{
					g.drawImage(i, msg3.getX() + w, msg3.getY(), null);
					lastX = 18;
					w = w + i.getWidth(this);
					++x;
				}
				else
				{
					Trace.dprint("Null Image at " + x + " in " + msg3.getWord());
				}
			}			
		}

		for(text t: words)
		{
			ArrayList<Image> images = t.getImages();
			int lastX = 0;
			int x = 0;
			int w = 0;
			for(Image i: images)
			{
				g.drawImage(i, t.getX() + w, t.getY(), null);
				lastX = 18;
				w = w + i.getWidth(this);
				++x;
			}
		}

		try{

			for(text t: wordsVertical)
			{
				ArrayList<Image> images = t.getImages();
				int lastY = 0;
				int y = 0;
				for(Image i: images)
				{
					g.drawImage(i, t.getX() + ((18 - i.getWidth(this))/2), t.getY() + y * 18, null);
					lastY = 18;
					++y;
				}
			}
		}
		catch(Exception e)
		{
			//catch a concurrent modification exception that isn't a problem
		}

		if(!cardSelected)
		{	

			drawOtherPlayers(g);

			drawSuit(g);	

			try{
				for(card c: Cards)
					g.drawImage(c.getImage(), c.getX(), c.getY(), null);
				for(card c: CardsH)
				{
					Image rotatedImage = new BufferedImage(c.getImage().getHeight(null), c.getImage().getWidth(null), BufferedImage.TYPE_INT_ARGB);

					Graphics2D g2d = (Graphics2D) rotatedImage.getGraphics();
					g2d.rotate(Math.toRadians(90.0));
					g2d.drawImage(c.getImage(), 0, -rotatedImage.getWidth(null), null);
					g2d.dispose();

					g.drawImage(rotatedImage, c.getX(), c.getY(), null);
				}
			}
			catch(Exception e)
			{
				// catch a concurrent modification exception
			}
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
			for(card c: CardsH)
			{
				Image rotatedImage = new BufferedImage(c.getImage().getHeight(null), c.getImage().getWidth(null), BufferedImage.TYPE_INT_ARGB);

				Graphics2D g2d = (Graphics2D) rotatedImage.getGraphics();
				g2d.rotate(Math.toRadians(90.0));
				g2d.drawImage(c.getImage(), 0, -rotatedImage.getWidth(null), null);
				g2d.dispose();

				g.drawImage(rotatedImage, c.getX(), c.getY(), null);
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

	/**
	 * Draws the suit on the screen.
	 * 
	 * @param g
	 */
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
				e.printStackTrace();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}

			g.drawImage(suitImage, 255, 140, null);
		}
	}

	/**
	 * Draws the back of the other players cards.
	 * 
	 * @param g
	 */
	public void drawOtherPlayers(Graphics g)
	{
		if(gameStarted)
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
				e.printStackTrace();
			} catch (IOException e) 
			{

				e.printStackTrace();
			}
		}
	}

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
	public void mouseMoved(MouseEvent e) 
	{

		//		g.drawImage(button.get(1), 364, 295, null);
		//		g.drawImage(button.get(3), 320, 295, null);

		if(buttons == 1)
		{
			if((e.getX() > 320) && (e.getX() < 360) && ((e.getY() > 295) && (e.getY() < 313)))
			{
				onOver = true;
			}
			else
				onOver = false;		

			if((e.getX() > 364) && (e.getX() < 394) && ((e.getY() > 295) && (e.getY() < 313)))
			{
				offOver = true;
			}
			else
				offOver = false;
			repaint();
		}
		else if(buttons == 2)
		{
			if((e.getX() > 370) && (e.getX() < 390) && ((e.getY() > 295) && (e.getY() < 315)))
			{
				aOver = true;
			}
			else
				aOver = false;

			if((e.getX() > 395) && (e.getX() < 415) && ((e.getY() > 295) && (e.getY() < 315)))
			{
				bOver = true;
			}
			else
				bOver = false;

			if((e.getX() > 320) && (e.getX() < 340) && ((e.getY() > 295) && (e.getY() < 315)))
			{
				cOver = true;
			}
			else
				cOver = false;

			if((e.getX() > 345) && (e.getX() < 365) && ((e.getY() > 295) && (e.getY() < 315)))
			{
				dOver = true;
			}
			else
				dOver = false;
			repaint();
		}


	}

	/**
	 * determine if the Card c is valid to be played
	 * @return
	 */
	public boolean notARenege(Card c)
	{
		int value;
		char s =getRealSuit(c);

		if(s==leadSuit || leadSuit==' ')
			return true;
		//check if out of suit
		for(int i =0;i < player.getCardCount();i++)
		{
			if(getRealSuit(player.getCards()[i])==leadSuit)
				return false;

		}
		return true;
	}

	public char getRealSuit(Card c){


		//turmp was clubs
		if(suit == 'c' && c.getSuit()=='s' && c.getValue()==11)
			return 'c';
		//spades was trump
		if(suit == 's' && c.getSuit()=='c' && c.getValue()==11)
			return 's';
		//hearts was trump
		if(suit == 'h' && c.getSuit()=='d' && c.getValue()==11)
			return 'h';
		//diamonds was trump
		if(suit == 'd' && c.getSuit()=='h' && c.getValue()==11)
			return 'd';
		return c.getSuit();
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		boolean follow = true;

		if(buttons == 1)
		{
			if((e.getX() > 320) && (e.getX() < 360) && ((e.getY() > 295) && (e.getY() < 313)))
			{


				if(owner.getState()==NetClientThread.Dealer_Discard)
				{
					card temp =null;
					for(card c:Cards)
					{
						if((c.getX() == 245) && (c.getY() == 215))
						{
							temp = c;
							break;
						}
					}
					int t=-1;
					if(temp != null)
					{
						for(int x =0;x < player.getCards().length;x++)
							if(player.getCards()[x].getSuit()==temp.getSuit() && player.getCards()[x].getValue()==temp.getVal())
							{
								t=x;
								break;
							}
						Cards.remove(temp);
						player.playCard(t);
					}
					//TODO check if t is the card that was picked up
					owner.setResult(1);
				}
				else if(owner.getState()==NetClientThread.Throw_Card)
				{
					card temp =null;
					for(card c:Cards)
					{
						if((c.getX() == 245) && (c.getY() == 215))
						{
							temp = c;
							break;
						}
					}
					int t=-1;
					if(temp != null)
					{
						for(int x =0;x < player.getCards().length;x++)
							if(player.getCards()[x].getSuit()==temp.getSuit() && player.getCards()[x].getValue()==temp.getVal())
							{
								t=x;
								break;
							}
						if(notARenege(player.getCards()[t]))
						{
							Cards.remove(temp);
							player.sendData(Opcode.throwCard, player.playCard(t));
							owner.setResult(1);
						}
						else
						{
							follow = false;
							owner.displayMessage("You have to follow suit!");//TODO make this message display
						}
					}


				}
				else
					owner.setResult(1);
				Trace.dprint("YES!");
				try 
				{
					if(follow)
						displayMessage(this.owner, "","","",0,0);
				} 
				catch (IOException e1) 
				{

					e1.printStackTrace();
				}
			}	

			if((e.getX() > 364) && (e.getX() < 394) && ((e.getY() > 295) && (e.getY() < 313)))
			{
				owner.setResult(0);
				Trace.dprint("NO!");
				try 
				{
					displayMessage(this.owner, "","","",0,0);
					if(playPos != null)
					{
						playPos.setX(openX);
						playPos.setY(openY);
					}
				} 
				catch (IOException e1) 
				{

					e1.printStackTrace();
				}
			}
		}
		else if(buttons == 2)
		{
			if(((e.getX() > 370) && (e.getX() < 390) && ((e.getY() > 295) && (e.getY() < 315))))
			{
				if(h)
				{
					owner.setResult(1);
					Trace.dprint("Hearts");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
				else
				{
					owner.setResult(0);
					Trace.dprint("Pass");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
			}

			if(((e.getX() > 395) && (e.getX() < 415) && ((e.getY() > 295) && (e.getY() < 315))))
			{
				if(c)
				{
					owner.setResult(2);
					Trace.dprint("Clubs");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
				else
				{
					owner.setResult(0);
					Trace.dprint("Pass");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
			}

			if(((e.getX() > 320) && (e.getX() < 340) && ((e.getY() > 295) && (e.getY() < 315))))
			{
				if(d)
				{
					owner.setResult(3);
					Trace.dprint("Diamonds");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
				else
				{
					owner.setResult(0);
					Trace.dprint("Pass");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
			}

			if(((e.getX() > 345) && (e.getX() < 365) && ((e.getY() > 295) && (e.getY() < 315))))
			{
				if(s)
				{
					owner.setResult(4);
					Trace.dprint("Spades");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
				else
				{
					owner.setResult(0);
					Trace.dprint("Pass");
					try 
					{
						displayMessage(this.owner, "","","",0,0);
					} 
					catch (IOException e1) 
					{

						e1.printStackTrace();
					}
				}
			}
		}

		repaint();

	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{

	}

	@Override
	public void mouseExited(MouseEvent e) 
	{

	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		for(card c: Cards)
		{
			if(e.getY() >= 215)
			{
				if(e.getY() >= PLAYER_CARD_Y)
				{
					if((e.getX() > c.getX()) && (e.getX() < c.getX() + 71))
					{
						if((e.getY() > c.getY()) && (e.getY() < c.getY() + 96))
						{
							//					TODO: implement gameCanvas.clear()
							if(c.getSuit() != ' ')
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
						if((xLoc + x) == openX)
							openX = origX;
						y = 20;
						x = 20;
						broke = true;

						if(value == 215)
						{
							playPos = selectedCard;
							openX = origX;
							openY = origY;
							try 
							{
								if(owner.getState()==NetClientThread.Dealer_Discard)
									displayMessage(this.owner, "Discard this", "card?","",1,0);
								else if(owner.getState()==NetClientThread.Throw_Card)
									displayMessage(this.owner, "Play this", "card?","",1,0);
							} 
							catch (IOException e1) 
							{

								e1.printStackTrace();
							}
						} 
						else
						{
							if((playPos != null) && (selectedCard == playPos))
							{
								playPos = null;
								try 
								{
									displayMessage(this.owner, "","","",0,0);
								} catch (IOException e1) 
								{

									e1.printStackTrace();
								}
							}
						}
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

						int i1 = player.getIndex(new Card(c.getSuit(), c.getVal()));
						int i2 = player.getIndex(new Card(selectedCard.getSuit(), selectedCard.getVal()));
						Trace.dprint("Swapping " + i1 + " and " + i2);
						if(i1 != -1 && i2 != -1)
							player.swapCards(i1 , i2);
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

	/**
	 * Set the player of this screen.
	 * 
	 * @param p The player
	 */
	public void setPlayer(Player p) 
	{
		player = p;
	}

	/**
	 * Displays a message on canvas
	 * 
	 * @param euchreApplet The euchreApplet calling this.
	 * @param txt First line of text
	 * @param txt2 Second line of text
	 * @param txt3 Third line of text
	 * @param opt 1 = Yes/No, 2 = Choose a suit, 3 = No buttons
	 * @param opt2 If opt = 2, used to determine what suit cannot be chosen. 1 = h, 2 = c, 3 = d, 4 = s
	 */
	public int displayMessage(EuchreApplet euchreApplet, String txt, String txt2, String txt3, int opt, int opt2) throws IOException 
	{	
		if(opt == 0)
		{
			button = new ArrayList<Image>();

			msg = null;
			msg2 = null;
			msg3 = null;
		}
		else
		{
			msg = new text(txt, 320, 220, owner);
			msg2 = new text(txt2, 320, 240, owner);
			msg3 = new text(txt3, 320, 260, owner);
			buttons = opt;

			button = new ArrayList<Image>();

			if(opt == 1)
			{
				button = new ArrayList<Image>();

				URL tempURL = new URL(owner.getCodeBase(), "no.gif");
				URL tempURL2 = new URL(owner.getCodeBase(), "noo.gif");
				URL tempURL3 = new URL(owner.getCodeBase(), "yes.gif");
				URL tempURL4 = new URL(owner.getCodeBase(), "yeso.gif");
				button.add(ImageIO.read(tempURL));
				button.add(ImageIO.read(tempURL2));
				button.add(ImageIO.read(tempURL3));
				button.add(ImageIO.read(tempURL4));

			}
			else if(opt == 2)
			{
				//1 = h, 2 = c, 3 = d, 4 = s
				if(opt2 == 1)
				{
					h = false;
					c = true;
					d = true;
					s = true;
				}
				else if(opt2 == 2)
				{
					c = false;
					h = true;
					d = true;
					s = true;
				}
				else if(opt2 == 3)
				{
					d = false;
					c = true;
					h = true;
					s = true;
				}
				else
				{
					s = false;
					c = true;
					d = true;
					h = true;
				}
				button = new ArrayList<Image>();

				URL tempURL1 = new URL(owner.getCodeBase(), "hs.gif");
				URL tempURL2 = new URL(owner.getCodeBase(), "cs.gif");
				URL tempURL3 = new URL(owner.getCodeBase(), "ds.gif");
				URL tempURL4 = new URL(owner.getCodeBase(), "ss.gif");
				URL tempURL5 = new URL(owner.getCodeBase(), "hso.gif");
				URL tempURL6 = new URL(owner.getCodeBase(), "cso.gif");
				URL tempURL7 = new URL(owner.getCodeBase(), "dso.gif");
				URL tempURL8 = new URL(owner.getCodeBase(), "sso.gif");
				URL tempURLd = new URL(owner.getCodeBase(), "p.gif");
				URL tempURLe = new URL(owner.getCodeBase(), "po.gif");

				button.add(ImageIO.read(tempURL1));
				button.add(ImageIO.read(tempURL2));
				button.add(ImageIO.read(tempURL3));
				button.add(ImageIO.read(tempURL4));
				button.add(ImageIO.read(tempURL5));
				button.add(ImageIO.read(tempURL6));
				button.add(ImageIO.read(tempURL7));
				button.add(ImageIO.read(tempURL8));
				button.add(ImageIO.read(tempURLd));
				button.add(ImageIO.read(tempURLe));
			}
		}

		repaint();

		return opt;
	}

	/**
	 * Updates a player.
	 * 
	 * @param player2 The player
	 */
	public void updatePlayer(Player player2) 
	{
		if(!players.contains(player2))
		{
			players.add(player2);
		}

		if(player2.getGuid() == (owner.getPlayer()).getGuid())
		{
			int size = player2.getCardCount();
			Card[] cards = player2.getCards();
			//cards[0].getSuit(); i don't think this belongs here - bert

			if(size > 0)
			{
				addCard(cards[0].getSuit(), cards[0].getValue(), 85, PLAYER_CARD_Y);
				if(size > 1)
				{
					addCard(cards[1].getSuit(), cards[1].getValue(), 164, PLAYER_CARD_Y);

					if(size > 2)
					{
						addCard(cards[2].getSuit(), cards[2].getValue(), 244, PLAYER_CARD_Y);
						if(size > 3)
						{
							addCard(cards[3].getSuit(), cards[3].getValue(), 322, PLAYER_CARD_Y);
							if(size > 4)
								addCard(cards[4].getSuit(), cards[4].getValue(), 401, PLAYER_CARD_Y);
						}
					}
				}
			}
		}
	}

	/**
	 * Tells the screen if the game is started.
	 * 
	 * @param start True if the game is started.
	 */
	public void setGameStarted(boolean start)
	{
		gameStarted = start;
	}

	/**
	 * Clears all cards except for this players cards.
	 */
	public void clear() 
	{
		try 
		{
			displayMessage(null, null, null, null, 0, 0);
		} catch (IOException e) {

			e.printStackTrace();
		}
		Cards.clear();
		CardsH.clear();
		Card[] tempCards = player.getCards();
		int x2 = PLAYER_X1;
		for(int x = 0; x < tempCards.length; x++)
		{
			if(tempCards[x]!= null)
			{
				Cards.add(new card(tempCards[x].getSuit(), tempCards[x].getValue(), x2, PLAYER_CARD_Y, owner));
				if(x2 == PLAYER_X1)
					x2 = PLAYER_X2;
				else if(x2 == PLAYER_X2)
					x2 = PLAYER_X3;
				else if(x2 == PLAYER_X3)
					x2 = PLAYER_X4;
				else if(x2 == PLAYER_X4)
					x2 = PLAYER_X5;
			}
		}
		repaint();
	}

	/**
	 * Get the sixth card that has been added to this players hand.
	 * 
	 * @return
	 */
	public Card getNewCard() 
	{
		for(card c:Cards)
		{
			if(c.getX() == 245 && c.getY() == 215)
			{
				return new Card(c.getSuit(), c.getVal());
			}
		}
		return null;
	}

}

/**
 * A card class that holds suit, value, location, and the image.
 * 
 * @author jpking
 *
 */
class card
{
	char suit;
	int value, x, y;
	Image img;

	/**
	 * card constructor.
	 * 
	 * @param theSuit
	 * @param val
	 * @param owner
	 */
	public card(char theSuit, int val, EuchreApplet owner)
	{
		suit = theSuit;
		value = val;
		URL url2;
		img = owner.getCardImg(suit, value);
		if(img == null)
			Trace.dprint(suit + ", " + value);
	}

	/**
	 * Get the value of this card.
	 * 
	 * @return The value of this card.
	 */
	public int getVal() 
	{
		return value;
	}

	/**
	 * Get the suit of this card.
	 * 
	 * @return The suit of this card.
	 */
	public char getSuit() 
	{
		return suit;
	}

	/**
	 * Set the y location
	 * 
	 * @param y2
	 */
	public void setY(int y2) 
	{
		y = y2;

	}

	/**
	 * Set the x location
	 * 
	 * @param x2
	 */
	public void setX(int x2) 
	{
		x = x2;

	}

	/** 
	 * Get the image associated with this card.
	 * 
	 * @return
	 */
	public Image getImage() 
	{
		return img;
	}

	/**
	 * Card constructor
	 * 
	 * @param theSuit
	 * @param val
	 * @param xPos
	 * @param yPos
	 * @param owner
	 */
	public card(char theSuit, int val, int xPos, int yPos, EuchreApplet owner)
	{
		suit = theSuit;
		value = val;
		x = xPos;
		y = yPos;

		img = owner.getCardImg(suit, value);
		if(img == null)
			Trace.dprint(suit + ", " + value);
	}

	/**
	 * Get x location
	 * 
	 * @return X location
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Get y location
	 * 
	 * @return Y location
	 */
	public int getY()
	{
		return y;
	}
}

/**
 * Class to store text that is displayed on this screen.
 * 
 * @author jpking
 *
 */
class text
{
	String txt;
	int x, y;
	ArrayList<Image> letters = new ArrayList<Image>();

	/**
	 * Text constructor.
	 * 
	 * @param word The text do add.
	 * @param ex The x location.
	 * @param why The y location.
	 * @param owner The applet that called this.
	 */
	public text(String word, int ex, int why, EuchreApplet owner)
	{
		for(int x = 0; x < word.length(); x++)
		{
			//	if(x != ' ')
			{
				char temp = word.charAt(x);
				URL url2;
				Image img;
				try 
				{
					if(temp == '?')
					{
						url2 = new URL(owner.getCodeBase(), "letters/qm.gif");
					}
					else if(temp == ' ')
					{
						url2 = new URL(owner.getCodeBase(), "letters/_.gif");
					}
					else if(temp > 90)
						url2 = new URL(owner.getCodeBase(), "letters/" + temp + "_.gif");
					else
						url2 = new URL(owner.getCodeBase(), "letters/" + temp + ".gif");
					img = ImageIO.read(url2);    //TODO: this line gave a javax.imageio.IIOException on dealerDiscard
					letters.add(img);
				} catch (MalformedURLException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}

		txt = word;
		x = ex;
		y = why;
	}

	/**
	 * Get the array of images of the letters.
	 * 
	 * @return The letters that make up this text.
	 */
	public ArrayList<Image> getImages()
	{
		return letters;
	}

	/**
	 * Get the text as a string.
	 * 
	 * @return The text as a string.
	 */
	public String getWord()
	{
		return txt;
	}

	/**
	 * Get the x location.
	 * 
	 * @return the x location.
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Get the y location.
	 * 
	 * @return the y location.
	 */
	public int getY()
	{
		return y;
	}
}
