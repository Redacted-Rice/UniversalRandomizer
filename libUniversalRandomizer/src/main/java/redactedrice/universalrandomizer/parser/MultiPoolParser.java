package redactedrice.universalrandomizer.parser;


import java.util.Map;
import java.util.stream.Stream;

import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentLiteral;
import redactedrice.modularparser.utils.ArgumentUtils;
import redactedrice.universalrandomizer.userobjectapis.MultiGetter;

public class MultiPoolParser extends BaseArgumentLiteral {
    protected static final String[] argsOrdered = new String[] {"type", "basedon", "by",
            "duplicates", "depth"};
    protected static final Object[] argsDefault = new Object[] {"reuse", "times", "not supplied",
            "allow", 1};

    public MultiPoolParser() {
        this(null);
    }

    public MultiPoolParser(Grouper grouper) {
        super(PoolParser.class.getSimpleName(), "Pool", grouper,
                new String[] {argsOrdered[0], argsOrdered[1]}, new String[] {argsOrdered[2]},
                new Object[] {1});
    }

    // Not sure if this is different than pool parser or will be part of it
    @Override
    public Response<Object> tryEvaluateObject(Map<String, Object> args) {
        Response<Boolean> reuse = ArgumentUtils.argDichotomyToBool(argsOrdered[0], args, "reuse",
                "eliminate");
        Response<Stream<Object>> values = ArgumentUtils.argToStream(argsOrdered[1], args);
        Response<MultiGetter<Object, Object>> getter = WrapperArgumentUtils
                .argToMutliGetter(argsOrdered[2], args);
        Response<Boolean> removeDupes = ArgumentUtils.argDichotomyToBool(argsOrdered[3], args,
                "remove", "allow");

        Response<Integer> depth = Response.is((Integer) argsDefault[4]);
        if (reuse.wasValueReturned() && Boolean.FALSE.equals(reuse.getValue())) {
            depth = ArgumentUtils.argToType(argsOrdered[3], args, Integer.class);
        }

        Response<Object> allErrors = Response.combineErrors(reuse, values, getter, removeDupes,
                depth);
        if (allErrors.wasError()) {
            return allErrors;
        }

        // if (Boolean.TRUE.equals(reuse.getValue())) {
        // Map<Object, RandomizerPool<Object>> poolMap = new HashMap<>();
        // return Response.is(MultiPool.create(poolMap, getter.getValue()));
        // } else {
        // return Response.is(EliminatePoolSet.create(
        // EliminatePool.create(removeDupes.getValue(), values.getValue().toList()),
        // depth.getValue()));
        // }
        return Response.notHandled();
    }
}