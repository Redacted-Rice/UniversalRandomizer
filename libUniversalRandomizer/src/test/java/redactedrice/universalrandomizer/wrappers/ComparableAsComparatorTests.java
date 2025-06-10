package redactedrice.universalrandomizer.wrappers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.wrappers.ComparableAsComparator;
import support.SumableComparableObject;


class ComparableAsComparatorTests 
{
	@Test
	void comparableAsComparator() 
	{
		ComparableAsComparator<SumableComparableObject> compare = new ComparableAsComparator<>();
		assertTrue(compare.isEitherNullThrow());
		assertFalse(compare.isBothNullThrow());

		SumableComparableObject so1 = new SumableComparableObject(3);
		SumableComparableObject so2 = new SumableComparableObject(7);
		
		assertEquals(0, compare.compare(so1, so1));
		assertTrue(compare.compare(so1, so2) < 0);
		assertTrue(compare.compare(so2, so1) > 0);
		
		assertThrows(NullPointerException.class, () ->
			compare.compare(so1, null));
		assertThrows(NullPointerException.class, () ->
			compare.compare(null, so1));
		assertThrows(NullPointerException.class, () ->
			compare.compare(null, null));
	}
	
	@Test
	void comparableAsComparator_noThrows() 
	{
		ComparableAsComparator<SumableComparableObject> compare = new ComparableAsComparator<>();
		SumableComparableObject so1 = new SumableComparableObject(3);

		compare.setEitherNullThrow(false);
		assertEquals(1, compare.compare(so1, null));
		assertEquals(-1, compare.compare(null, so1));
		assertEquals(0, compare.compare(null, null));

		compare.setBothNullThrow(true);
		assertEquals(1, compare.compare(so1, null));
		assertEquals(-1, compare.compare(null, so1));
		assertThrows(NullPointerException.class, () ->
			compare.compare(null, null));
		
		so1.compareSecNullReturn = -1;
		assertEquals(-1, compare.compare(so1, null));
		assertEquals(1, compare.compare(null, so1));
		
		so1.compareSecNullReturn = 0;
		assertEquals(0, compare.compare(so1, null));
		assertEquals(0, compare.compare(null, so1));
	}
}
