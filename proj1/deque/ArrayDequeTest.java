package deque;

import org.junit.Test;

import static org.junit.Assert.*;

/* Performs some basic array deque tests. */
public class ArrayDequeTest {

    /** You MUST use the variable below for all of your tests. If you test
     * using a local variable, and not this static variable below, the
     * autograder will not grade that test. If you would like to test
     * ArrayDeques with types other than Integer (and you should),
     * you can define a new local variable. However, the autograder will
     * not grade that test. */

    public static Deque<Integer> ad = new ArrayDeque<Integer>();

    @Test
    /** Adds a few things to the list, checks that isEmpty() is correct.
     * This is one simple test to remind you how junit tests work. You
     * should write more tests of your own.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        assertTrue("A newly initialized Array Deque should be empty", ad.isEmpty());
        ad.addFirst(0);

        assertFalse("ad should now contain 1 item", ad.isEmpty());

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();

    }

    @Test
    public void add() {
        for(int i = 0; i < 20; i++) {
            ad.addFirst(i);
            ad.addLast(i);
        }
        assertEquals(40,ad.size());
        assertEquals(19.0,(double) ad.get(0),0.0);

    }

    @Test
    public void remove() {
        ad = new ArrayDeque<Integer>();

        for(int i = 0; i < 20; i++) {
            ad.addFirst(i);
            ad.addLast(i);
        }
        assertEquals(40,ad.size());
        assertEquals(19.0,(double) ad.get(0),0.0);

        for(int i = 0; i < 20; i++) {
            assertEquals(ad.removeFirst(),ad.removeLast(), 0.0);
        }
        assertEquals(0,ad.size());
    }


    @Test
    public void get() {
        ad = new ArrayDeque<Integer>();

        for(int i = 0; i < 20; i++) {
            ad.addLast(i);
        }

        for(int i = 0; i < 20; i++) {
            assertEquals(i,ad.get(i), 0.0);
        }
        assertEquals(20,ad.size());
    }

    @Test
    public void removeFromnull() {
        ad = new ArrayDeque<Integer>();


        assertEquals(null,ad.removeFirst());
        assertEquals(null,ad.removeLast());
        assertEquals(0,ad.size());
    }

    @Test
    public void RandomizedTest() {
        ad = new ArrayDeque<Integer>();
        ad.addLast(7);
        ad.addLast(6);
        ad.addLast(5);
        ad.addFirst(4);
        ad.addLast(1);
        ad.addFirst(3);



        assertEquals(3,ad.removeFirst(),0.0);
        assertEquals(1,ad.removeLast(),0.0);
        assertEquals(4,ad.size());
    }


}
