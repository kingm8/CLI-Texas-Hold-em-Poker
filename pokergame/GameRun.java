package pokergame;
import cards.*;
import holdem.*;
import java.io.*;

public class GameRun
{
	private Player[] players;
	private Card[] board;
	private DeckofCards deck;
	private int pot, bet;
	private final int BIGBLIND = 100;
	
	public GameRun()
	{
		pot = 0;
		bet = 0;
		players = new Player[1];
		deck = new DeckofCards();
	}
	
	//------------------------------------------------
	// Find out how many players, then prompt
	// each for their names
	//------------------------------------------------
	public void enterNames()
	{
		boolean valid = false;
		int seats = -1;
		String response;
		BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		
		do {
			System.out.println("How many players are here? (max 9)");
			try {
				response = brIn.readLine();
				seats = Integer.parseInt(response);
				valid = true;
			} catch (IOException ioe) {
				System.err.println(ioe);
				valid = false;
			} catch (NumberFormatException nfe) {
				valid = false;
			}
			
			if (valid) valid = (seats<9 && seats>1);
			if (!valid) System.out.println("***Invalid Input***");
		} while(!valid);
		
		players = new Player[seats];
		for (int i=0; i<players.length; i++)
		{
			players[i] = new Player();
			System.out.println("Player #" + (i+1) + ", enter your name:");
			try {
				response = brIn.readLine();
			} catch (IOException ioe) {
				response = (i+1) + ": Who?"; }
			players[i].setName(response);
		}
	}
	
	//------------------------------------------------------
	// RUN THE GAME, broken up into various functions
	//------------------------------------------------------
	public void run()
	{
		zeroBets();
		setBet(BIGBLIND);
		
		deck.resetDeck();
		deck.shuffle();
		dealHands();
		
		// Make all pre-flop bets/calls/folds/etc
		blindBet();
		zeroBets();
		
		// Establish board/community/whatever ya wana call it
		board = deck.getCommunity();
		
		// Show each player their hands with the flop
		if (continueH())
		{
			betRound(3, false);
			zeroBets();
		}
		
		// Show each player their hands with the turn
		if (continueH())
		{
			betRound(4, false);
			zeroBets();
		}
		
		// Show each player their hands with the river
		if (continueH())
		{
			betRound(5, false);
			zeroBets();
		}
		
		endHand();
	}
	
	//--------------------------------------------------------
	// Provide each player a two card hand with which to play
	//--------------------------------------------------------
	private void dealHands()
	{
		Card[] firstCard = new Card[players.length];
		Card[] secondCard = new Card[players.length];
		
		for (int fc=0; fc<firstCard.length; fc++)
			firstCard[fc] = deck.dealNext();
		for (int sc=0; sc<secondCard.length; sc++)
			secondCard[sc] = deck.dealNext();
		
		for (int c=0; c<players.length; c++)
			players[c].dealHand(firstCard[c], secondCard[c]);
	}
	
	//--------------------------------------------------
	// Runs a full round of betting amongst the players
	//--------------------------------------------------
	private void betRound(int commCards, boolean blinded)
	{
		if (blinded)
		{
			for (int i=2; i<players.length; i++)
			{
				if (players[i].isInHand())
					showHands(i, commCards);
			}
			
			showHands(0,0);
			showHands(1,0);
		}
		else
		{
			for (int i=0; i<players.length; i++)
			{
				if (players[i].isInHand())
					showHands(i, commCards);
			}
		}
		
		// Ensures that all players have called or folded to
		// the current bet, whatever that may be
		for (int ij=0; ij<10; ij++)
		{
			int index=0;
			
			while (index<players.length)
			{
				if (players[index].getLastBet() < bet &&
					players[index].isInHand())
				{
					showHands(index, commCards);
				}
				index++;
			}
		}
	}
	
	//-------------------------------------------
	// Operate pre flop betting, with the blinds
	//-------------------------------------------
	private void blindBet()
	{
		int index = 0;
		players[index].bet(getBet()/2);
		pot += getBet()/2;
		players[index].setLastBet(getBet()/2);
		index++;
		players[index].bet(getBet());
		pot += getBet();
		players[index].setLastBet(getBet());
		
		betRound(0,true);
	}
	
	//------------------------------------------------------
	// Show each player their a hand and current game stats
	// Then prompts the player for its decision
	//------------------------------------------------------
	private void showHands(int ind, int commCards)
	{
		System.out.println("Player #" + (ind+1) + "\n");
		System.out.println(players[ind]);
		
		if (commCards > 0)
		{
			System.out.println("The board is now showing these cards:\n");
			for (int j=0; j<commCards; j++)
				System.out.println(board[j] + "\n");
			
			System.out.print("\n");
		}
		
		singleBet(ind);
		System.out.println("Your turn has ended.");
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
	}
	
	//--------------------------------------------------------
	// Decide whether or not to continue dealing based on how
	// many players are still in the hand
	//--------------------------------------------------------
	private boolean continueH()
	{
		int plays = 0;
		
		for (int i=0; i<players.length; i++)
			if (players[i].isInHand()) plays++;
		
		if (plays>1) return true;
		else return false;
	}
	
