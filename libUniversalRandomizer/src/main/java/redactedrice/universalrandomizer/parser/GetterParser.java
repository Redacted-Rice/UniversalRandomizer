package redactedrice.universalrandomizer.parser;


import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.reflectionhelpers.utils.ReflectionUtils;
import redactedrice.universalrandomizer.userobjectapis.Getter;

public class GetterParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"path"};

    public GetterParser() {
        this(null);
    }

    public GetterParser(Grouper grouper) {
        super(GetterParser.class.getSimpleName(), "Getter", grouper, new String[] {argsOrdered[0]},
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

    protected static Response<Getter<Object, Object>> parsePath(String path) {
        Getter<Object, Object> getter = o -> {
            try {
                return ReflectionUtils.getVariable(o, path);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
                return null;
            }
        };
        return Response.is(getter);
    }

    @SuppressWarnings("unchecked")
    public static Response<Getter<Object, Object>> parse(Object getter) {
        if (getter instanceof Getter<?, ?> asGetter) {
            return Response.is((Getter<Object, Object>) asGetter);
        } else if (getter instanceof String asString) {
            return parsePath(asString);
        }
        return Response.error("Failed to parse Getter object or path string");
    }
}
