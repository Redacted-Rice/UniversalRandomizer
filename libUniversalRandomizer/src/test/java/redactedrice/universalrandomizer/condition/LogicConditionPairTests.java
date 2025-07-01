package redactedrice.universalrandomizer.condition;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.userobjectapis.Condition;
import support.SimpleObject;

class LogicConditionPairTests {
    @Test
    void create() {
        Condition<SimpleObject> eq5 = SimpleCondition.create(o -> o.intField, Negate.NO,
                Comparison.EQUAL, 5);

        LogicConditionPair<SimpleObject> lcp = LogicConditionPair.create(Logic.OR, eq5);
        assertEquals(Logic.OR, lcp.getLogicOperator(), "Logic Operator was not created correctly");
        assertEquals(Negate.NO, lcp.getNegateOperator(), "Negate was not created correctly");
        assertEquals(eq5, lcp.getCondition(), "Condition was not created correctly");

        LogicConditionPair<SimpleObject> lcpNeg = LogicConditionPair.create(Logic.XNOR, Negate.YES,
                eq5);
        assertEquals(Logic.XNOR, lcpNeg.getLogicOperator(),
                "Logic Operator was not created correctly");
        assertEquals(Negate.YES, lcpNeg.getNegateOperator(), "Negate was not created correctly");
        assertEquals(eq5, lcpNeg.getCondition(), "Condition was not created correctly");
    }
}
