package redactedrice.universalrandomizer.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.condition.Comparison;
import redactedrice.universalrandomizer.condition.Negate;
import redactedrice.universalrandomizer.condition.SimpleCondition;
import redactedrice.universalrandomizer.userobjectapis.Condition;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import support.SimpleObject;
import support.UncomparableObject;

class SimpleConditionTests 
{	
	@Test
	void create()
	{
		Getter<SimpleObject, Integer> getter = o -> o.intField;
		Getter<SimpleObject, UncomparableObject> getterUo = o -> o.uncomparableObj;

		UncomparableObject uo = new UncomparableObject(3);
		Comparator<UncomparableObject> customComp = (lhs,rhs)-> {return Integer.compare(lhs.val, rhs.val);};
		
		SimpleCondition<SimpleObject, Integer> sc1 = SimpleCondition.create(getter, Comparison.EQUAL, 5);
		assertEquals(getter, sc1.getGetter());
		assertEquals(Negate.NO, sc1.getNegate());
		assertEquals(Comparison.EQUAL, sc1.getComparison());
		assertEquals(5, sc1.getCompareToVal());
		assertNotNull(sc1.getComparator());
		
		SimpleCondition<SimpleObject, Integer> sc2 = SimpleCondition.create(getter, Negate.YES, Comparison.GREATER_THAN, 5);
		assertEquals(getter, sc2.getGetter());
		assertEquals(Negate.YES, sc2.getNegate());
		assertEquals(Comparison.GREATER_THAN, sc2.getComparison());
		assertEquals(5, sc2.getCompareToVal());
		assertNotNull(sc2.getComparator());
		
		SimpleCondition<SimpleObject, UncomparableObject> sc3 = SimpleCondition.create(getterUo, Comparison.LESS_THAN_OR_EQUAL, uo, customComp);
		assertEquals(getterUo, sc3.getGetter());
		assertEquals(Negate.NO, sc3.getNegate());
		assertEquals(Comparison.LESS_THAN_OR_EQUAL, sc3.getComparison());
		assertEquals(uo, sc3.getCompareToVal());
		assertEquals(customComp, sc3.getComparator());
		
		SimpleCondition<SimpleObject, UncomparableObject> sc4 = SimpleCondition.create(getterUo, Negate.YES, Comparison.LESS_THAN, uo, customComp);
		assertEquals(getterUo, sc4.getGetter());
		assertEquals(Negate.YES, sc4.getNegate());
		assertEquals(Comparison.LESS_THAN, sc4.getComparison());
		assertEquals(uo, sc4.getCompareToVal());
		assertEquals(customComp, sc4.getComparator());

		SimpleCondition<SimpleObject, Integer> sc5 = SimpleCondition.create(getter, Comparison.EQUAL, null);
		assertEquals(getter, sc5.getGetter());
		assertEquals(Negate.NO, sc5.getNegate());
		assertEquals(Comparison.EQUAL, sc5.getComparison());
		assertNull(sc5.getCompareToVal());
		assertNotNull(sc5.getComparator());
	}
	
	@Test
	void enforceValidData()
	{
		Getter<SimpleObject, UncomparableObject> getter = o -> o.uncomparableObj;
		Getter<SimpleObject, UncomparableObject> getter2 = o -> o.uncomparableObj;
		
		UncomparableObject uo = new UncomparableObject(3);
		UncomparableObject uo2 = new UncomparableObject(1);
		
		Comparator<UncomparableObject> customComp = (lhs,rhs)-> {return Integer.compare(lhs.val, rhs.val);};
		Comparator<UncomparableObject> customComp2 = (lhs,rhs)-> {return Integer.compare(lhs.val, rhs.val);};
		
		SimpleCondition<SimpleObject, UncomparableObject> sc = 
				SimpleCondition.create(getter, Negate.NO, Comparison.EQUAL, uo, customComp);
		
		assertNull(SimpleCondition.create(null, Negate.YES, Comparison.GREATER_THAN, uo, customComp));
		assertNull(SimpleCondition.create(getter, null, Comparison.GREATER_THAN, uo, customComp));
		assertNull(SimpleCondition.create(getter, Negate.YES, null, uo, customComp));
		
		assertFalse(sc.setGetter(null));
		assertEquals(getter, sc.getGetter());
		assertTrue(sc.setGetter(getter2));
		assertEquals(getter2, sc.getGetter());

		assertFalse(sc.setNegate(null));
		assertEquals(Negate.NO, sc.getNegate());
		assertTrue(sc.setNegate(Negate.YES));
		assertEquals(Negate.YES, sc.getNegate());

		assertFalse(sc.setComparison(null));
		assertEquals(Comparison.EQUAL, sc.getComparison());
		assertTrue(sc.setComparison(Comparison.GREATER_THAN));
		assertEquals(Comparison.GREATER_THAN, sc.getComparison());
		

		sc.setCompareToVal(null);
		assertNull(sc.getCompareToVal());
		sc.setCompareToVal(uo2);
		assertEquals(uo2, sc.getCompareToVal());

		assertFalse(sc.setComparator(null));
		assertEquals(customComp, sc.getComparator());
		assertTrue(sc.setComparator(customComp2));
		assertEquals(customComp2, sc.getComparator());
	}
	
