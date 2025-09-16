package redactedrice.universalrandomizer.parser;


import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.reflectionhelpers.utils.ReflectionUtils;
import redactedrice.universalrandomizer.userobjectapis.Setter;

public class SetterParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"field"};

    public SetterParser() {
        this(null);
    }

    public SetterParser(Grouper grouper) {
        super(SetterParser.class.getSimpleName(), "setter", grouper, new String[] {argsOrdered[0]},
                new String[] {}, new Object[] {});
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<String> path = ArgumentUtils.argToType(argsOrdered[0], args, String.class);
        if (path.wasError()) {
            return path.convert(Object.class);
        }
        return parsePath(path.getValue()).convert(Object.class);
    }

    protected static Response<Setter<Object, Object>> parsePath(String path) {
        Setter<Object, Object> setter = (o, v) -> {
            try {
                Object ret = ReflectionUtils.setVariable(o, path, v);
                return !Boolean.FALSE.equals(ret);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
                return false;
            }
        };
        return Response.is(setter);
    }

    @SuppressWarnings("unchecked")
    public static Response<Setter<Object, Object>> parse(Object setter) {
        if (setter instanceof Setter<?, ?> asSetter) {
            return Response.is((Setter<Object, Object>) asSetter);
        } else if (setter instanceof String asString) {
            return parsePath(asString);
        }
        return Response.error("Failed to parse Setter object or path string: " + setter);
    }
}
