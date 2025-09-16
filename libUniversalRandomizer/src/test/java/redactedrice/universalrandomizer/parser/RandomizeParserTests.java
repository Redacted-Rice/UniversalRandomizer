package redactedrice.universalrandomizer.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redactedrice.modularparser.core.ModularParser;
import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentChainableLiteral;
import redactedrice.modularparser.literal.LiteralSupporter;
import redactedrice.universalrandomizer.pool.ReusePool;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import support.SimpleObject;
import support.SimpleObjectUtils;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class RandomizeParserTests {
    private ModularParser parser;
    private LiteralSupporter literalSupporter;
    private Grouper grouper;
    private RandomizeParser testee;
    private Random rand;

    static final String NAME = RandomizeParser.class.getSimpleName();
    static final String KEYWORD = "randomize";

    @BeforeEach
    void setup() {
        parser = mock(ModularParser.class);
        literalSupporter = mock(LiteralSupporter.class);
        grouper = mock(Grouper.class);
        rand = mock(Random.class);

        when(parser.getSupporterOfType(LiteralSupporter.class)).thenReturn(literalSupporter);
        testee = new RandomizeParser(rand, grouper);
        testee.setParser(parser);
        testee.setModuleRefs();
    }

    @Test
    void defaultGrouper() {
        BaseArgumentChainableLiteral.setDefaultGrouper(grouper);
        assertEquals(grouper, BaseArgumentChainableLiteral.getDefaultGrouper());
        RandomizeParser defaultGrouper = new RandomizeParser(rand);
        assertEquals(grouper, defaultGrouper.getGrouper());

        // Set it back to null for other tests and test that constructor ensures not null
        BaseArgumentChainableLiteral.setDefaultGrouper(null);
    }

    @Test
    void constructorSetModuleRefs() {
        assertEquals(NAME, testee.getName());
        assertEquals(KEYWORD, testee.getKeyword());
        assertEquals(3, testee.getRequiredArgs().length);
        assertEquals("field", testee.getRequiredArgs()[0]);
        assertEquals("of", testee.getRequiredArgs()[1]);
        assertEquals("using", testee.getRequiredArgs()[2]);
        assertEquals(0, testee.getOptionalArgs().length);
        assertEquals(0, testee.getOptionalDefaults().length);
        assertEquals(literalSupporter, testee.getLiteralSupporter());
    }

    @Test
    void perform_basic() {
        final int POOL_VAL = 5;
        final int LIST_SIZE = 10;
        List<SimpleObject> soList = SimpleObjectUtils.soList(LIST_SIZE);

        // Set expectations - since we control rand and the pool, it should all be the same
        List<Integer> expected = new LinkedList<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            expected.add(POOL_VAL);
        }

        // Setup mocks
        when(rand.nextInt(anyInt())).thenReturn(0);

        @SuppressWarnings("unchecked")
        ReusePool<Integer> pool = mock(ReusePool.class);
        when(pool.get(any())).thenReturn(POOL_VAL);
        when(pool.copy()).thenReturn(pool);

        Setter<SimpleObject, Integer> setter = SimpleObject::setIntFieldReturn;

        Map<String, Object> args = Map.of("field", setter, "of", soList, "using", pool);
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        assertTrue((Boolean) result.getValue());

        // Ensure the "randomization" happened
        List<Integer> found = soList.stream().map(SimpleObject::getIntField).toList();
        assertIterableEquals(expected, found);

        // Test bad value
        args = Map.of("field", setter, "of", 5, "using", pool);
        assertTrue(testee.tryEvaluateObject(args).wasError());
        assertTrue((Boolean) result.getValue());
    }
}
