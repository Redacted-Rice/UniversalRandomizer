package redactedrice.universalrandomizer.parser;


import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.reflectionhelpers.utils.ReflectionUtils;
import redactedrice.universalrandomizer.userobjectapis.MultiGetter;

// TODO: Not sure if this is a separate parser yet or not
public class MultiGetterParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"path"};

    public MultiGetterParser() {
        this(null);
    }

    public MultiGetterParser(Grouper grouper) {
        super(MultiGetterParser.class.getSimpleName(), "MultiGetter", grouper,
                new String[] {argsOrdered[0]}, new String[] {}, new Object[] {});
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        try {
            Response<String> path = ArgumentUtils.argToType(argsOrdered[0], args, String.class);
            if (path.wasError()) {
                return path.convert(Object.class);
            }

            MultiGetter<?, ?> multiGetter = (o, cnt) -> {
                try {
                    return ReflectionUtils.invoke(o, path.getValue(), cnt);
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException
                        | NoSuchFieldException e) {
                    return null;
                }
            };
            return Response.is(multiGetter);
        } catch (ClassCastException e) {
            // This should have been handled if this was called
            return Response.error("failed to cast value: " + e.getMessage());
        }
    }
}
