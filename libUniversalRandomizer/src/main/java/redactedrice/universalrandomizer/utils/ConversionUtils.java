package redactedrice.universalrandomizer.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ConversionUtils 
{	
	private ConversionUtils() 
	{
	    throw new IllegalStateException("Utility class");
	}
	
	public static Class<?> convertToWrapperClass(Class<?> toCheck)
	{
		if (byte.class.equals(toCheck))
		{
			return Byte.class;
		}
		else if (short.class.equals(toCheck))
		{
			return Short.class;
		}
		else if (int.class.equals(toCheck))
		{
			return Integer.class;
		}
		else if (long.class.equals(toCheck))
		{
			return Long.class;
		}
		else if (float.class.equals(toCheck))
		{
			return Float.class;
		}
		else if (double.class.equals(toCheck))
		{
			return Double.class;
		}
		else if (boolean.class.equals(toCheck))
		{
			return Boolean.class;
		}
		else if (char.class.equals(toCheck))
		{
			return Character.class;
		}
		else if (void.class.equals(toCheck))
		{
			return Void.class;
		}
		
		return null;
	}
	
	public static Class<?> convertToPrimitiveClass(Class<?> toCheck)
	{
		if (Byte.class.equals(toCheck))
		{
			return byte.class;
		}
		else if (Short.class.equals(toCheck))
		{
			return short.class;
		}
		else if (Integer.class.equals(toCheck))
		{
			return int.class;
		}
		else if (Long.class.equals(toCheck))
		{
			return long.class;
		}
		else if (Float.class.equals(toCheck))
		{
			return float.class;
		}
		else if (Double.class.equals(toCheck))
		{
			return double.class;
		}
		else if (Boolean.class.equals(toCheck))
		{
			return boolean.class;
		}
		else if (Character.class.equals(toCheck))
		{
			return char.class;
		}
		else if (Void.class.equals(toCheck))
		{
			return void.class;
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertToStream(Object obj)
	{
		if (obj instanceof Collection)
		{
			return ((Collection<T>) obj).stream();
		}
		else if (obj.getClass().isArray())
		{
			return convertArrayToStream(obj);
		}
		else if (obj instanceof Map)
		{
			return ((Map<?,T>) obj).values().stream();
		}
		return (Stream<T>) Stream.of(obj);
	}
	
	public static <T> Stream<T> convertArrayToStream(T[] array)
	{
		return Arrays.stream(array);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertArrayToStream(Object array)
	{
		if (array.getClass().getComponentType().isPrimitive())
		{
			return (Stream<T>) convertPrimitiveArrayToStream(array);
		}
		else
		{
			return Arrays.stream((T[]) array);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertPrimitiveArrayToStream(Object primativeArray)
	{
		Class<?> primType = primativeArray.getClass().getComponentType();
		if (primType == byte.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((byte[])primativeArray);
		}
		else if (primType == short.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((short[])primativeArray);
		}
		else if (primType == int.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((int[])primativeArray);
		}
		else if (primType == long.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((long[])primativeArray);
		}
		else if (primType == float.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((float[])primativeArray);
		}
		else if (primType == double.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((double[])primativeArray);
		}
		else if (primType == boolean.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((boolean[])primativeArray);
		}
		else if (primType == char.class)
		{
			return (Stream<T>) convertPrimitiveArrayToStream((char[])primativeArray);
		}
		
		throw new IllegalArgumentException();
	}
	
	public static Stream<Byte> convertPrimitiveArrayToStream(byte... primitiveArray)
	{
		return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
	}
	
	public static Stream<Short> convertPrimitiveArrayToStream(short... primitiveArray)
	{
		return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
	}
	
	public static Stream<Integer> convertPrimitiveArrayToStream(int... primitiveArray)
	{
		return Arrays.stream(primitiveArray).boxed();
	}
	
	public static Stream<Long> convertPrimitiveArrayToStream(long... primitiveArray)
	{
		return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
	}
	
	public static Stream<Float> convertPrimitiveArrayToStream(float... primitiveArray)
	{
		return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
	}
	
	public static Stream<Double> convertPrimitiveArrayToStream(double... primitiveArray)
	{
		return Arrays.stream(primitiveArray).boxed();
	}
	
	public static Stream<Boolean> convertPrimitiveArrayToStream(boolean... primitiveArray)
	{
		return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
	}
	
	public static Stream<Character> convertPrimitiveArrayToStream(char... primitiveArray)
	{
		return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
	}
}