	@Test
	void evaluate_eq_neq()
	{
		SimpleObject testObj3 = new SimpleObject("test obj", 3);
		SimpleObject testObj5 = new SimpleObject("test obj", 5);

		Getter<SimpleObject, Integer> getter = o -> o.intField;

		Condition<SimpleObject> eq5 = SimpleCondition.create(getter, Negate.NO, Comparison.EQUAL, 5);
		Condition<SimpleObject> neq5 = SimpleCondition.create(getter, Negate.YES, Comparison.EQUAL, 5);
		
		assertTrue(eq5.evaluate(testObj5), "Simple compare eq5 failed - 5 should be equal but wasn't (returned false)");
		assertFalse(neq5.evaluate(testObj5), "Simple compare neq5 failed - 5 should be equal but wasn't (returned true)");
		assertFalse(eq5.evaluate(testObj3), "Simple compare eq5 failed - 3 should NOT be equal but was (returned true)");
		assertTrue(neq5.evaluate(testObj3), "Simple compare neq5 failed - 3 should NOT be equal but was (returned false)");
	}
	
	@Test
	void evaluate_lt_lte_nlt_nlte()
	{
		SimpleObject testObj3 = new SimpleObject("test obj", 3);
		SimpleObject testObj5 = new SimpleObject("test obj", 5);
		SimpleObject testObj7 = new SimpleObject("test obj", 7);

		Getter<SimpleObject, Integer> getter = o -> o.intField;

		Condition<SimpleObject> lt5 = SimpleCondition.create(getter, Negate.NO, Comparison.LESS_THAN, 5);
		Condition<SimpleObject> nlt5 = SimpleCondition.create(getter, Negate.YES, Comparison.LESS_THAN, 5);
		Condition<SimpleObject> lte5 = SimpleCondition.create(getter, Negate.NO, Comparison.LESS_THAN_OR_EQUAL, 5);
		Condition<SimpleObject> nlte5 = SimpleCondition.create(getter, Negate.YES, Comparison.LESS_THAN_OR_EQUAL, 5);
		
		assertTrue(lt5.evaluate(testObj3), "Simple compare lt5 failed - 3 should be less than 5 but wasn't (returned false)");
		assertFalse(nlt5.evaluate(testObj3), "Simple compare nlt5 failed - 3 should be less than 5 but wasn't (returned true)");
		assertTrue(lte5.evaluate(testObj3), "Simple compare lte5 failed - 3 should be less than or equal to 5 but wasn't (returned false)");
		assertFalse(nlte5.evaluate(testObj3), "Simple compare nlte5 failed - 3 should be less than or equal to 5 but wasn't (returned true)");
		
		assertFalse(lt5.evaluate(testObj5), "Simple compare lt5 failed - 5 should NOT be less than 5 but was (returned true)");
		assertTrue(nlt5.evaluate(testObj5), "Simple compare nlt5 failed - 5 should NOT be less than 5 but was (returned false)");
		assertTrue(lte5.evaluate(testObj5), "Simple compare lte5 failed - 5 should be less than or equal to 5 but wasn't (returned false)");
		assertFalse(nlte5.evaluate(testObj5), "Simple compare nlte5 failed - 5 should be less than or equal to 5 but wasn't (returned true)");
		
		assertFalse(lt5.evaluate(testObj7), "Simple compare lt5 failed - 7 should NOT be less than 5 but was (returned true)");
		assertTrue(nlt5.evaluate(testObj7), "Simple compare nlt5 failed - 7 should NOT be less than 5 but was (returned false)");
		assertFalse(lte5.evaluate(testObj7), "Simple compare lte5 failed - 7 should NOT be less than or equal to 5 but was (returned false)");
		assertTrue(nlte5.evaluate(testObj7), "Simple compare nlte5 failed - 7 should NOT be less than or equal to 5 but was (returned true)");
	}
	
