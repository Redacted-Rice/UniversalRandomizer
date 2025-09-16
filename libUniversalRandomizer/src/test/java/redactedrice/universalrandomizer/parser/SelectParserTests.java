package redactedrice.universalrandomizer.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redactedrice.modularparser.core.ModularParser;
import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentChainableLiteral;
import redactedrice.modularparser.literal.LiteralSupporter;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import support.SimpleObject;
import support.SimpleObjectUtils;

class SelectParserTest {
    private ModularParser parser;
    private LiteralSupporter literalSupporter;
    private Grouper grouper;
    private SelectParser testee;

    static final String NAME = SelectParser.class.getSimpleName();
    static final String KEYWORD = "select";
    static final String CHAINED_ARG = "stream";

    @BeforeEach
    void setup() {
        parser = mock(ModularParser.class);
        literalSupporter = mock(LiteralSupporter.class);
        grouper = mock(Grouper.class);

        when(parser.getSupporterOfType(LiteralSupporter.class)).thenReturn(literalSupporter);
        testee = spy(new SelectParser(grouper));
        testee.setParser(parser);
        testee.setModuleRefs();
    }

    @Test
    void defaultGrouper() {
        BaseArgumentChainableLiteral.setDefaultGrouper(grouper);
        assertEquals(grouper, BaseArgumentChainableLiteral.getDefaultGrouper());
        SelectParser defaultGrouper = new SelectParser();
        assertEquals(grouper, defaultGrouper.getGrouper());

        // Set it back to null for other tests and test that constructor ensures not null
        BaseArgumentChainableLiteral.setDefaultGrouper(null);
    }

    @Test
    void constructorSetModuleRefs() {
        assertEquals(NAME, testee.getName());
        assertEquals(KEYWORD, testee.getKeyword());
        assertEquals(CHAINED_ARG, testee.getChainedArg());
        assertEquals(2, testee.getRequiredArgs().length);
        assertEquals(CHAINED_ARG, testee.getRequiredArgs()[0]);
        assertEquals("field", testee.getRequiredArgs()[1]);
        assertEquals(0, testee.getOptionalArgs().length);
        assertEquals(0, testee.getOptionalDefaults().length);
        assertEquals(literalSupporter, testee.getLiteralSupporter());
    }

    @Test
    void tryEvaluateObject() {
        List<SimpleObject> soList = SimpleObjectUtils.soList(3);
        List<Integer> expectedInts = soList.stream().map(o -> o.intField).toList();

        Getter<SimpleObject, Integer> intGetter = o -> o.intField;
        Map<String, Object> args = Map.of("stream", soList.stream(), "field", intGetter);
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());
        assertIterableEquals(expectedInts, ((Stream<?>) result.getValue()).toList());

        // Test bad args
        args = Map.of("stream", new SimpleObject("single", 1), "field", intGetter);
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }
}