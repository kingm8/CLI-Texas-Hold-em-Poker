package pokergame;
import cards.*;

public class Player
{
	private boolean inHand;
	private int chips, lastBet;
	private String name;
	private Card card1, card2;
	
	public Player()
	{
		chips = 2000;
		lastBet = 0;
		inHand = true;
		name = null;
		card1 = card2 = null;
	}
	
	public Player(Card card1, Card card2)
	{
		chips = 2000;
		lastBet = 0;
		inHand = true;
		name = null;
		this.card1 = card1;
		this.card2 = card2;
	}
	
	// Various getters and setters
	public boolean isInHand()
		{ return inHand; }
	
	public void setInHand()
	{
		if (card1!=null && card2!=null)
			inHand = true;
		else
			inHand = false;
	}
	
	public void setInHand(boolean hand)
		{ inHand = hand; }
	
	public void setName(String Name)
		{ this.name = Name; }
	
	public String getName()
		{ return name; }
	
	public String getHand()
		{ return (card1.toString() + "\n" + card2.toString()); }
	
	public int getChips()
		{ return chips; }
	
	public void setLastBet(int bet)
		{ lastBet = bet; }
	
	public int getLastBet()
		{ return lastBet; }
	
	public Card getCard1()
		{ return card1; }
	
	public Card getCard2()
		{ return card2; }
	
	public void resetHand()
		{ card1 = card2 = null; }
	
	public void dealHand(Card cd)
	{
		if (this.card1 == null)
			this.card1 = cd;
		else
			if (this.card2 == null)
				this.card2 = cd;
			else
				System.out.println("Player already has a two card hand.");
	}
	
	public void dealHand(Card cd1, Card cd2)
	{
		this.card1 = cd1;
		this.card2 = cd2;
	}
	
	// Now into game utilities
	public void bet(int wager)
	{
		chips -= (wager-lastBet);
	}
	
	public void winPot(int pot)
	{
		chips += pot;
	}
	
	public void fold()
	{
		card1 = card2 = null;
		lastBet = 0;
		inHand = false;
	}
	
	public String toString()
	{
		String descrip = "";
		descrip += name + "\n";
		descrip += "Current chip total: " + chips + "\n";
		descrip += card1.toString() + "\n";
		descrip += card2.toString() + "\n";
		return descrip;
	}
}