package redactedrice.universalrandomizer.condition;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.userobjectapis.Condition;
import support.SimpleObject;

class CompoundConditionTests 
{	
	@SuppressWarnings("serial")
	final Map<String, SimpleObject> OBJ_LIST = Collections.unmodifiableMap(new LinkedHashMap<String, SimpleObject>() {
	    {
	        put("1 5", new SimpleObject("1", 5));
	        put("1 7", new SimpleObject("1", 7));
	        put("2 5", new SimpleObject("2", 5));
	        put("2 7", new SimpleObject("2", 7));
	    }
	});

	@SuppressWarnings("serial")
	final Map<String, Condition<SimpleObject>> SIMPLE_CONDS = Collections.unmodifiableMap(new LinkedHashMap<String, Condition<SimpleObject>>() {
	    {
	        put("intEq5", SimpleCondition.create(o -> o.intField, Negate.NO, Comparison.EQUAL, 5));
	        put("strEq2", SimpleCondition.create(o -> o.stringField, Negate.NO, Comparison.EQUAL, "2"));
	    }
	});

	public void assertExpectedResults(String label, CompoundCondition<SimpleObject> cond, Map<String, SimpleObject> toTest, List<Boolean> expectedResults)
	{
		assert(toTest.size() == expectedResults.size());
		
		Iterator<Boolean> itr = expectedResults.iterator();
		for (Entry<String, SimpleObject> pair : toTest.entrySet())
		{
			boolean expected = itr.next();
			assertEquals(expected, cond.evaluate(pair.getValue()), label + " expected " + expected + " but failed for simple object " + pair.getKey());
		}
	}
	