	@Test
	void evaluate_gt_gte_ngt_ngte()
	{
		SimpleObject testObj3 = new SimpleObject("test obj", 3);
		SimpleObject testObj5 = new SimpleObject("test obj", 5);
		SimpleObject testObj7 = new SimpleObject("test obj", 7);

		Getter<SimpleObject, Integer> getter = o -> o.intField;

		Condition<SimpleObject> gt5 = SimpleCondition.create(getter, Negate.NO, Comparison.GREATER_THAN, 5);
		Condition<SimpleObject> ngt5 = SimpleCondition.create(getter, Negate.YES, Comparison.GREATER_THAN, 5);
		Condition<SimpleObject> gte5 = SimpleCondition.create(getter, Negate.NO, Comparison.GREATER_THAN_OR_EQUAL, 5);
		Condition<SimpleObject> ngte5 = SimpleCondition.create(getter, Negate.YES, Comparison.GREATER_THAN_OR_EQUAL, 5);

		assertFalse(gt5.evaluate(testObj3), "Simple compare gt5 failed - 3 should NOT be greater than 5 but was (returned true)");
		assertTrue(ngt5.evaluate(testObj3), "Simple compare ngt5 failed - 3 should NOT be greater than 5 but was (returned false)");
		assertFalse(gte5.evaluate(testObj3), "Simple compare gte5 failed - 3 should NOT be greater than or equal to 5 but was (returned false)");
		assertTrue(ngte5.evaluate(testObj3), "Simple compare ngte5 failed - 3 should NOT be greater than or equal to 5 but was (returned true)");
		
		assertFalse(gt5.evaluate(testObj5), "Simple compare gt5 failed - 5 should NOT be greater than 5 but was (returned true)");
		assertTrue(ngt5.evaluate(testObj5), "Simple compare ngt5 failed - 5 should NOT be greater than 5 but was (returned false)");
		assertTrue(gte5.evaluate(testObj5), "Simple compare gte5 failed - 5 should be greater than or equal to 5 but wasn't (returned false)");
		assertFalse(ngte5.evaluate(testObj5), "Simple compare ngte5 failed - 5 should be greater than or equal to 5 but wasn't (returned true)");

		assertTrue(gt5.evaluate(testObj7), "Simple compare gt5 failed - 7 should be greater than 5 but wasn't (returned false)");
		assertFalse(ngt5.evaluate(testObj7), "Simple compare ngt5 failed - 7 should be greater than 5 but wasn't (returned true)");
		assertTrue(gte5.evaluate(testObj7), "Simple compare gte5 failed - 7 should be greater than or equal to 5 but wasn't (returned false)");
		assertFalse(ngte5.evaluate(testObj7), "Simple compare ngte5 failed - 7 should be greater than or equal to 5 but wasn't (returned true)");
	}
	
	@Test
	void evaluate_null()
	{
		SimpleObject testObjNull = new SimpleObject(null, 5);
		SimpleObject testObjNonNull = new SimpleObject("test obj", 3);

		Getter<SimpleObject, String> getter = o -> o.stringField;
		Comparator<String> nullCapable = (lhs, rhs) -> {
			if (rhs == null)
			{
				return lhs == null ? 0 : 1;
			}
			else if (lhs == null)
			{
				return -1;
			}
			return lhs.compareTo(rhs);
		};
		
		Condition<SimpleObject> eqTestObj = SimpleCondition.create(getter, Negate.NO, Comparison.EQUAL, "test obj", nullCapable);
		Condition<SimpleObject> eqNull = SimpleCondition.create(getter, Negate.NO, Comparison.EQUAL, null, nullCapable);
		Condition<SimpleObject> neqNull = SimpleCondition.create(getter, Negate.YES, Comparison.EQUAL, null, nullCapable);
		
		assertFalse(eqTestObj.evaluate(testObjNull));
		
		assertTrue(eqNull.evaluate(testObjNull));
		assertFalse(eqNull.evaluate(testObjNonNull));
		assertFalse(neqNull.evaluate(testObjNull));
		assertTrue(neqNull.evaluate(testObjNonNull));
	}
}
