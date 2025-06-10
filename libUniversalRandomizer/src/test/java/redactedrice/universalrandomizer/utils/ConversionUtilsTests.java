package redactedrice.universalrandomizer.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.utils.ConversionUtils;
import support.SumableComparableObject;


class ConversionUtilsTests {

	void assertScoCollectionEquals(Collection<Integer> expected, Collection<SumableComparableObject> found)
	{
		assertEquals(expected.size(), found.size());
		Iterator<Integer> expItr = expected.iterator();
		Iterator<SumableComparableObject> foundItr = found.iterator();
		while (expItr.hasNext())
		{
			assertEquals(expItr.next(), foundItr.next().intVal);
		}
	}
	
	@Test
	void convertToWrapperClass() 
	{
		assertEquals(Byte.class, ConversionUtils.convertToWrapperClass(byte.class));
		assertEquals(Short.class, ConversionUtils.convertToWrapperClass(short.class));
		assertEquals(Integer.class, ConversionUtils.convertToWrapperClass(int.class));
		assertEquals(Long.class, ConversionUtils.convertToWrapperClass(long.class));
		assertEquals(Float.class, ConversionUtils.convertToWrapperClass(float.class));
		assertEquals(Double.class, ConversionUtils.convertToWrapperClass(double.class));
		assertEquals(Boolean.class, ConversionUtils.convertToWrapperClass(boolean.class));
		assertEquals(Character.class, ConversionUtils.convertToWrapperClass(char.class));
		assertEquals(Void.class, ConversionUtils.convertToWrapperClass(void.class));
		
		assertNull(ConversionUtils.convertToWrapperClass(Object.class));
		assertNull(ConversionUtils.convertToWrapperClass(null));
	}
	
	@Test
	void convertToPrimitiveClass() 
	{
		assertEquals(byte.class, ConversionUtils.convertToPrimitiveClass(Byte.class));
		assertEquals(short.class, ConversionUtils.convertToPrimitiveClass(Short.class));
		assertEquals(int.class, ConversionUtils.convertToPrimitiveClass(Integer.class));
		assertEquals(long.class, ConversionUtils.convertToPrimitiveClass(Long.class));
		assertEquals(float.class, ConversionUtils.convertToPrimitiveClass(Float.class));
		assertEquals(double.class, ConversionUtils.convertToPrimitiveClass(Double.class));
		assertEquals(boolean.class, ConversionUtils.convertToPrimitiveClass(Boolean.class));
		assertEquals(char.class, ConversionUtils.convertToPrimitiveClass(Character.class));
		assertEquals(void.class, ConversionUtils.convertToPrimitiveClass(Void.class));
		
		assertNull(ConversionUtils.convertToPrimitiveClass(Object.class));
		assertNull(ConversionUtils.convertToPrimitiveClass(null));
	}

