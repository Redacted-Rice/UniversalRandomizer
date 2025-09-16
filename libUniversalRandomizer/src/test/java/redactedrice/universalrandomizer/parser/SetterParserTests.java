package redactedrice.universalrandomizer.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redactedrice.modularparser.core.ModularParser;
import redactedrice.modularparser.core.Response;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.literal.BaseArgumentChainableLiteral;
import redactedrice.modularparser.literal.LiteralSupporter;
import redactedrice.universalrandomizer.testsupport.SimpleObject;
import redactedrice.universalrandomizer.userobjectapis.Setter;

class SetterParserTests {
    private ModularParser parser;
    private LiteralSupporter literalSupporter;
    private Grouper grouper;
    private SetterParser testee;

    static final String NAME = SetterParser.class.getSimpleName();
    static final String KEYWORD = "setter";

    @BeforeEach
    void setup() {
        parser = mock(ModularParser.class);
        literalSupporter = mock(LiteralSupporter.class);
        grouper = mock(Grouper.class);

        when(parser.getSupporterOfType(LiteralSupporter.class)).thenReturn(literalSupporter);
        testee = new SetterParser(grouper);
        testee.setParser(parser);
        testee.setModuleRefs();
    }

    @Test
    void defaultGrouper() {
        BaseArgumentChainableLiteral.setDefaultGrouper(grouper);
        assertEquals(grouper, BaseArgumentChainableLiteral.getDefaultGrouper());
        SetterParser defaultGrouper = new SetterParser();
        assertEquals(grouper, defaultGrouper.getGrouper());

        // Set it back to null for other tests and test that constructor ensures not null
        BaseArgumentChainableLiteral.setDefaultGrouper(null);
    }

    @Test
    void constructorSetModuleRefs() {
        assertEquals(NAME, testee.getName());
        assertEquals(KEYWORD, testee.getKeyword());
        assertEquals(1, testee.getRequiredArgs().length);
        assertEquals("field", testee.getRequiredArgs()[0]);
        assertEquals(0, testee.getOptionalArgs().length);
        assertEquals(0, testee.getOptionalDefaults().length);
        assertEquals(literalSupporter, testee.getLiteralSupporter());
    }

    @SuppressWarnings("unchecked")
    @Test
    void tryEvaluateObject() {
        Map<String, Object> args = Map.of("field", "setIntFieldReturn()");
        Response<Object> result = testee.tryEvaluateObject(args);
        assertTrue(result.wasValueReturned());

        SimpleObject testObj = new SimpleObject("so1", 1);
        assertTrue(((Setter<Object, Object>) result.getValue()).setReturn(testObj, 2));
        assertEquals(2, testObj.intField);
        // Setter doesn't allow negative
        assertFalse(((Setter<Object, Object>) result.getValue()).setReturn(testObj, -1));
        assertEquals(2, testObj.intField);
        assertFalse(((Setter<Object, Object>) result.getValue()).setReturn(testObj, "bad"));
        assertEquals(2, testObj.intField);

        // Test bad args
        args = Map.of("field", 5);
        result = testee.tryEvaluateObject(args);
        assertTrue(result.wasError());
    }

    @Test
    void parse() {
        SimpleObject testObj = new SimpleObject("so1", 1);

        Response<Setter<Object, Object>> result = SetterParser.parse("setIntFieldReturn()");
        assertTrue(result.wasValueReturned());
        assertTrue(((Setter<Object, Object>) result.getValue()).setReturn(testObj, 2));
        assertEquals(2, testObj.intField);
        // Setter doesn't allow negative
        assertFalse(((Setter<Object, Object>) result.getValue()).setReturn(testObj, -1));
        assertEquals(2, testObj.intField);
        assertFalse(((Setter<Object, Object>) result.getValue()).setReturn(testObj, "bad"));
        assertEquals(2, testObj.intField);

        Setter<SimpleObject, Integer> intSetter = SimpleObject::setIntFieldReturn;
        result = SetterParser.parse((Object) intSetter);
        assertTrue(result.wasValueReturned());
        assertTrue(((Setter<Object, Object>) result.getValue()).setReturn(testObj, 2));
        assertEquals(2, testObj.intField);
        // Setter doesn't allow negative
        assertFalse(((Setter<Object, Object>) result.getValue()).setReturn(testObj, -1));
        assertEquals(2, testObj.intField);
        // For now this is unsafe and throws. In the future we may make it safe and instead
        // return null
        Setter<Object, Object> resultAsSetter = ((Setter<Object, Object>) result.getValue());
        assertThrows(ClassCastException.class, () -> {
            resultAsSetter.setReturn(testObj, "bad");
        });

        result = SetterParser.parse(5);
        assertTrue(result.wasError());
    }
}
