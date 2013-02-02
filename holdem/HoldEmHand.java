package holdem;
import cards.*;
import java.util.Arrays;
import sort.Sorting;

public class HoldEmHand
{
	private Card[] hand;
	private int handScore;
	private final int MAX_CARDS = 5;
	private int[] intRep;
	
	public HoldEmHand(Card[] unsortHand)
	{
		handScore = 0;
		Sorting.insertionSort(unsortHand);
		this.hand = unsortHand;
		intRep = new int[hand.length];
		
		for (int i=0; i<hand.length; i++)
			intRep[i] = hand[i].getValue();
	}
	
	public int handValue()
	{
		handScore = highCardVal();
		int hasTrips, hasStraight;
		boolean hasPair, hasFlush;
		
		if (pairTest()) hasPair = true;
		else hasPair = false;
		
		hasTrips = tripsTest();
		hasStraight = straightTest(hasPair);
		
		if (flushTest()) hasFlush = true;
		else hasFlush = false;
		
		if (hasTrips!=-1) fullHouseTest(hasTrips);
		
		quadTest();
		
		if (hasFlush && (hasStraight!=-1)) straightFlushTest(hasStraight);
		
		return handScore;
	}
	
	//-----------------------------------------------------------
	// Test to find a pair, adjust score accordingly with kickers
	//-----------------------------------------------------------
	public boolean pairTest()
	{
		boolean pair = false;
		int index = 0;
		
		while (index<(hand.length-1) && (!pair))
		{
			if (intRep[index] == intRep[index+1])
			{
				pair = true;
				handScore = 10000;
				handScore += 2 * (100 * intRep[index]);
			}
			else index++;
		}
		
		// If there's a pair, resolve kickers
		if (pair)
		{
			switch (index)
			{
				case 0:		// Pair includes first two cards
					if (!twoPairTest(index)) {
						for (int i=2; i<MAX_CARDS; i++)
							handScore += intRep[i]; }
					break;
					
				case 1:		// Pair excludes first card, includes 2nd-3rd
					if (!twoPairTest(index)) {
						handScore += intRep[0];
						handScore += (intRep[3] + intRep[4]); }
					break;
					
				case 2:		// Pair excludes first two, includes 3-4
					if (!twoPairTest(index)) {
						handScore += (intRep[0] + intRep[1]);
						handScore += intRep[4]; }
					break;
				
				default:	// Anything else where the kickers are higher than pair
					if (!twoPairTest(index))
						handScore += (intRep[0] + intRep[1] + intRep[2]);
					break;
			}
		}
		
		return pair;
	}
	
	//--------------------------------------------------------
	// Test for a second pair, adjusting the score accordingly
	//--------------------------------------------------------
	private boolean twoPairTest(int index)
	{
		boolean twopair = false;
		int last = index;
		index += 2;
		
		while (index<(hand.length-1) && (!twopair))
		{
			if (intRep[index] == intRep[index+1])
			{
				twopair = true;
				handScore += 10000;
				handScore += 2 * (100 * intRep[index]);
			}
			else index++;
		}
		
		// resolve kickers
		if (twopair)
		{
			if (index==2) handScore += intRep[4];
			else
			{
				if (last==0) handScore += intRep[2];
				else handScore += intRep[0];
			}
		}
		
		return twopair;
	}
	
	//------------------------------------------------------------
	// Test for a set of trips, adjust score should they be found
	//------------------------------------------------------------
	public int tripsTest()
	{
		boolean trips = false;
		int index = 0, confirm = -1;
		
		while (index<(hand.length-2) && (!trips))
		{
			if ((intRep[index] == intRep[index+1]) &&
				(intRep[index+1] == intRep[index+2]) &&
				(intRep[index] == intRep[index+2]) )
			{
				handScore = 30000;
				handScore += 3 * (100 * intRep[index]);
				trips = true;
				confirm = index;
			}
			else index++;
		}
		
		if (trips)
		{
			switch(index)
			{
				case 0:
					handScore += (intRep[3] + intRep[4]);
					break;
				case 1:
					handScore += (intRep[0] + intRep[4]);
					break;
				default:
					handScore += (intRep[0] + intRep[1]);
					break;
			}
		}
		
		return confirm;
	}
	
	//--------------------------------------------------
	// Test to find a straight, adjust score accordingly
	//--------------------------------------------------
	public int straightTest(boolean pair)
	{
		boolean straight = false, ace = false;
		int index = 0, lim = 2, confirm = -1;
		int[] test;
		
		// If there is an ace, test high and low end
		if (intRep[0] == 12)
		{
			test = new int[8];
			test[7] = -1;	// Two card is valued 0
			ace = true;
			lim = 3;
			
			for (int i=0; i<intRep.length; i++)
				test[i] = intRep[i];
		}
		else { test = Arrays.copyOf(intRep, intRep.length); }
		
		if (pair)
		{
			// Run twice to avoid trips/twopair
			test = removePair(test);
			test = removePair(test);
		}
		
		while (index<=lim && (!straight))
		{
			if ((test[index] - test[index+1] == 1) &&
				(test[index+1] - test[index+2] == 1) &&
				(test[index+2] - test[index+3] == 1) &&
				(test[index+3] - test[index+4] == 1) )
			{
				straight = true;
				confirm = index;
				handScore = 40000;
				handScore += 100 * test[index];
			}
			else index++;
		}
		
		return confirm;
	}
	
