package redactedrice.universalrandomizer.wrappers;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import redactedrice.universalrandomizer.utils.ConversionUtils;

public class ExtendableObject {
    Object obj;
    HashMap<String, Object> attrMap;

    public ExtendableObject(Object obj) {
        this.obj = obj;
        attrMap = new HashMap<>();
    }

    protected ExtendableObject() {
        this.obj = this;
        attrMap = new HashMap<>();
    }

    public Object get(String fieldName) {
        Object foundObj = getWithReflection(fieldName);
        if (foundObj == null) {
            foundObj = attrMap.get(fieldName);
        }
        return foundObj;
    }

    public <T> T get(String fieldName, T defaultVal) {
        Object foundObj = getWithReflection(fieldName);
        if (foundObj == null) {
            foundObj = attrMap.get(fieldName);
        }
        return foundObj != null ? (T) foundObj : defaultVal;
    }

    public Object getWithReflection(String field) {
        Object foundObj = tryGetFromGetter(field);
        if (foundObj == null) {
            foundObj = tryGetField(field);
        }
        return foundObj;
    }

    public void set(String fieldName, Object val) {
        boolean set = setWithReflection(fieldName, val);
        if (!set) {
            attrMap.put(fieldName, val);
        }
    }

    public boolean setIfExists(String fieldName, Object val) {
        boolean set = setWithReflection(fieldName, val);
        if (!set) {
            set = attrMap.computeIfPresent(fieldName, (key, prevVal) -> val) != null;
        }
        return set;
    }

    public boolean setWithReflection(String fieldName, Object val) {
        boolean set = trySetWithSetter(fieldName, val);
        if (!set) {
            set = trySetField(fieldName, val);
        }
        return set;
    }

    private Object tryGetFromGetter(String fieldName) {
        Object foundObj = null;
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        // Check for a public getter
        Method getter = tryGetMethodByName("get" + capitalized);
        if (getter == null) {
            getter = tryGetMethodByName("is" + capitalized);
        }

        if (getter != null) {
            try {
                foundObj = getter.invoke(obj);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                // Nothing to do - foundObj is already null
            }
        }
        return foundObj;
    }

    private Object tryGetField(String fieldName) {
        Object foundObj = null;
        try {
            // Throws if not found so no need to check the return of getField
            foundObj = obj.getClass().getField(fieldName).get(obj);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e) {
            // Nothing to do - foundObj is already null
        }
        return foundObj;
    }

    private boolean trySetWithSetter(String fieldName, Object val) {
        boolean set = false;
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        // Check for a public getter
        Method setter = tryGetMethodByName("set" + capitalized, val);
        if (setter == null) {
            setter = tryGetMethodByName(capitalized, val);
        }

        if (setter != null) {
            try {
                setter.invoke(obj, val);
                set = true;
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                // nothing to do - set is already false
            }
        }
        return set;
    }

    private boolean trySetField(String fieldName, Object val) {
        boolean set = false;
        try {
            // Throws if not found so no need to check the return of getField
            obj.getClass().getField(fieldName).set(obj, val); // NOSONAR - we already tried for
            // a getter and we only get public fields so no major bypass here
            set = true;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e) {
            // nothing to do - set is already false
        }
        return set;
    }

    private Method tryGetMethodByName(String methodName, Object... params) {
        Method[] methods = obj.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName) && doParamsMatch(m.getParameters(), params)) {
                return m;
            }
        }
        return null;
    }

    private boolean doParamsMatch(Parameter[] methodParams, Object... params) {
        boolean match = true;
        if (methodParams.length != params.length) {
            match = false;
        } else // same length - check their types
        {
            match = true;
            for (int i = 0; i < methodParams.length; i++) {
                Class<?> mParamWrapped = ConversionUtils
                        .convertToWrapperClass(methodParams[i].getType());
                if (params[i] != null && // if params are not null, we check they are the right type
                        (!methodParams[i].getType().isInstance(params[i]) &&  // if they are not the
                                                                              // right type
                                (mParamWrapped == null || !mParamWrapped.isInstance(params[i])))) // and
                                                                                                  // they
                                                                                                  // are
                                                                                                  // not
                                                                                                  // primitive
                                                                                                  // types
                                                                                                  // or
                                                                                                  // the
                                                                                                  // wrapped
                                                                                                  // type
                                                                                                  // does
                                                                                                  // not
                                                                                                  // match
                {
                    match = false;
                    break;
                }
            }
        }
        return match;
    }
}
