package redactedrice.universalrandomizer.wrappers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Stream;

import redactedrice.universalrandomizer.utils.ConversionUtils;


public class ReflectionObject <T> 
{
	private T obj;

	protected ReflectionObject(T obj)
	{
		this.obj = obj;
	}
	
	public static <T2> ReflectionObject<T2> create(T2 obj)
	{
		if (obj == null)
		{
			return null;
		}
		return new ReflectionObject<>(obj);
	}
	
	public Object getField(String pathToGetterOrField) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return getObjectFromPath(obj, pathToGetterOrField, false);
	}

	public Stream<Object> getFieldStream(String pathToGetterOrField)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return ConversionUtils.convertToStream(getField(pathToGetterOrField));
	}

	public Stream<Object> getMapFieldValuesStream(String pathToGetterOrField)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return getMapFieldStream(pathToGetterOrField, true);
	}

	public Stream<Object> getMapFieldKeysStream(String pathToGetterOrField)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return getMapFieldStream(pathToGetterOrField, false);
	}

	@SuppressWarnings("unchecked")
	public Stream<Object> getMapFieldStream(String pathToGetterOrField, boolean valuesNotKeys)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Object ret = getField(pathToGetterOrField);
		if (valuesNotKeys)
		{
			return ConversionUtils.convertToStream(((Map<?, Object>) ret).values());
		}
		else
		{
			return ConversionUtils.convertToStream(((Map<Object, ?>) ret).keySet());
		}
	}
	
	public Object invoke(String pathToSetterOrField, Object... values) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Object owningObj = getObjectFromPath(obj, pathToSetterOrField, true);
		int lastSeparator = pathToSetterOrField.lastIndexOf('.');
		String lastPath = pathToSetterOrField;
		if (lastSeparator >= 0)
		{
			lastPath = lastPath.substring(lastSeparator + 1);
		}

		if (lastPath.endsWith("()"))
		{
			Method method = getMethodByName(owningObj, lastPath.substring(0, lastPath.length() - 2), values);
			return method.invoke(owningObj, values);
		}
		else
		{
			throw new NoSuchMethodException();
		}
	}

	public Object setField(String pathToSetterOrField, Object value) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Object owningObj = getObjectFromPath(obj, pathToSetterOrField, true);
		int lastSeparator = pathToSetterOrField.lastIndexOf('.');
		String lastPath = pathToSetterOrField;
		if (lastSeparator >= 0)
		{
			lastPath = lastPath.substring(lastSeparator + 1);
		}

		if (lastPath.endsWith("()"))
		{
			Method method = getMethodByName(owningObj, lastPath.substring(0, lastPath.length() - 2), value);
			return method.invoke(owningObj, value);
		}
		else
		{
			// This is falsely flagged by sonarlint as an accessibility bypass - getField only returns
			// public fields which would already be accessible via the object
			owningObj.getClass().getField(lastPath).set(owningObj, value); // NOSONAR
			return null;
		}
	}
	
	private boolean doParamsMatch(Parameter[] methodParams, Object... params)
	{
		boolean match = true;
		if (methodParams.length != params.length)
		{
			match = false;
		}
		else // same length - check their types
		{
			match = true;
			for (int i = 0; i < methodParams.length; i++)
			{
				Class<?> mParamWrapped = ConversionUtils.convertToWrapperClass(methodParams[i].getType());
				if (params[i] != null && // if params are not null, we check they are the right type
						(!methodParams[i].getType().isInstance(params[i]) &&  // if they are not the right type
								(mParamWrapped == null || !mParamWrapped.isInstance(params[i])))) // and they are not primitive types or the wrapped type does not match
				{
					match = false;
					break;
				}
			}
		}
		return match;
	}
	
	private Method getMethodByName(Object obj, String methodName, Object... params) throws NoSuchMethodException
	{
		Method[] methods = obj.getClass().getMethods();
		for (Method m : methods)
		{
			if (m.getName().equals(methodName) && doParamsMatch(m.getParameters(), params))
			{
				return m;
			}
		}
		throw new NoSuchMethodException();
	}
	
	private Object getObjectFromPath(Object baseObj, String path, boolean penultimate) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
				NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		String[] paths = path.split("\\.");
		if (paths.length <= 1 && penultimate)
		{
			return baseObj;
		}
		
		Object nextObj = getFromMethodOrField(baseObj, paths[0]);
		int lengths = !penultimate ? paths.length : paths.length - 1;
		for (int pathIndex = 1; pathIndex < lengths; pathIndex++)
		{
			nextObj = getFromMethodOrField(nextObj, paths[pathIndex]);
		}
		return nextObj;
	}
	
	private Object getFromMethodOrField(Object owningObj, String methodOrField) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
				NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		if (methodOrField.endsWith("()"))
		{
			return getMethodByName(owningObj, methodOrField.substring(0, methodOrField.length() - 2)).invoke(owningObj);
		}
		else
		{
			return owningObj.getClass().getField(methodOrField).get(owningObj);
		}
	}
	
	public T getObject()
	{
		return obj;
	}
	
	protected boolean setObject(T obj)
	{
		if (obj != null)
		{
			this.obj = obj;
			return true;
		}
		return false;
	}
}