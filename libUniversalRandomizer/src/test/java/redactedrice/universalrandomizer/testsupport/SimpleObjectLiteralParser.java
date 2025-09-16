package redactedrice.universalrandomizer.testsupport;


import java.util.Map;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;

public class SimpleObjectLiteralParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"str", "int"};

    public SimpleObjectLiteralParser() {
        this(null);
    }

    public SimpleObjectLiteralParser(Grouper grouper) {
        super(SimpleObjectLiteralParser.class.getSimpleName(), "SimpleObject", grouper,
                new String[] {argsOrdered[0]}, new String[] {argsOrdered[1]}, new Object[] {1});
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        try {
            return Response.is(new SimpleObject((String) args.get(argsOrdered[0]),
                    (int) args.get(argsOrdered[1])));
        } catch (ClassCastException e) {
            // This should have been handled if this was called
            return Response.error("failed to cast value: " + e.getMessage());
        }
    }
}
