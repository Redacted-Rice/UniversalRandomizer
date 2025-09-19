package redactedrice.universalrandomizer.functional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redactedrice.modularparser.comment.DefaultMutliLineCommentLineModifier;
import redactedrice.modularparser.comment.DefaultSingleLineCommentLineModifier;
import redactedrice.modularparser.core.LogSupporter.LogLevel;
import redactedrice.modularparser.core.ModularParser;
import redactedrice.modularparser.lineformer.DefaultGroupingLineModifier;
import redactedrice.modularparser.lineformer.DefaultLineFormerSupporter;
import redactedrice.modularparser.lineformer.Grouper;
import redactedrice.modularparser.lineparser.DefaultLineParserSupporter;
import redactedrice.modularparser.literal.DefaultBoolLiteralParser;
import redactedrice.modularparser.literal.DefaultCharLiteralParser;
import redactedrice.modularparser.literal.DefaultLiteralSupporter;
import redactedrice.modularparser.literal.DefaultNumberLiteralParser;
import redactedrice.modularparser.log.DefaultCacheLogHandler;
import redactedrice.modularparser.log.DefaultLogSupporter;
import redactedrice.modularparser.reserved.DefaultReservedWordSupporter;
import redactedrice.modularparser.scope.DefaultScopeSupporter;
import redactedrice.modularparser.scope.DefaultScopedVarConstParser;
import redactedrice.universalrandomizer.parser.GetterParser;
import redactedrice.universalrandomizer.parser.PoolParser;
import redactedrice.universalrandomizer.parser.RandomizeParser;
import redactedrice.universalrandomizer.parser.SelectParser;
import redactedrice.universalrandomizer.parser.SetterParser;
import redactedrice.universalrandomizer.pool.ReusePool;
import redactedrice.universalrandomizer.testsupport.SimpleObject;
import redactedrice.universalrandomizer.testsupport.SimpleObjectLiteralParser;
import redactedrice.universalrandomizer.testsupport.SimpleObjectUtils;

class BasicParsingTests {
    ModularParser parser;
    DefaultLineFormerSupporter reader;
    DefaultScopedVarConstParser varParser;
    DefaultScopedVarConstParser constParser;
    DefaultCacheLogHandler logger;
    Random rand;

    @BeforeEach
    void setup() {
        rand = mock(Random.class);
        parser = new ModularParser();
        reader = new DefaultLineFormerSupporter();
        parser.addModule(reader);
        parser.addModule(new DefaultLineParserSupporter());
        parser.addModule(new DefaultLiteralSupporter());
        parser.addModule(new DefaultReservedWordSupporter());
        parser.addModule(new DefaultLogSupporter());

        logger = new DefaultCacheLogHandler();
        logger.enableAll(false);
        logger.enable(LogLevel.ERROR, true);
        logger.enable(LogLevel.ABORT, true);
        parser.addModule(logger);

        parser.addModule(new DefaultSingleLineCommentLineModifier("DoubleSlashComments", "//"));
        parser.addModule(
                new DefaultMutliLineCommentLineModifier("MutlilineSlashStarComments", "/*", "*/"));
        DefaultScopeSupporter scope = new DefaultScopeSupporter(true);
        scope.pushScope("global");
        scope.pushScope("file");
        parser.addModule(scope);

        Grouper parenGrouper = new DefaultGroupingLineModifier("BasicParenthesisModule", "(", ")",
                false);
        // Don't set as static default to prevent interfering with other tests
        parser.addModule(parenGrouper);

        parser.addModule(new DefaultNumberLiteralParser());
        parser.addModule(new DefaultCharLiteralParser());
        parser.addModule(new DefaultBoolLiteralParser());
        parser.addModule(new SimpleObjectLiteralParser(parenGrouper));

        parser.addModule(new GetterParser(parenGrouper));
        parser.addModule(new PoolParser(parenGrouper));
        parser.addModule(new RandomizeParser(rand, parenGrouper));
        parser.addModule(new SelectParser(parenGrouper));
        parser.addModule(new SetterParser(parenGrouper));

        varParser = new DefaultScopedVarConstParser("BasicVarHandler", true, "var");
        parser.addModule(varParser);
        constParser = new DefaultScopedVarConstParser("BasicConstHandler", false, "const");
        parser.addModule(constParser);
    }

    @SuppressWarnings("unchecked")
    @Test
    void basicVarManipulationTests() {

        parser.configureModules();

        List<SimpleObject> soList = SimpleObjectUtils.soList(3);
        varParser.setVariable("global", "soList", soList);

        // Returns so that all have changed
        when(rand.nextInt(anyInt())).thenReturn(2, 0, 1);
        List<Integer> expectedInts = List.of(soList.get(2).intField, soList.get(0).intField,
                soList.get(1).intField);

        String script = """
                // For this case soList is injected before hand representing a list of object
                // passed to be randomized by the application
                var intStream = Select(from soList, field \"intField\")
                var intPool = Pool(from intStream, onuse "keep", duplicates "keep")
                var setInt = Setter(field "intField")
                Randomize(field setInt, of soList, using intPool)
                """;

        // Run parser
        reader.setReader(new BufferedReader(new StringReader(script)));
        boolean result = parser.parse();
        List<String> logs = logger.getLogs();
        assertTrue(logs.isEmpty());
        assertTrue(result);

        assertEquals(ReusePool.class, varParser.getVariableValue("intPool").getValue().getClass());
        assertEquals(3,
                ((ReusePool<Object>) varParser.getVariableValue("intPool").getValue()).size());
        // can't really verify the setter either - get class returns lambda
        assertEquals(3, soList.size());
        List<Integer> foundInts = soList.stream().map(SimpleObject::getIntField).toList();
        assertIterableEquals(expectedInts, foundInts);
    }
}
