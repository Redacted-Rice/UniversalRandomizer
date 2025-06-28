package redactedrice.universalrandomizer.wrappers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import support.SumableComparableObject;


class SumAsSumableTests 
{
	@Test
	void sumableAsSum() 
	{
		SumableAsSum<SumableComparableObject> sum = new SumableAsSum<>();
		assertTrue(sum.isEitherNullThrow());
		assertFalse(sum.isBothNullThrow());
		
		SumableComparableObject so1 = new SumableComparableObject(3);
		SumableComparableObject so2 = new SumableComparableObject(7);
		SumableComparableObject so3 = sum.sum(so1, so2);
		assertEquals(10, so3.intVal);
		assertEquals(3, so1.intVal);
		assertEquals(7, so2.intVal);
		
		assertThrows(NullPointerException.class, () ->
			sum.sum(so1, null));
		assertThrows(NullPointerException.class, () ->
			sum.sum(null, so1));
		assertThrows(NullPointerException.class, () ->
			sum.sum(null, null));
	}
	
	@Test
	void sumableAsSum_noThrows() 
	{
		SumableAsSum<SumableComparableObject> sum = new SumableAsSum<>();
		SumableComparableObject so1 = new SumableComparableObject(3);
		
		sum.setEitherNullThrow(false);
		assertEquals(so1.intVal, sum.sum(so1, null).intVal);
		assertEquals(so1.intVal, sum.sum(null, so1).intVal);
		assertNull(sum.sum(null, null));

		sum.setBothNullThrow(true);
		assertThrows(NullPointerException.class, () ->
			sum.sum(null, null));
		assertEquals(so1.intVal, sum.sum(so1, null).intVal);
		assertEquals(so1.intVal, sum.sum(null, so1).intVal);
	}
}
