package redactedrice.universalrandomizer.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redactedrice.modularparser.core.ModularParser;
import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentChainableLiteral;
import redactedrice.modularparser.literal.LiteralSupporter;
import redactedrice.universalrandomizer.pool.EliminatePoolSet;
import redactedrice.universalrandomizer.pool.ReusePool;
import support.SimpleObject;
import support.SimpleObjectUtils;

class PoolParserTests {
    private ModularParser parser;
    private LiteralSupporter literalSupporter;
    private Grouper grouper;
    private PoolParser testee;

    static final String NAME = PoolParser.class.getSimpleName();
    static final String KEYWORD = "pool";

    @BeforeEach
    void setup() {
        parser = mock(ModularParser.class);
        literalSupporter = mock(LiteralSupporter.class);
        grouper = mock(Grouper.class);

        when(parser.getSupporterOfType(LiteralSupporter.class)).thenReturn(literalSupporter);
        testee = new PoolParser(grouper);
        testee.setParser(parser);
        testee.setModuleRefs();
    }

    @Test
    void defaultGrouper() {
        BaseArgumentChainableLiteral.setDefaultGrouper(grouper);
        assertEquals(grouper, BaseArgumentChainableLiteral.getDefaultGrouper());
        PoolParser defaultGrouper = new PoolParser();
        assertEquals(grouper, defaultGrouper.getGrouper());

        // Set it back to null for other tests and test that constructor ensures not null
        BaseArgumentChainableLiteral.setDefaultGrouper(null);
    }

    @Test
    void constructorSetModuleRefs() {
        assertEquals(NAME, testee.getName());
        assertEquals(KEYWORD, testee.getKeyword());

        assertEquals(1, testee.getRequiredArgs().length);
        assertEquals("basedon", testee.getRequiredArgs()[0]);

        assertEquals(3, testee.getOptionalArgs().length);
        assertEquals("type", testee.getOptionalArgs()[0]);
        assertEquals("duplicates", testee.getOptionalArgs()[1]);
        assertEquals("depth", testee.getOptionalArgs()[2]);

        assertEquals(3, testee.getOptionalDefaults().length);
        assertEquals("reuse", testee.getOptionalDefaults()[0]);
        assertEquals("allow", testee.getOptionalDefaults()[1]);
        assertEquals(1, testee.getOptionalDefaults()[2]);

        assertEquals(literalSupporter, testee.getLiteralSupporter());
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryEvaluateObject_reuse() {
        List<SimpleObject> soList = SimpleObjectUtils.soList(3);
        soList.add(soList.get(0)); // add a duplicate
        Map<String, Object> args = Map.of("basedon", soList.stream(), "type", "reuse", "duplicates",
                "allow");
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        ReusePool<SimpleObject> casted = (ReusePool<SimpleObject>) result.getValue();
        assertEquals(4, casted.size());

        // Try some other values
        args = Map.of("basedon", soList.stream(), "type", "reuse", "duplicates", "remove");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        casted = (ReusePool<SimpleObject>) result.getValue();
        assertEquals(3, casted.size());

        // Bad args
        args = Map.of("basedon", soList.stream(), "type", "reuse", "duplicates", "bad");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryEvaluateObject_eliminate() {
        List<SimpleObject> soList = SimpleObjectUtils.soList(3);
        soList.add(soList.get(0)); // add a duplicate
        Map<String, Object> args = Map.of("basedon", soList.stream(), "type", "eliminate",
                "duplicates", "allow", "depth", 3);
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        EliminatePoolSet<SimpleObject> casted = (EliminatePoolSet<SimpleObject>) result.getValue();
        assertEquals(4, casted.originalPoolSize());
        assertEquals(3, casted.maxDepth());

        // Try some other values
        args = Map.of("basedon", soList.stream(), "type", "eliminate", "duplicates", "remove",
                "depth", "unlimited");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        casted = (EliminatePoolSet<SimpleObject>) result.getValue();
        assertEquals(3, casted.originalPoolSize());
        assertEquals(-1, casted.maxDepth());

        // Bad args
        args = Map.of("basedon", soList.stream(), "type", "false", "duplicates", "allow", "depth",
                "bad");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }
}
