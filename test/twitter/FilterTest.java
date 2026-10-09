/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * testing strategy
     * 
     * writtenBy(tweets, username)
     *  - no. of tweets: 0, 1, > 1
     *  - no. of tweets written by username: 0, 1, > 1
     *  - username case: same as author, not same as author
     * 
     * inTimespan(tweets, timespan)
     *  - no. of tweets: 0, 1, > 1
     *  - no. of tweets in timespan: 0, 1, > 1
     *  - tweets at start or end of timespan: true, false
     * 
     * containing(tweets, words)
     *  - no. of tweets: 0, 1, > 1
     *  - no. of tweets containing words: 0, 1, > 1
     *  - word letter-case: same as in tweet, not same as in tweet
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "someguy", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "wait wait i need more time also ask @randomguy if they want to join @someguy", d2);
    private static final Tweet tweet4 = new Tweet(4, "randomguy", "yes i would, thanks @SomeGuy, my email is random123@gmail.com", d3);
    private static final Tweet tweet5 = new Tweet(5, "anothergal", "@alyssa of course. in fact, it is unreasonable not to talk about rivest", d1);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    
    //---------------------------------//
    //-----------writtenBy()-----------//
    //---------------------------------//
    
    // tweets 0, result 0, case unimportant
    @Test
    public void testWrittenBy00() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "alyssa");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    // tweets 1, result 0, case unimportant
    @Test
    public void testWrittenBy10() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet2), "alyssa");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    // tweets 1, result 1, case same
    @Test
    public void testWrittenBy11S() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // tweets 1, result 1, case not same
    @Test
    public void testWrittenBy11N() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "ALYSSA");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // tweets > 1, result 0, case unimportant
    @Test
    public void testWrittenByM0() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet2, tweet4), "alyssa");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    // tweets > 1, result 1, case same
    @Test
    public void testWrittenByM1S() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // tweets > 1, result 1, case not same
    @Test
    public void testWrittenByM1N() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "ALYSSA");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // tweets > 1, result > 1, case same
    @Test
    public void testWrittenByMMS() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet3), "alyssa");
        
        assertEquals("expected two tweets", 2, writtenBy.size());
        assertEquals("expected two tweets in order", Arrays.asList(tweet1, tweet3),
        		writtenBy);
    }
    
    // tweets > 1, result > 1, case not same
    @Test
    public void testWrittenByMMN() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet3, tweet4), "ALYSSA");
        
        assertEquals("expected singleton list", 2, writtenBy.size());
        assertEquals("expected two tweets in order", Arrays.asList(tweet1, tweet3),
        		writtenBy);
    }
    
    
    //---------------------------------//
    //----------inTimespan()-----------//
    //---------------------------------//
    
    // tweets 0, result 0, position insignificant
    @Test
    public void testInTimespan00() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        
        assertEquals("expected empty set", 0, inTimespan.size());
    }
    
    // tweets 1, result 0, position insignificant
    @Test
    public void testInTimespan1M() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertEquals("expected empty set", 0, inTimespan.size());
    }
    
    // tweets 1, result 1, at start/end
    @Test
    public void testInTimespan11S() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton set", 1, inTimespan.size());
        assertTrue("expected different tweet", inTimespan.contains(tweet1));
    }
    
    // tweets 1, result 1, not at start/end
    @Test
    public void testInTimespan11N() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton set", 1, inTimespan.size());
        assertTrue("expected different tweet", inTimespan.contains(tweet1));
    }
    
    // tweets > 1, result 0, position insignificant
    @Test
    public void testInTimespanM0() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected empty set", 0, inTimespan.size());
    }
    
    // tweets > 1, result 1, at start/end
    @Test
    public void testInTimespanM1S() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton set", 1, inTimespan.size());
        assertTrue("expected different tweet", inTimespan.contains(tweet1));
    }
    
    // tweets > 1, result 1, not at start/end
    @Test
    public void testInTimespanM1N() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected singleton set", 1, inTimespan.size());
        assertTrue("expected different tweet", inTimespan.contains(tweet1));
    }
    
    // tweets > 1, result > 1, at start/end
    @Test
    public void testInTimespanMMS() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected two tweets", 2, inTimespan.size());
        assertEquals("expected two tweets in order", Arrays.asList(tweet1, tweet2),
        		inTimespan);
    }
    
    // tweets > 1, result > 1, not at start/end
    @Test
    public void testInTimespanMMN() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet4), new Timespan(testStart, testEnd));
        
        assertEquals("expected two tweets", 2, inTimespan.size());
        assertEquals("expected two tweets in order", Arrays.asList(tweet1, tweet2),
        		inTimespan);
    }
    
    
    //---------------------------------//
    //----------containing()-----------//
    //---------------------------------//
    
    // tweets 0, result 0, case insignificant
    @Test
    public void testContaining00() {
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList("talk"));
        
        assertEquals("expected empty set", 0, containing.size());
    }
    
    // tweets 1, result 0, case insignificant
    @Test
    public void testContaining10() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("khadija"));
        
        assertEquals("expected empty set", 0, containing.size());
    }
    
    // tweets 1, result 1, same case
    @Test
    public void testContaining11S() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk"));
        
        assertEquals("expected singleton set", 1, containing.size());
        assertTrue("expected different tweet", containing.contains(tweet1));
    }
    
    // tweets 1, result 1, not same case
    @Test
    public void testContaining11N() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("TALK"));
        
        assertEquals("expected singleton set", 1, containing.size());
        assertTrue("expected different tweet", containing.contains(tweet1));
    }
    
    // tweets > 1, result 0, case insignificant
    @Test
    public void testContainingM0() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("khadija"));
        
        assertEquals("expected empty set", 0, containing.size());
    }
    
    // tweets > 1, result 1, same case
    @Test
    public void testContainingM1S() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("reasonable"));
        
        assertEquals("expected singleton set", 1, containing.size());
        assertTrue("expected different tweet", containing.contains(tweet1));
    }
    
    // tweets > 1, result 1, not same case
    @Test
    public void testContainingM1N() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("REASONABLE"));
        
        assertEquals("expected singleton set", 1, containing.size());
        assertTrue("expected different tweet", containing.contains(tweet1));
    }
    
    // tweets > 1, result > 1, same case
    @Test
    public void testContainingMMS() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertEquals("expected two tweets", 2, containing.size());
        assertEquals("expected two tweets in order", Arrays.asList(tweet1, tweet2),
        		containing);
    }
    
    // tweets > 1, result > 1, not same case
    @Test
    public void testContainingMMN() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("TALK"));
        
        assertEquals("expected two tweets", 2, containing.size());
        assertEquals("expected two tweets in order", Arrays.asList(tweet1, tweet2),
        		containing);
    }
    

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