	@Test
	void convertPrimitiveArrayToStream() 
	{
		byte[] inputByte = {1,2,3,4,5};
		short[] inputShort = {1,2,3,4,5};
		int[] inputInt = {1,2,3,4,5};
		long[] inputLong = {1,2,3,4,5};
		float[] inputFloat = {1,2,3,4,5};
		double[] inputDouble = {1,2,3,4,5};
		boolean[] inputBool = {true, false, true, true, false};
		char[] inputChar = {1,2,3,4,5};
		
		Collection<Byte> expectedByte = new LinkedList<>();
		Collection<Short> expectedShort = new LinkedList<>();
		Collection<Integer> expectedInt = new LinkedList<>();
		Collection<Long> expectedLong = new LinkedList<>();
		Collection<Float> expectedFloat = new LinkedList<>();
		Collection<Double> expectedDouble = new LinkedList<>();
		Collection<Boolean> expectedBool = new LinkedList<>();
		Collection<Character> expectedChar = new LinkedList<>();
		
		for (int i = 0; i < inputByte.length; i++)
		{
			expectedByte.add(inputByte[i]);
			expectedShort.add(inputShort[i]);
			expectedInt.add(inputInt[i]);
			expectedLong.add(inputLong[i]);
			expectedFloat.add(inputFloat[i]);
			expectedDouble.add(inputDouble[i]);
			expectedBool.add(inputBool[i]);
			expectedChar.add(inputChar[i]);
		}
		
		assertIterableEquals(expectedByte, ConversionUtils.convertPrimitiveArrayToStream(inputByte).toList());
		assertIterableEquals(expectedByte, ConversionUtils.convertPrimitiveArrayToStream((Object)inputByte).toList());
		assertIterableEquals(expectedByte, ConversionUtils.convertArrayToStream(inputByte).toList());

		assertIterableEquals(expectedShort, ConversionUtils.convertPrimitiveArrayToStream(inputShort).toList());
		assertIterableEquals(expectedShort, ConversionUtils.convertPrimitiveArrayToStream((Object)inputShort).toList());
		assertIterableEquals(expectedShort, ConversionUtils.convertArrayToStream(inputShort).toList());
		
		assertIterableEquals(expectedInt, ConversionUtils.convertPrimitiveArrayToStream(inputInt).toList());
		assertIterableEquals(expectedInt, ConversionUtils.convertPrimitiveArrayToStream((Object)inputInt).toList());
		assertIterableEquals(expectedInt, ConversionUtils.convertArrayToStream(inputInt).toList());
		
		assertIterableEquals(expectedLong, ConversionUtils.convertPrimitiveArrayToStream(inputLong).toList());
		assertIterableEquals(expectedLong, ConversionUtils.convertPrimitiveArrayToStream((Object)inputLong).toList());
		assertIterableEquals(expectedLong, ConversionUtils.convertArrayToStream(inputLong).toList());
		
		assertIterableEquals(expectedFloat, ConversionUtils.convertPrimitiveArrayToStream(inputFloat).toList());
		assertIterableEquals(expectedFloat, ConversionUtils.convertPrimitiveArrayToStream((Object)inputFloat).toList());
		assertIterableEquals(expectedFloat, ConversionUtils.convertArrayToStream(inputFloat).toList());
		
		assertIterableEquals(expectedDouble, ConversionUtils.convertPrimitiveArrayToStream(inputDouble).toList());
		assertIterableEquals(expectedDouble, ConversionUtils.convertPrimitiveArrayToStream((Object)inputDouble).toList());
		assertIterableEquals(expectedDouble, ConversionUtils.convertArrayToStream(inputDouble).toList());
		
		assertIterableEquals(expectedBool, ConversionUtils.convertPrimitiveArrayToStream(inputBool).toList());
		assertIterableEquals(expectedBool, ConversionUtils.convertPrimitiveArrayToStream((Object)inputBool).toList());
		assertIterableEquals(expectedBool, ConversionUtils.convertArrayToStream(inputBool).toList());
		
		assertIterableEquals(expectedChar, ConversionUtils.convertPrimitiveArrayToStream(inputChar).toList());
		assertIterableEquals(expectedChar, ConversionUtils.convertPrimitiveArrayToStream((Object)inputChar).toList());
		assertIterableEquals(expectedChar, ConversionUtils.convertArrayToStream(inputChar).toList());
	}

	@Test
	void convertToStream() 
	{
		String[] inputStr = {"t", "h", "r", "o", "w"};
		
		Collection<String> expectedStr = new LinkedList<>();
		for (int i = 0; i < inputStr.length; i++)
		{			
			expectedStr.add(inputStr[i]);
		}
		
		assertThrows(IllegalArgumentException.class, () ->
			ConversionUtils.convertPrimitiveArrayToStream(inputStr));
		
		assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStream(inputStr).toList());
		assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStream((Object)inputStr).toList());
		
		assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputStr).toList());
		assertIterableEquals(expectedStr, ConversionUtils.convertToStream(Arrays.asList(inputStr)).toList());
		
		Map<Integer, String> inputMap = new HashMap<>();
		for (int i = 0; i < inputStr.length; i++)
		{
			inputMap.put(i, inputStr[i]);
		}
		assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputMap).toList());
		
		assertEquals("t", ConversionUtils.convertToStream("t").findFirst().get());
	}
}
