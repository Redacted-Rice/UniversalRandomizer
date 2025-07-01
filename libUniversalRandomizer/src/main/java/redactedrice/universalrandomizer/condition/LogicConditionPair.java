package redactedrice.universalrandomizer.condition;


import redactedrice.universalrandomizer.userobjectapis.Condition;

public class LogicConditionPair<T> {
    Logic op;
    Negate negate;
    Condition<T> cond;

    public static <T2> LogicConditionPair<T2> create(Logic op, Condition<T2> cond) {
        return create(op, Negate.NO, cond);
    }

    public static <T2> LogicConditionPair<T2> create(Logic op, Negate negate, Condition<T2> cond) {
        return new LogicConditionPair<>(op, negate, cond);
    }

    protected LogicConditionPair(Logic op, Negate negate, Condition<T> cond) {
        this.op = op;
        this.negate = negate;
        this.cond = cond;
    }

    Logic getLogicOperator() {
        return op;
    }

    Negate getNegateOperator() {
        return negate;
    }

    Condition<T> getCondition() {
        return cond;
    }
}
