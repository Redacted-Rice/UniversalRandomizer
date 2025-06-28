package redactedrice.universalrandomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import support.SimpleObject;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class SingleRandomizerTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	@Test
	void create() 
	{
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	Setter<SimpleObject, Integer> setter = (o, v) -> { o.intField = v; return true; };
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	
    	//create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
    	Randomizer<SimpleObject, Integer> rr = SingleRandomizer.create(ms, count2Getter);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	//create(MultiSetter<T2, P2> setter, int count, EnforceParams<T2> enforce)
    	rr = SingleRandomizer.create(ms, 1);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());

    	// create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
    	SimpleObject test = new SimpleObject("test", 0);
    	rr = SingleRandomizer.create(setter);
    	assertTrue(setter.setReturn(test, 1)); 	// Setter gets wrapped 
    	assertEquals(1, test.intField); 		// Setter gets wrapped 
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SingleRandomizer.create(ms));
	}
	
	@Test
	void create_badInput() 
	{
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	MultiSetter<SimpleObject, Integer> msNull = null;
    	Setter<SimpleObject, Integer> setterNull = null;
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	Getter<SimpleObject, Integer> countGetterNull = null;
    	
    	assertNull(SingleRandomizer.create(msNull, count2Getter));
    	assertNull(SingleRandomizer.create(ms, countGetterNull));
    	assertNull(SingleRandomizer.create(msNull, 1));
    	assertNull(SingleRandomizer.create(msNull));
    	assertNull(SingleRandomizer.create(setterNull));
	}
	
	@Test
	void assignAndCheckEnforce() 
	{
		// TODO
	}
}
