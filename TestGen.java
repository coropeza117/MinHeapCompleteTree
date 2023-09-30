
import edu.uwm.cs351.TreeCompleteTree;
import junit.framework.TestCase;
import edu.uwm.cs351.MinHeap;

public class TestGen extends TestCase {
	protected void assertException(Class<?> exc, Runnable r) {
		try {
			r.run();
			assertFalse("should have thrown an exception.",true);
		} catch (RuntimeException e) {
			if (exc == null) return;
			assertTrue("threw wrong exception type: " + e.getClass(),exc.isInstance(e));
		}
	}

	protected void assertEquals(int expected, Integer result) {
		super.assertEquals(Integer.valueOf(expected),result);
	}

	public void test() {
		MinHeap<String> h0 = new MinHeap<String>(new TreeCompleteTree<>(),String.CASE_INSENSITIVE_ORDER);
		h0.add(new String("COMPSCI")); // should terminate normally
		assertEquals(1,h0.size());
		h0.add(new String("CompSci")); // should terminate normally
		assertEquals(new String("COMPSCI"),h0.min());
		h0.add(new String("COMPSCI")); // should terminate normally
		h0.add(new String("COMPSCI")); // should terminate normally
		h0.add(new String("Greetings")); // should terminate normally
		assertEquals(new String("COMPSCI"),h0.min());
		assertEquals(new String("COMPSCI"),h0.removeMin());
		assertEquals(new String("CompSci"),h0.removeMin());
	}
}
