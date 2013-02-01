package cards;
import java.util.Random;

public class Card implements Comparable<Card>
{
	private enum Values {Two, Three, Four, Five, Six, Seven,
				Eight, Nine, Ten, Jack, Queen, King, Ace};
	private enum Suits {Clubs, Diamonds, Hearts, Spades};
	Values value;
	Suits suit;
	
	public Card()
	{
		Random generator = new Random();
		setInstance(generator.nextInt(13), generator.nextInt(4));
	}
	
	public Card(int val, int tsuit)
	{
		setInstance(val, tsuit);
	}
	
	// Various getters and setters
	public int getValue()
		{ return value.ordinal(); }
		
	public String getStringValue()
		{ return value.name(); }
		
	public String getSuit()
		{ return suit.name(); }
	
	public int getIntSuit()
		{ return suit.ordinal(); }
	
	//------------------------------------------------------
	// Fulfills comparable interface, sorts with face value
	//------------------------------------------------------
	public int compareTo(Card cd)
	{
		int result = 0;
		
		if (this.getValue() > cd.getValue())
			result = -1;
		if (this.getValue() < cd.getValue())
			result = 1;
		
		return result;
	}
	
	public void setInstance(int val, int tsuit)
	{
		switch(val)
		{
			case 0:
				value = Values.Two;
				break;
			case 1:
				value = Values.Three;
				break;
			case 2:
				value = Values.Four;
				break;
			case 3:
				value = Values.Five;
				break;
			case 4:
				value = Values.Six;
				break;
			case 5:
				value = Values.Seven;
				break;
			case 6:
				value = Values.Eight;
				break;
			case 7:
				value = Values.Nine;
				break;
			case 8:
				value = Values.Ten;
				break;
			case 9:
				value = Values.Jack;
				break;
			case 10:
				value = Values.Queen;
				break;
			case 11:
				value = Values.King;
				break;
			case 12:
				value = Values.Ace;
				break;
		}
		
		switch(tsuit)
		{
			case 0:
				suit = Suits.Clubs;
				break;
			case 1:
				suit = Suits.Diamonds;
				break;
			case 2:
				suit = Suits.Hearts;
				break;
			case 3:
				suit = Suits.Spades;
				break;
		}
	}
	
	public String toString()
	{
		String descrip = value.name();
		descrip += " of " + suit.name();
		return descrip;
	}
}