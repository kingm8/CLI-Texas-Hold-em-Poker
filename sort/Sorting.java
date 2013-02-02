package sort;

public class Sorting
{
	//-----------------------------------------------------------------
	//  Sorts the specified array of objects using an insertion
	//  sort algorithm.
	//-----------------------------------------------------------------
	public static void insertionSort (Comparable[] data)
	{
		for (int index = 1; index < data.length; index++)
		{
			Comparable key = data[index];
			int position = index;

			// Shift larger values to the right
			while (position > 0 && data[position-1].compareTo(key) > 0)
			{
				data[position] = data[position-1];
				position--;
			}
			
			data[position] = key;
		}
	}
}