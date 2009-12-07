package game;

import java.io.Serializable;

public class CardWrapper implements Serializable 
{
	private Card card;
	private int player;
	private boolean leading;
	public CardWrapper(Card c,int p,boolean l)
	{
		card=c;
		player=p;
		leading=l;
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public int getPID()
	{
		return player;
	}
	
	public boolean isLeading()
	{
		return leading;
	}
}
