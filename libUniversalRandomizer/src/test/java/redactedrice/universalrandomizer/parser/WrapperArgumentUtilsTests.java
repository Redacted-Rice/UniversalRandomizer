package redactedrice.universalrandomizer.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import redactedrice.modularparser.core.Response;
import redactedrice.universalrandomizer.testsupport.SimpleObject;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiGetter;

class WrapperArgumentUtilsTests {

    @Test
    void argToMutliGetter() {
        int soInt = 1;
        Getter<SimpleObject, Integer> soGetInt = (so) -> so.getIntField();
        MultiGetter<SimpleObject, Integer> soMultiGetInt = (so, cnt) -> so.getIntField();

        String getArgName = "get";
        String multiGetArgName = "multiGet";
        String nonGetterArgName = "stringObj";
        Map<String, Object> args = Map.of(getArgName, soGetInt, multiGetArgName, soMultiGetInt,
                nonGetterArgName, "test");
        Response<MultiGetter<Object, Object>> recast = WrapperArgumentUtils
                .argToMutliGetter(getArgName, args);

        SimpleObject so = new SimpleObject("so1", soInt);

        assertTrue(recast.wasValueReturned());
        assertEquals(1, recast.getValue().get(so, 1));

        recast = WrapperArgumentUtils.argToMutliGetter(multiGetArgName, args);
        assertTrue(recast.wasValueReturned());
        assertEquals(1, recast.getValue().get(so, 1));

        recast = WrapperArgumentUtils.argToMutliGetter(nonGetterArgName, args);
        assertTrue(recast.wasError());
    }
}
