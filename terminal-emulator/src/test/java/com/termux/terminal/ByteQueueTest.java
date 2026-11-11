package com.termux.terminal;

import junit.framework.TestCase;

public class ByteQueueTest extends TestCase {

    public void testNonblockingWriteIsAtomicAndValidatesItsRange() {
        ByteQueue queue = new ByteQueue(4);
        assertTrue(queue.write(new byte[]{1, 2, 3}, 0, 3, false));
        assertFalse(queue.write(new byte[]{4, 5}, 0, 2, false));
        byte[] buffer = new byte[4];
        assertEquals(3, queue.read(buffer, false));
        assertArrayEquals(new byte[]{1, 2, 3, 0}, buffer);
        try {
            queue.write(buffer, -1, 1, false);
            fail("Accepted a negative offset");
        } catch (IllegalArgumentException expected) { }
        try {
            queue.write(buffer, 1, Integer.MAX_VALUE, false);
            fail("Accepted an overflowing range");
        } catch (IllegalArgumentException expected) { }
    }

	private static void assertArrayEquals(byte[] expected, byte[] actual) {
		if (expected.length != actual.length) {
			fail("Difference array length");
		}
		for (int i = 0; i < expected.length; i++) {
			if (expected[i] != actual[i]) {
				fail("Inequals at index=" + i + ", expected=" + (int) expected[i] + ", actual=" + (int) actual[i]);
			}
		}
	}

	public void testCompleteWrites() throws Exception {
		ByteQueue q = new ByteQueue(10);
		assertTrue(q.write(new byte[]{1, 2, 3}, 0, 3));

		byte[] arr = new byte[10];
		assertEquals(3, q.read(arr, true));
		assertArrayEquals(new byte[]{1, 2, 3}, new byte[]{arr[0], arr[1], arr[2]});

		assertTrue(q.write(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 0, 10));
		assertEquals(10, q.read(arr, true));
		assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, arr);
	}

	public void testQueueWraparound() throws Exception {
		ByteQueue q = new ByteQueue(10);

		byte[] origArray = new byte[]{1, 2, 3, 4, 5, 6};
		byte[] readArray = new byte[origArray.length];
		for (int i = 0; i < 20; i++) {
			q.write(origArray, 0, origArray.length);
			assertEquals(origArray.length, q.read(readArray, true));
			assertArrayEquals(origArray, readArray);
		}
	}

	public void testWriteNotesClosing() throws Exception {
		ByteQueue q = new ByteQueue(10);
		q.close();
		assertFalse(q.write(new byte[]{1, 2, 3}, 0, 3));
	}

	public void testReadNonBlocking() throws Exception {
		ByteQueue q = new ByteQueue(10);
		assertEquals(0, q.read(new byte[128], false));
	}

}
