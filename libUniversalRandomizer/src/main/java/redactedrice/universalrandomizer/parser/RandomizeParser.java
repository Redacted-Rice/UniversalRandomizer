package redactedrice.universalrandomizer.parser;


import java.util.Map;
import java.util.stream.Stream;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.universalrandomizer.userobjectapis.Setter;

public class RandomizeParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"field", "of", "using"};

    public RandomizeParser() {
        this(null);
    }

    public RandomizeParser(Grouper grouper) {
        super(RandomizeParser.class.getSimpleName(), "randomize", grouper,
                new String[] {argsOrdered[0], argsOrdered[1], argsOrdered[2]}, new String[] {},
                new String[] {});
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<Setter<Object, Object>> setter = SetterParser.parse(args.get(argsOrdered[0]));
        Response<Stream<Object>> stream = ArgumentUtils.argToStream(argsOrdered[1], args);
        // Response<RandomizerPool<Object>> pool = PoolParser.parse(args.get(argsOrdered[2]));
        Response<Object> combined = Response.combineErrors(setter, stream);// , pool);
        if (combined.wasError()) {
            return combined;
        }

        // TODO: Actual logic

        // Nothing to return
        return Response.is(null);
    }
}