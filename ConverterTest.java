package ru.gnkoshelev.kontur.intern;

import org.junit.Test;
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
import static junit.framework.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class ConverterTest
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    @Test
    public testCheckUnits()
    {
        Converter conv = new Converter();
	assertFalse(conv.checkUnits(" ", " "));
	assertFalse(conv.checkUnits("", ""));
	assertFalse(conv.checkUnits(" ", ""));
	assertFalse(conv.checkUnits("Í„", " "));
	assertFalse(conv.checkUnits("Í„", ""));
	assertFalse(conv.checkUnits("Í„ ", " "));
	assertFalse(conv.checkUnits(" Í„ ", "Í„"));
	assertFalse(conv.checkUnits("Í„ ", ""));
	assertFalse(conv.checkUnits("", "Í„"));
	assertFalse(conv.checkUnits(" Í„ ", null));
	assertFalse(conv.checkUnits(null, ""));
	assertFalse(conv.checkUnits(null, null));
	assertTrue(conv.checkUnits("ÍÏ", "Ò"));
	assertTrue(conv.checkUnits("ÒÏ", "Ï"));
    }

    @Test
    public testCheckExpresson()
    {
        Converter conv = new Converter();
	assertTrue(conv.checkExpression("ÍÏ", "Ï"));
	assertTrue(conv.checkExpression("Ò", "˜"));
	assertTrue(conv.checkExpression("ÒÏ", "ÍÏ"));
	assertTrue(conv.checkExpression("˜‡Ò", "ÏËÌ"));
	assertFalse(conv.checkExpression("Ï", "Ò"));
    }
    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