	//--------------------------------------------
	// Find a flush using Card suit ordinal value
	//--------------------------------------------
	public boolean flushTest()
	{
		boolean flush = false;
		final int SUITS = 4;
		int[] highs = new int[SUITS];
		int[] suitCount = new int[SUITS];
		
		for (int i=0; i<SUITS; i++)
		{
			highs[i] = -1;
			suitCount[i] = 0;
		}
		
		for (int j=0; j<hand.length; j++)
		{
			int suit = hand[j].getIntSuit();
			suitCount[suit]++;
			
			if ((highs[suit]==-1) && (j==5 || j==6))
				highs[suit] = hand[j].getValue();
		}
		
		for (int x=0; x<SUITS; x++)
		{
			if (suitCount[x] >= 5)
			{
				flush = true;
				handScore = 50000;
				handScore += 100 * highs[x];
			}
		}
		
		return flush;
	}
	
	//--------------------------------------------------------
	// Find a full house by accepting a starting index for
	// trips, and then testing for a pair in the other indexes
	//--------------------------------------------------------
	public void fullHouseTest(int tripsInd)
	{
		boolean boat = false;
		int index;
		
		switch (tripsInd)	// Handle various situations in which the trips could be placed
		{
			case 0:		// where the trips start at index 0 (0-1-2)
				index = 3;
				while (index<(hand.length-1) && (!boat))
				{
					if (intRep[index] == intRep[index+1])
					{
						boat = true;
						handScore = 60000;
						handScore += 3 * (100 * intRep[tripsInd]);
						handScore += 2 * (10 * intRep[index]);
					}
					else index++;
				}
				break;
			case 1:		// where the trips start at index 1 (1-2-3)
				index = 4;
				while (index<(hand.length-1) && (!boat))
				{
					if (intRep[index] == intRep[index+1])
					{
						boat = true;
						handScore = 60000;
						handScore += 3 * (100 * intRep[tripsInd]);
						handScore += 2 * (10 * intRep[index]);
					}
					else index++;
				}
				break;
			case 2:		// where the trips start at index 2 (2-3-4)
				index = 0;		// need to test (0-1) and (5-6)
				if (intRep[index] == intRep[index+1])	// test index 0 and 1
				{
					boat = true;
					handScore = 60000;
					handScore += 3 * (100 * intRep[tripsInd]);
					handScore += 2 * (10 * intRep[index]);
				}
				else
				{
					index = 5;		// test indices 5 and 6 (trips are 2-3-4)
					if (intRep[index] == intRep[index+1])
					{
						boat = true;
						handScore = 60000;
						handScore += 3 * (100 * intRep[tripsInd]);
						handScore += 2 * (10 * intRep[index]);
					}
				}
				break;
			default:	// trips could either be (3-4-5) or (4-5-6)
				index = 0;		// in either case, pair cant be index (5-6)
				while (index<tripsInd && (!boat))
				{
					if (intRep[index] == intRep[index+1])
					{
						boat = true;
						handScore = 60000;
						handScore += 3 * (100 * intRep[tripsInd]);
						handScore += 2 * (10 * intRep[index]);
					}
					else index++;
				}
				break;
		}
	}
	
	//-----------------------------------------------------
	// Test for a four of a kind, adjust score accordingly
	//-----------------------------------------------------
	public void quadTest()
	{
		int index = 0, lim = 3;
		boolean quads = false;
		
		while (index<=lim && (!quads))
		{
			if ((intRep[index] == intRep[index+1]) &&
				(intRep[index] == intRep[index+2]) &&
				(intRep[index] == intRep[index+3]) )
			{
				quads = true;
				handScore = 70000;
				handScore += 100 * intRep[index];
			}
			else index++;
		}
	}
	
	//-------------------------
	// Find the straight flush!
	//-------------------------
	public void straightFlushTest(int index)
	{
		boolean strFlush = false;
		int baseSuit = hand[index].getIntSuit();
		
		if ((baseSuit == hand[index+1].getIntSuit()) &&
			(baseSuit == hand[index+2].getIntSuit()) &&
			(baseSuit == hand[index+3].getIntSuit()) &&
			(baseSuit == hand[index+4].getIntSuit()) )
		{
			strFlush = true;
			handScore = 80000;
			handScore += 100 * intRep[index];
		}
	}
	
	//--------------------------------------------------------
	// Add up the values of the first five cards for high card
	//--------------------------------------------------------
	public int highCardVal()
	{
		int value = 0;
		
		for (int i=0; i<MAX_CARDS; i++)
			value += hand[i].getValue();
		
		return value;
	}
	
	public int getScore()
		{ return handScore; }
	
	private int[] removePair(int[] test)
	{
		boolean tpair = true;
		int index = 0;
		
		// Eliminate a pair if there is one.
		while (tpair && index<(test.length-1))
		{
			if (test[index] == test[index+1])
			{
				tpair = false;
				index++;
				
				while (index<(test.length-1))
				{
					test[index] = test[index+1];
					index++;
				}
				
				test[test.length-1] = -5;
			}
			
			index++;
		}
		
		return test;
	}
}