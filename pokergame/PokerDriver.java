package pokergame;
import cards.*;
import java.util.Scanner;

public class PokerDriver
{
	public static void main(String[] args)
	{
		char ans = 'Y';
		Scanner in = new Scanner(System.in);
		
		GameRun runner = new GameRun();
		runner.enterNames();
		
		do
		{
			runner.run();
			
			System.out.println("\n\n\n\n");
			System.out.println("Would you like to play another hand? (y/n)");
			String answer = in.nextLine();
			while (answer.isEmpty())
			{
				System.out.println("Try again...(y/n)");
				answer = in.nextLine();
			}
			ans = answer.charAt(0);
			
			System.out.println("\n\n\n\n\n\n");
			System.gc();
		}
		while (ans=='y' || ans=='Y');
	}
}