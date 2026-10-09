/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;


import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * testing strategy
     * 
     * getTimespan(tweets)
     *  - no. of tweets: 1, > 1
     *  - order: by time, not in order
     *  - timestamps: all equal, not all equal
     *  - no. of tweets: 0 is not tested
     *  since there is no mention of it in specs
     * 
     * getMentionedUsers(tweets)
     *  - no. of tweets: 1, > 1
     *  - mentions in single tweet: 0, 1, > 1
     *  - same user mentioned: 1, > 1
     *  - position of mention: start, end, middle
     *  - @ after username chracter: true, false
     *  - punctuation right after mention: yes, no
     * 
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
    //----------getTimespan()----------//
    //---------------------------------//
    
    // covers: = 1, timestamps all equal, so in time order
    @Test
    public void testGetTimespanOneTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    
    // covers: > 1, timestamps all equal, so in time order
    @Test
    public void testGetTimespanTwoEqualTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet3));
        
        assertEquals("expected start", d2, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }   
    
    // covers: > 1, timestamps not equal, in time order
    @Test
    public void testGetTimespanTwoNotEqualTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    // covers: > 1, timestamps not all equal, not in time order
    @Test
    public void testGetTimespanThreeUnorderedTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet4, tweet5));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    
    //---------------------------------//
    //-------getMentionedUsers()-------//
    //---------------------------------//
    
    // covers: = 1, mentions = 0
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    // cover: 1 tweet, 1 mention, position: start, trailing punctuation: no, @ after char: false
    @Test
    public void testGetMentionedUsersOneMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));
        
        assertEquals("expected one user", new HashSet<>(Arrays.asList("alyssa")),
        		mentionedUsers);
    }
    
    // cover: 1 tweet, >1 mentions, positions: middle (@randomguy) and end (@someguy)
    @Test
    public void testGetMentionedUsersMultipleMentionsMiddleAndEnd() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        
        assertEquals("expected two users", new HashSet<>(Arrays.asList("randomguy", "someguy")),
        		mentionedUsers);
    }
    
    // cover: 1 tweet, trailing punctuation (@SomeGuy,), @ after username char (random123@gmail.com)
    @Test
    public void testGetMentionedUsersPunctuationAndEmailAndCaseSensitivity() {
        Set<String> mentionedUsers = toLower(Extract.getMentionedUsers(Arrays.asList(tweet4)));
        
        assertEquals("expected one user (someguy)", new HashSet<>(Arrays.asList("someguy")),
        		mentionedUsers);
    }
    
    // covers: >1 tweets, same user mentioned >1 times (@someguy in tweet3 and @SomeGuy in tweet4)
    @Test
    public void testGetMentionedUsersMultipleTweetsAndDuplicateMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet4));
        
        assertEquals("expected each user once", 2, mentionedUsers.size());
        assertEquals("expected two users (someguy and randomguy)", new HashSet<>(Arrays.asList("someguy", "randomguy")),
        		mentionedUsers);
        toLower(mentionedUsers);
    }
    
    // usernames are case-insensitive, so compare them in lowercase
    private static Set<String> toLower(Set<String> usernames) {
    	Set<String> result = new HashSet<>();
    	for (String username : usernames) {
    		result.add(username.toLowerCase());
    	}
    	return result;
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
