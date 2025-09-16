package redactedrice.universalrandomizer.parser;


import java.util.Map;
import java.util.stream.Stream;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentChainableLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.utils.StreamUtils;

public class SelectParser extends BaseArgumentChainableLiteral {
    private static final String[] argsOrdered = new String[] {"from", "field"};

    public SelectParser() {
        this(null);
    }

    public SelectParser(Grouper grouper) {
        super(SelectParser.class.getSimpleName(), "Select", grouper, argsOrdered[0],
                new String[] {argsOrdered[0], argsOrdered[1]}, new String[] {}, new Object[] {});
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<Stream<Object>> stream = ArgumentUtils.argToStream(argsOrdered[0], args);
        Response<Getter<Object, Object>> getter = GetterParser.parse(args.get(argsOrdered[1]));
        Response<Object> combined = Response.combineErrors(stream, getter);
        if (combined.wasError()) {
            return combined;
        }
        return Response.is(StreamUtils.field(stream.getValue(), getter.getValue()));
    }
}
