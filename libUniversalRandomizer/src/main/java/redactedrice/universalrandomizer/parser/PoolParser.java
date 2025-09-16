package redactedrice.universalrandomizer.parser;


import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.reflectionhelpers.utils.ConversionUtils;
import redactedrice.universalrandomizer.pool.EliminatePool;
import redactedrice.universalrandomizer.pool.EliminatePoolSet;
import redactedrice.universalrandomizer.pool.RandomizerSinglePool;
import redactedrice.universalrandomizer.pool.ReusePool;

public class PoolParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"from", "type", "duplicates",
            "depth"};
    protected static final Object[] argsDefault = new Object[] {"reuse", "allow", 1};
    protected static final Map<Object, Integer> specialValues = Map.of("unlimited", -1);

    public PoolParser() {
        this(null);
    }

    public PoolParser(Grouper grouper) {
        super(PoolParser.class.getSimpleName(), "Pool", grouper, new String[] {argsOrdered[0]},
                new String[] {argsOrdered[1], argsOrdered[2], argsOrdered[3]}, argsDefault);
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<Stream<Object>> values = ArgumentUtils.argToStream(argsOrdered[0], args);
        Response<Boolean> reuse = ArgumentUtils.argDichotomyToBool(argsOrdered[1], args, "reuse",
                "eliminate");
        Response<Boolean> removeDupes = ArgumentUtils.argDichotomyToBool(argsOrdered[2], args,
                "remove", "allow");

        Response<Integer> depth = Response.is((Integer) argsDefault[2]);
        if (reuse.wasValueReturned() && Boolean.FALSE.equals(reuse.getValue())) {
            depth = ArgumentUtils.argToType(argsOrdered[3], args, Integer.class, specialValues);
        }

        Response<Object> allErrors = Response.combineErrors(reuse, values, removeDupes, depth);
        if (allErrors.wasError()) {
            return allErrors;
        }

        if (Boolean.TRUE.equals(reuse.getValue())) {
            return Response
                    .is(ReusePool.create(removeDupes.getValue(), values.getValue().toList()));
        } else {
            return Response.is(EliminatePoolSet.create(
                    EliminatePool.create(removeDupes.getValue(), values.getValue().toList()),
                    depth.getValue()));
        }
    }

    @SuppressWarnings("unchecked")
    public static Response<RandomizerSinglePool<Object>> parse(Object pool) {
        if (pool instanceof RandomizerSinglePool<?> asPool) {
            return Response.is((RandomizerSinglePool<Object>) asPool);
        }
        Collection<Object> asCollection = ConversionUtils.convertToCollection(pool);
        if (asCollection.size() > 1) {
            return Response.is(ReusePool.create(asCollection));
        }
        return Response.error("Failed to parse Pool object or collection/stream: " + pool);
    }
}