	@Test
	void create() 
	{	
		LogicConditionPair<SimpleObject> lcp1 = LogicConditionPair.create(Logic.NAND, SIMPLE_CONDS.get("strEq2"));
		LogicConditionPair<SimpleObject> lcp2 = LogicConditionPair.create(Logic.OR, SIMPLE_CONDS.get("strEq2"));
		LogicConditionPair<SimpleObject> lcp3 = LogicConditionPair.create(Logic.XNOR, SIMPLE_CONDS.get("strEq2"));
		List<LogicConditionPair<SimpleObject>> lcpl = new LinkedList<>();
		lcpl.add(lcp1);
		lcpl.add(lcp2);
		lcpl.add(lcp3);
		
		CompoundCondition<SimpleObject> ccArray = CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), lcp1, lcp2, lcp3);
		assertEquals(ccArray.getBaseCond(), SIMPLE_CONDS.get("intEq5"));
		assertIterableEquals(ccArray.getAdditionalConds(), lcpl);
		
		CompoundCondition<SimpleObject> ccList = CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), lcpl);
		assertEquals(ccList.getBaseCond(), SIMPLE_CONDS.get("intEq5"));
		assertIterableEquals(ccList.getAdditionalConds(), lcpl);
	}
	
	@Test
	void create_badInput() 
	{	
		LogicConditionPair<SimpleObject> lcp = LogicConditionPair.create(Logic.NAND, SIMPLE_CONDS.get("strEq2"));
		LogicConditionPair<SimpleObject> lcpNull = null;
		List<LogicConditionPair<SimpleObject>> nullList = null;
		List<LogicConditionPair<SimpleObject>> listWithNull = new LinkedList<>();
		listWithNull.add(null);
		listWithNull.add(lcp);
		
		assertNull(CompoundCondition.create(null, lcp));
		assertNull(CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), lcpNull));
		assertNull(CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), nullList));
		assertNull(CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), listWithNull));
	}
	
	@Test
	void enforceNonNull() 
	{
		CompoundCondition<SimpleObject> ccArray = CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), 
				LogicConditionPair.create(Logic.NAND, SIMPLE_CONDS.get("strEq2")));
		
		assertTrue(ccArray.setBaseCond(SIMPLE_CONDS.get("strEq2")));
		assertEquals(SIMPLE_CONDS.get("strEq2"), ccArray.getBaseCond());
		assertFalse(ccArray.setBaseCond(null));
		assertEquals(SIMPLE_CONDS.get("strEq2"), ccArray.getBaseCond());


		List<LogicConditionPair<SimpleObject>> empty = new LinkedList<>();
		assertTrue(ccArray.setAdditionalConds(empty));
		assertEquals(empty, ccArray.getAdditionalConds());
		assertFalse(ccArray.setAdditionalConds(null));
		assertEquals(empty, ccArray.getAdditionalConds());
		
		List<LogicConditionPair<SimpleObject>> listWithNull = new LinkedList<>();
		listWithNull.add(null);
		listWithNull.add(LogicConditionPair.create(Logic.NAND, SIMPLE_CONDS.get("strEq2")));
		assertFalse(ccArray.setAdditionalConds(listWithNull));
		assertEquals(empty, ccArray.getAdditionalConds());
	}
	
	@Test
	void evaluate_many() 
	{	
		LogicConditionPair<SimpleObject> lcp1 = LogicConditionPair.create(Logic.AND, SIMPLE_CONDS.get("strEq2"));
		LogicConditionPair<SimpleObject> lcp2 = LogicConditionPair.create(Logic.AND, 
				SimpleCondition.create(o -> o.list.size(), Comparison.LESS_THAN_OR_EQUAL, 3));
		LogicConditionPair<SimpleObject> lcp3 = LogicConditionPair.create(Logic.AND, 
				SimpleCondition.create(o -> o.map.size(), Comparison.EQUAL, 0));
		CompoundCondition<SimpleObject> cc = CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), lcp1, lcp2, lcp3);
		
		assertExpectedResults("compound condition many args", cc, OBJ_LIST, List.of(false, false, true, false));
	}
	
	@Test
	void evaluate_nested() 
	{	
		CompoundCondition<SimpleObject> ccLeftTrue25 = CompoundCondition.create(SIMPLE_CONDS.get("intEq5"), 
				LogicConditionPair.create(Logic.AND, SIMPLE_CONDS.get("strEq2")));
		
		CompoundCondition<SimpleObject> ccRightFalse = CompoundCondition.create(
				SimpleCondition.create(o -> o.list.size(), Negate.YES, Comparison.EQUAL, 0),
				LogicConditionPair.create(Logic.OR, 
						 SimpleCondition.create(o -> o.map.size(), Negate.YES, Comparison.EQUAL, 0)));
		
		CompoundCondition<SimpleObject> ccAnd = CompoundCondition.create(ccLeftTrue25,
				LogicConditionPair.create(Logic.AND, Negate.YES, ccRightFalse));
		assertExpectedResults("compound condition nested - and", ccAnd, OBJ_LIST, List.of(false, false, true, false));
		
		CompoundCondition<SimpleObject> ccNand = CompoundCondition.create(ccLeftTrue25,
				LogicConditionPair.create(Logic.NAND, Negate.NO, ccRightFalse));
		assertExpectedResults("compound condition nested - nand", ccNand, OBJ_LIST, List.of(true, true, true, true));

		CompoundCondition<SimpleObject> ccOr = CompoundCondition.create(ccLeftTrue25,
				LogicConditionPair.create(Logic.OR, ccRightFalse));
		assertExpectedResults("compound condition nested - or", ccOr, OBJ_LIST, List.of(false, false, true, false));
		
		CompoundCondition<SimpleObject> ccNor = CompoundCondition.create(ccLeftTrue25,
				LogicConditionPair.create(Logic.NOR, ccRightFalse));
		assertExpectedResults("compound condition nested - nor", ccNor, OBJ_LIST, List.of(true, true, false, true));
	}
	
	@Test
	void evaluate_and_nand() 
	{		
		CompoundCondition<SimpleObject> intEq5AndStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.AND, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5AndStrEq2", intEq5AndStrEq2, OBJ_LIST, List.of(false, false, true, false));
		CompoundCondition<SimpleObject> intEq5NandStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.NAND, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NandStrEq2", intEq5NandStrEq2, OBJ_LIST, List.of(true, true, false, true));

		CompoundCondition<SimpleObject> intEq5AndStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.AND, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5AndStrNeq2", intEq5AndStrNeq2, OBJ_LIST, List.of(true, false, false, false));
		CompoundCondition<SimpleObject> intEq5NandStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.NAND, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NandStrNeq2", intEq5NandStrNeq2, OBJ_LIST, List.of(false, true, true, true));
		
		CompoundCondition<SimpleObject> strEq2AndIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), LogicConditionPair.create(
						Logic.AND, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2AndIntNeq5", strEq2AndIntNeq5, OBJ_LIST, List.of(false, false, false, true));
		CompoundCondition<SimpleObject> strEq2NandIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), LogicConditionPair.create(
						Logic.NAND, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2NandIntNeq5", strEq2NandIntNeq5, OBJ_LIST, List.of(true, true, true, false));
	}
	
	@Test
	void evaluate_or_nor() 
	{
		CompoundCondition<SimpleObject> intEq5OrStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.OR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5OrStrEq2", intEq5OrStrEq2, OBJ_LIST, List.of(true, false, true, true));
		CompoundCondition<SimpleObject> intEq5NorStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.NOR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NorStrEq2", intEq5NorStrEq2, OBJ_LIST, List.of(false, true, false, false));

		CompoundCondition<SimpleObject> intEq5OrStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.OR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5OrStrNeq2", intEq5OrStrNeq2, OBJ_LIST, List.of(true, true, true, false));
		CompoundCondition<SimpleObject> intEq5NorStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.NOR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NorStrNeq2", intEq5NorStrNeq2, OBJ_LIST, List.of(false, false, false, true));
		
		CompoundCondition<SimpleObject> strEq2OrIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), LogicConditionPair.create(
						Logic.OR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2OrIntNeq5", strEq2OrIntNeq5, OBJ_LIST, List.of(false, true, true, true));
		CompoundCondition<SimpleObject> strEq2NorIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), LogicConditionPair.create(
						Logic.NOR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2NorIntNeq5", strEq2NorIntNeq5, OBJ_LIST, List.of(true, false, false, false));
	}
	
	@Test
	void evaluate_xor_xnor() 
	{
		CompoundCondition<SimpleObject> intEq5XorStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.XOR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XorStrEq2", intEq5XorStrEq2, OBJ_LIST, List.of(true, false, false, true));
		CompoundCondition<SimpleObject> intEq5XnorStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.XNOR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XnorStrEq2", intEq5XnorStrEq2, OBJ_LIST, List.of(false, true, true, false));

		CompoundCondition<SimpleObject> intEq5XorStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.XOR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XorStrNeq2", intEq5XorStrNeq2, OBJ_LIST, List.of(false, true, true, false));
		CompoundCondition<SimpleObject> intEq5XnorStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), LogicConditionPair.create(
						Logic.XNOR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XnorStrNeq2", intEq5XnorStrNeq2, OBJ_LIST, List.of(true, false, false, true));
		
		CompoundCondition<SimpleObject> strEq2XorIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), LogicConditionPair.create(
						Logic.XOR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2XorIntNeq5", strEq2XorIntNeq5, OBJ_LIST, List.of(false, true, true, false));
		CompoundCondition<SimpleObject> strEq2XnorIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), LogicConditionPair.create(
						Logic.XNOR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2XnorIntNeq5", strEq2XnorIntNeq5, OBJ_LIST, List.of(true, false, false, true));
	}
}
