package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list deque tests. */
public class LinkedListDequeTest {

    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */



    /** You MUST use the variable below for all of your tests. If you test
     * using a local variable, and not this static variable below, the
     * autograder will not grade that test. If you would like to test
     * LinkedListDeques with types other than Integer (and you should),
     * you can define a new local variable. However, the autograder will
     * not grade that test. */

    public static Deque<Integer> lld = new LinkedListDeque<Integer>();

    @Test
    /** Adds a few things to the list, checks that isEmpty() is correct.
     * This is one simple test to remind you how junit tests work. You
     * should write more tests of your own.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

		assertTrue("A newly initialized LLDeque should be empty", lld.isEmpty());
		lld.addFirst(0);

        assertFalse("lld should now contain 1 item", lld.isEmpty());

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();

    }

    @Test
    public void add() {

        for(int i = 0; i < 20; i++) {
            lld.addFirst(i);
            lld.addLast(i);
        }
        assertEquals(40,lld.size());
        assertEquals(19.0,(double) lld.get(0),0.0);

    }

    

    @Test
    public void remove() {
        lld = new ArrayDeque<Integer>();

        for(int i = 0; i < 20; i++) {
            lld.addFirst(i);
            lld.addLast(i);
        }
        assertEquals(40,lld.size());
        assertEquals(19.0,(double) lld.get(0),0.0);

        for(int i = 0; i < 20; i++) {
            assertEquals(lld.removeFirst(),lld.removeLast(), 0.0);
        }
        assertEquals(0,lld.size());
    }


    @Test
    public void get() {
        lld = new LinkedListDeque<Integer>();

        for(int i = 0; i < 20; i++) {
            lld.addLast(i);
        }

        for(int i = 0; i < 20; i++) {
            assertEquals(i,lld.get(i), 0.0);
        }
        assertEquals(20,lld.size());
    }

    @Test
    public void removeFromnull() {
        lld = new LinkedListDeque<Integer>();


        assertEquals(null,lld.removeFirst());
        assertEquals(null,lld.removeLast());
        assertEquals(0,lld.size());
    }

    @Test
    public void RandomizedTest() {
        lld = new LinkedListDeque<Integer>();
        lld.addLast(7);
        lld.addLast(6);
        lld.addLast(5);
        lld.addFirst(4);
        lld.addLast(1);
        lld.addFirst(3);



        assertEquals(3,lld.removeFirst(),0.0);
        assertEquals(1,lld.removeLast(),0.0);
        assertEquals(4,lld.size(),0.0);
    }


}
