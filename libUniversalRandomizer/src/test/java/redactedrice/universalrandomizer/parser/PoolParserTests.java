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
import redactedrice.universalrandomizer.pool.RandomizerSinglePool;
import redactedrice.universalrandomizer.pool.ReusePool;
import redactedrice.universalrandomizer.testsupport.SimpleObject;
import redactedrice.universalrandomizer.testsupport.SimpleObjectUtils;

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
        assertEquals("from", testee.getRequiredArgs()[0]);

        assertEquals(3, testee.getOptionalArgs().length);
        assertEquals("onuse", testee.getOptionalArgs()[0]);
        assertEquals("duplicates", testee.getOptionalArgs()[1]);
        assertEquals("depth", testee.getOptionalArgs()[2]);

        assertEquals(3, testee.getOptionalDefaults().length);
        assertEquals("keep", testee.getOptionalDefaults()[0]);
        assertEquals("keep", testee.getOptionalDefaults()[1]);
        assertEquals(1, testee.getOptionalDefaults()[2]);

        assertEquals(literalSupporter, testee.getLiteralSupporter());
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryEvaluateObject_reuse() {
        List<SimpleObject> soList = SimpleObjectUtils.soList(3);
        soList.add(soList.get(0)); // add a duplicate
        Map<String, Object> args = Map.of("from", soList.stream(), "onuse", "keep", "duplicates",
                "keep");
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        ReusePool<SimpleObject> casted = (ReusePool<SimpleObject>) result.getValue();
        assertEquals(4, casted.size());

        // Try some other values
        args = Map.of("from", soList.stream(), "onuse", "keep", "duplicates", "remove");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        casted = (ReusePool<SimpleObject>) result.getValue();
        assertEquals(3, casted.size());

        // Bad args
        args = Map.of("from", soList.stream(), "onuse", "keep", "duplicates", "bad");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryEvaluateObject_eliminate() {
        List<SimpleObject> soList = SimpleObjectUtils.soList(3);
        soList.add(soList.get(0)); // add a duplicate
        Map<String, Object> args = Map.of("from", soList.stream(), "onuse", "remove",
                "duplicates", "keep", "depth", 3);
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        EliminatePoolSet<SimpleObject> casted = (EliminatePoolSet<SimpleObject>) result.getValue();
        assertEquals(4, casted.originalPoolSize());
        assertEquals(3, casted.maxDepth());

        // Try some other values
        args = Map.of("from", soList.stream(), "onuse", "remove", "duplicates", "remove", "depth",
                "unlimited");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        casted = (EliminatePoolSet<SimpleObject>) result.getValue();
        assertEquals(3, casted.originalPoolSize());
        assertEquals(-1, casted.maxDepth());

        // Bad args
        args = Map.of("from", soList.stream(), "onuse", "false", "duplicates", "keep", "depth",
                "bad");
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }

    @Test
    void parse() {
        List<SimpleObject> soList = SimpleObjectUtils.soList(3);

        Response<RandomizerSinglePool<Object>> result = PoolParser.parse(soList);
        assertTrue(result.wasValueReturned());
        ReusePool<Object> asPool = (ReusePool<Object>) result.getValue();
        assertEquals(3, asPool.size());

        RandomizerSinglePool<SimpleObject> pool = ReusePool.create(soList);
        result = PoolParser.parse((Object) pool);
        assertTrue(result.wasValueReturned());
        asPool = (ReusePool<Object>) result.getValue();
        assertEquals(3, asPool.size());

        result = PoolParser.parse(5);
        assertTrue(result.wasError());
    }
}
