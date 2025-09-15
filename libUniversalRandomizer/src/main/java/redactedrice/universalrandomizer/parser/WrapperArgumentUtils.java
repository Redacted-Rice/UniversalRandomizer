package redactedrice.universalrandomizer.parser;


import java.util.Map;

import redactedrice.modularparser.core.Response;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiGetter;

public class WrapperArgumentUtils {
    private WrapperArgumentUtils() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("unchecked")
    public static Response<MultiGetter<Object, Object>> argToMutliGetter(String fieldName,
            Map<String, Object> args) {
        Object val = args.get(fieldName);
        if (val instanceof Getter<?, ?> asGetter) {
            return Response.is(((Getter<Object, Object>) asGetter).asMultiGetter());
        } else if (val instanceof MultiGetter<?, ?> asMultiGetter) {
            return Response.is((MultiGetter<Object, Object>) asMultiGetter);
        }
        return Response
                .error("Invalid type: 'Getter' or 'MulitGetter' expected for '" + fieldName + "'");
    }
}
