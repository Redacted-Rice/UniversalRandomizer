package redactedrice.universalrandomizer.parser;


import java.util.Map;
import java.util.stream.Stream;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.universalrandomizer.pool.EliminatePool;
import redactedrice.universalrandomizer.pool.EliminatePoolSet;
import redactedrice.universalrandomizer.pool.ReusePool;

public class PoolParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"type", "basedon", "duplicates",
            "depth"};
    protected static final Object[] argsDefault = new Object[] {"reuse", "times", "allow", 1};

    public PoolParser() {
        this(null);
    }

    public PoolParser(Grouper grouper) {
        super(PoolParser.class.getSimpleName(), "Pool", grouper,
                new String[] {argsOrdered[0], argsOrdered[1]}, new String[] {argsOrdered[2]},
                new Object[] {1});
    }

    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<Boolean> reuse = ArgumentUtils.argDichotomyToBool(argsOrdered[0], args, "reuse",
                "eliminate");
        Response<Stream<Object>> values = ArgumentUtils.argToStream(argsOrdered[1], args);
        Response<Boolean> removeDupes = ArgumentUtils.argDichotomyToBool(argsOrdered[2], args,
                "remove", "allow");

        // TODO: support no limit (-1)
        Response<Integer> depth = Response.is((Integer) argsDefault[3]);
        if (reuse.wasValueReturned() && Boolean.FALSE.equals(reuse.getValue())) {
            depth = ArgumentUtils.argToType(argsOrdered[3], args, Integer.class);
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
}