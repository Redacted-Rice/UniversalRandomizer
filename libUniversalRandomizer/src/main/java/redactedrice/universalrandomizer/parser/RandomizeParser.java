package redactedrice.universalrandomizer.parser;


import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.universalrandomizer.pool.RandomizerSinglePool;
import redactedrice.universalrandomizer.randomize.Randomizer;
import redactedrice.universalrandomizer.randomize.SingleRandomizer;
import redactedrice.universalrandomizer.userobjectapis.Setter;

public class RandomizeParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"field", "of", "using"};
    protected Random rand;

    public RandomizeParser(Random rand) {
        this(rand, null);
    }

    public RandomizeParser(Random rand, Grouper grouper) {
        super(RandomizeParser.class.getSimpleName(), "randomize", grouper,
                new String[] {argsOrdered[0], argsOrdered[1], argsOrdered[2]}, new String[] {},
                new String[] {});
        this.rand = rand;
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<Setter<Object, Object>> setter = SetterParser.parse(args.get(argsOrdered[0]));
        Response<Stream<Object>> stream = ArgumentUtils.argToStream(argsOrdered[1], args);
        Response<RandomizerSinglePool<Object>> pool = PoolParser.parse(args.get(argsOrdered[2]));
        Response<Object> combined = Response.combineErrors(setter, stream, pool);
        if (combined.wasError()) {
            return combined;
        }

        Randomizer<Object, Object> randomizer = SingleRandomizer.create(setter.getValue());
        return Response.is(randomizer.perform(stream.getValue(), pool.getValue(), rand));
    }
}