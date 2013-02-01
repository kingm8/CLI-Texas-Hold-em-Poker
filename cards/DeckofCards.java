package cards;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeckofCards
{
	private int index = 0;
	private final int VALUE_LIM = 13, SUIT_LIM = 4;
	private Card[] deck = new Card[VALUE_LIM * SUIT_LIM];
	
	public DeckofCards()
	{
		for (int i=0; i<SUIT_LIM; i++)
		{
			for(int j=0; j<VALUE_LIM; j++)
			{
				deck[index] = new Card(j,i);
				index++;
			}
		}
		
		index = 0;
	}
	
	public void resetDeck()
	{
		index = 0;
		
		for (int i=0; i<SUIT_LIM; i++)
		{
			for(int j=0; j<VALUE_LIM; j++)
			{
				deck[index] = new Card(j,i);
				index++;
			}
		}
		
		index = 0;
	}
	
	public void shuffle()
	{
		index = 0;
		
		List<Card> cardList = Arrays.asList(deck);
		Collections.shuffle(cardList);
		deck = cardList.toArray(new Card[52]);
	}
	
	public void burnCard()
	{
		index++;
	}
	
	public Card dealNext()
	{
		index++;
		return deck[index-1];
	}
	
	public Card[] dealNextSet(int lim)
	{
		Card[] result = new Card[lim];
		
		for (int in=0; in<result.length; in++)
		{
			result[in] = dealNext();
		}
		
		return result;
	}
	
	public Card[] getCommunity()
	{
		final int commLim = 5;
		Card[] community = new Card[commLim];
		
		for (int i=0; i<community.length; i++)
		{
			if (i==0 || i>=3) burnCard();
			
			community[i] = dealNext();
		}
		
		return community;
	}
	
	public String toString()
	{
		String descrip = "";
		
		for(int ij=0; ij<deck.length; ij++)
		{
			descrip += deck[ij].toString() + "\n";
		}
		
		return descrip;
	}
}