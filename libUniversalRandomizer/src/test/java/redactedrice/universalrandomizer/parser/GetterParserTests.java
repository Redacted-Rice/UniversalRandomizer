package redactedrice.universalrandomizer.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redactedrice.modularparser.core.ModularParser;
import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentChainableLiteral;
import redactedrice.modularparser.literal.LiteralSupporter;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import support.SimpleObject;

class GetterParserTests {
    private ModularParser parser;
    private LiteralSupporter literalSupporter;
    private Grouper grouper;
    private GetterParser testee;

    static final String NAME = GetterParser.class.getSimpleName();
    static final String KEYWORD = "getter";
    static final String CHAINED_ARG = "stream";

    @BeforeEach
    void setup() {
        parser = mock(ModularParser.class);
        literalSupporter = mock(LiteralSupporter.class);
        grouper = mock(Grouper.class);

        when(parser.getSupporterOfType(LiteralSupporter.class)).thenReturn(literalSupporter);
        testee = spy(new GetterParser(grouper));
        testee.setParser(parser);
        testee.setModuleRefs();
    }

    @Test
    void defaultGrouper() {
        BaseArgumentChainableLiteral.setDefaultGrouper(grouper);
        assertEquals(grouper, BaseArgumentChainableLiteral.getDefaultGrouper());
        GetterParser defaultGrouper = new GetterParser();
        assertEquals(grouper, defaultGrouper.getGrouper());

        // Set it back to null for other tests and test that constructor ensures not null
        BaseArgumentChainableLiteral.setDefaultGrouper(null);
    }

    @Test
    void constructorSetModuleRefs() {
        assertEquals(NAME, testee.getName());
        assertEquals(KEYWORD, testee.getKeyword());
        assertEquals(1, testee.getRequiredArgs().length);
        assertEquals("path", testee.getRequiredArgs()[0]);
        assertEquals(0, testee.getOptionalArgs().length);
        assertEquals(0, testee.getOptionalDefaults().length);
        assertEquals(literalSupporter, testee.getLiteralSupporter());
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryEvaluateObject() {
        Map<String, Object> args = Map.of("path", "intField");
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());

        SimpleObject testObj = new SimpleObject("so1", 1);
        assertEquals(testObj.intField, ((Getter<Object, Object>) result.getValue()).get(testObj));
        assertNull(((Getter<Object, Object>) result.getValue()).get("some bad object"));

        // Test bad args
        args = Map.of("path", 5);
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }

    @Test
    void parse() {
        SimpleObject testObj = new SimpleObject("so1", 1);

        Response<Getter<Object, Object>> result = GetterParser.parse("intField");
        assertTrue(result.wasValueReturned());
        assertEquals(testObj.intField, ((Getter<Object, Object>) result.getValue()).get(testObj));
        assertNull(((Getter<Object, Object>) result.getValue()).get("some bad object"));

        Getter<SimpleObject, Integer> intGetter = o -> o.intField;
        result = GetterParser.parse((Object) intGetter);
        assertTrue(result.wasValueReturned());
        assertEquals(testObj.intField, ((Getter<Object, Object>) result.getValue()).get(testObj));
        // For now this is unsafe and throws. In the future we may make it safe and instead
        // return null
        Getter<Object, Object> resultAsGetter = ((Getter<Object, Object>) result.getValue());
        assertThrows(ClassCastException.class, () -> {
            resultAsGetter.get("some bad object");
        });

        result = GetterParser.parse(5);
        assertTrue(result.wasError());
    }
}