	//------------------------------------------------
	// Scan for bets/calls/folds from a single player
	//------------------------------------------------
	private void singleBet(int index)
	{
		boolean valid = false;
		int prompt = -1;
		String betPrompt, response;
		BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		
		if ((getBet() == 0) || (getBet() == players[index].getLastBet()))
			betPrompt = "Enter 1 to check, or 2 to raise, or 3 to fold.";
		else
			betPrompt = "Enter 1 to call, or 2 to raise, or 3 to fold.";
		
		do {
			System.out.println("The current bet is: " + bet);
			System.out.println("The pot currently holds: " + pot);
			System.out.println("Your last bet this round was: " +
							players[index].getLastBet());
			System.out.println(betPrompt);
			
			// Obtain and check the input
			try {
				response = brIn.readLine();
				prompt = Integer.parseInt(response);
				valid = true;
			} catch (IOException ioe) {
				System.err.println(ioe);
				valid = false;
			} catch (NumberFormatException nfe) {
				valid = false;
			}
			
			if (valid) valid = (prompt==1 || prompt==2 || prompt==3);
			if (!valid) System.out.println(players[index].getName() + 
						", just enter a 1, 2, or 3.\n");
		} while (!valid);
		
		// Run user operation
		switch (prompt)
		{
			case 1:		// A call or check
				players[index].bet(getBet());
				pot += (getBet() - players[index].getLastBet());
				players[index].setLastBet(getBet());
				break;
			case 2: 	// A raise
				valid = false;
				int nbet = 0;
				do {
					System.out.println("To what total would you like to raise the bet?");
					try {
						response = brIn.readLine();
						nbet = Integer.parseInt(response);
						valid = true;
					} catch (IOException ioe) {
						System.err.println(ioe);
						valid = false;
					} catch (NumberFormatException nfe) {
						System.out.println("***Invalid Input***");
						valid = false;
					}
					if (valid && (nbet < bet+BIGBLIND || nbet > players[index].getChips()))
					{
						valid = false;
						System.out.println("Your new bet must be greater than the " +
								"current bet plus the blind and less than your chip total.");
					}
				} while (!valid);
				
				setBet(nbet);
				players[index].bet(getBet());
				pot += (getBet() - players[index].getLastBet());
				players[index].setLastBet(getBet());
				break;
			case 3:		// A fold
				players[index].fold();
				break;
		}
	}
	
	//--------------------------------------------------
	// Zero out all the players' most recent bets...
	// Only to be used at the end of each betting round
	//--------------------------------------------------
	private void zeroBets()
	{
		bet = 0;
		for (int i=0; i<players.length; i++)
			players[i].setLastBet(0);
	}
	
	//----------------------------------------------------
	// After the completion of the hand, reset everything
	//----------------------------------------------------
	private void endHand()
	{
		int winner = -3;
		
		if (continueH())
			winner = findWinner();
		else
		{
			int i=0;
			while (winner!=-3)
			{
				if (players[i].isInHand())
					winner = i;
				
				i++;
			}
		}
		
		if (winner != -3)
		{
			System.out.println("Player #" + (winner+1) + ", " +
					players[winner].getName() + ", has won the pot!");
			System.out.println("That pot held: " + pot);
			
			players[winner].winPot(pot);
			System.out.println(players[winner].getName() + " now has " +
							players[winner].getChips() + " chips.");
			
			players[winner].resetHand();
		}
		else
			System.out.println("That pot was discarded because all you " +
						"dumbasses folded; I can't give it to any of you.");
		
		board = null;
		players = shiftPlayers();
		pot = 0;
		setBet(0);
		
		for (int j=0; j<players.length; j++)
		{
			players[j].fold();
			players[j].setInHand(true);
			players[j].setLastBet(0);
		}
	}
	
	//----------------------------------------------------
	// Shift the players to simulate dealer chip movement
	//----------------------------------------------------
	private Player[] shiftPlayers()
	{
		Player[] shifted = new Player[players.length];
		shifted[shifted.length-1] = players[0];
		
		for (int i=0; i<players.length-1; i++)
			shifted[i] = players[i+1];
		
		return shifted;
	}
	
	// Bet handling methods added only during debug period
	// Did fix bugs, but still might work without them
	// Further testing required
	private void setBet(int sbet)
		{ bet = sbet;}
	private int getBet()
		{ return bet; }
	
	//--------------------------------------------------------
	// Determine the winner of the hand....fixed and optimized
	//--------------------------------------------------------
	private int findWinner()
	{
		int winner = -3, winVal = -2;
		final int handLim = 7;
		HoldEmHand[] hands = new HoldEmHand[players.length];
		
		for (int i=0; i<players.length; i++)
		{
			if (players[i].isInHand())
			{
				Card[] cards = new Card[handLim];
				
				for (int x=0; x<board.length; x++)
					cards[x] = board[x];
				
				cards[5] = players[i].getCard1();
				cards[6] = players[i].getCard2();
				
				hands[i] = new HoldEmHand(cards);
			}
			else hands[i] = null;
		}
		
		for (int ij=0; ij<hands.length; ij++)
		{
			int temp = 0;
			
			if (hands[ij] != null)
			{
				temp = hands[ij].handValue();
				
				if (temp > winVal)
				{
					winner = ij;
					winVal = temp;
				}
			}
		}
		
		return winner;
	}
}