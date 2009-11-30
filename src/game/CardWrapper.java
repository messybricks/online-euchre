package game;

import java.io.Serializable;

public class CardWrapper implements Serializable 
{
	private Card card;
	private int player;
	public CardWrapper(Card c,int p)
	{
		card=c;
		player=p;
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public int getPID()
	{
		return player;
	}
}
