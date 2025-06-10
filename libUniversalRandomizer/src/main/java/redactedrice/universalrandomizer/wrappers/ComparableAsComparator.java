package redactedrice.universalrandomizer.wrappers;

import java.util.Comparator;

public class ComparableAsComparator<T extends Comparable<T>> implements Comparator<T>
{
	private boolean eitherNullThrow;
	private boolean bothNullThrow;

	public ComparableAsComparator()
	{
		eitherNullThrow = true;
		bothNullThrow = false;
	}
	
	public boolean isBothNullThrow() {
		return bothNullThrow;
	}

	public void setBothNullThrow(boolean bothNullThrow) {
		this.bothNullThrow = bothNullThrow;
	}

	public boolean isEitherNullThrow() {
		return eitherNullThrow;
	}

	public void setEitherNullThrow(boolean eitherNullThrow) {
		this.eitherNullThrow = eitherNullThrow;
	}

	@Override
	public int compare(T o1, T o2) 
	{
		if (o1 == null)
		{
			if (eitherNullThrow)
			{
				throw new NullPointerException();
			}
			
			if (o2 == null)
			{
				if (bothNullThrow)
				{
					throw new NullPointerException();
				}
				return 0;
			}
			
			int result = o2.compareTo(null);
			if (result > 0)
			{
				return -1;
			}
			else if (result < 0)
			{
				return 1;
			}
			return 0;
		}
		else if (o2 == null && eitherNullThrow)
		{
			throw new NullPointerException();
		}
		
		return o1.compareTo(o2);
	}
}
