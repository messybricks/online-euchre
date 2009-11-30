package game;

import java.io.Serializable;

public class CardWrapper implements Serializable 
{
	public Card card;
	public int player;
	public CardWrapper(Card c,int p)
	{
		card=c;
		player=p;
	}
}
