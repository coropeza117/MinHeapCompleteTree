package edu.uwm.cs351;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

import edu.uwm.cs.util.Pair;
import edu.uwm.cs.util.PowersOfTwo;

/**
 * @author * CHRISTIAN OROPEZA CS-351 ...RECIEVED HELP FROM BOOK, CS LIBRARY TUTORS, ONLINE CS TUTOR, AND ADVICE FROM FRIENDS ON HOW TO APPROACH FIXING PERSISTENT BUGS.
 * COLLABORATORS: JOSHUA KNIGHT, JULLIAN MURENO, BIJAY PANTA, JIAHUI YANG , MIGUEL GARCIA, MARAWAN SALAMA, ESTELLE BRADY (WHILE IN TUTORING LIBRARY SECTION) BUT NO CODE WAS SHARED.
 * Online Sources: https://piazza.com/class/l5mh78qa9hm5g/post/838
 * 				   https://www.baeldung.com/java-number-of-digits-in-int
 * 				   https://www.geeksforgeeks.org/how-to-calculate-log-base-2-of-an-integer-in-java/cha
 * 				   https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
 * 				   https://stackoverflow.com/questions/3901253/bitshifting-in-java
 *                 
 */

public class TreeCompleteTree<E> implements CompleteTree<E> 
{
	private static class Node<T> implements Location<T> 
	{
		Node<T> parent, left, right;
		T data;
		
		Node(T data) 
		{
			this.data = data;
		}
		
		// The following are for the purposes of clients.
		// We don't bother using them.
		@Override // required
		public T get() 
		{
			return data;
		}
		
		@Override // required
		public void set(T val) 
		{
			data = val;
		}
		
		@Override // required
		public Location<T> parent() 
		{
			return parent;
		}
		
		@Override // required
		public Location<T> child(boolean right) 
		{
			return right? this.right : left;
		}
	}
	
	private Node<E> root;
	private int manyNodes;
	// NO MORE FIELDS!
	
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}
	
	private boolean checkSubtree(Node<E> r, Node<E> p, int size) 
	{
		if (r == null) 
		{
			if (size == 0) return true;
			return report("found null tree of supposed size " + size);
		}
		
		if (r.parent != p) return report("Found bad parent on node with " + r.data);
		if (size <= 0) return report("a non-empty subtree cannot have size = " + size);
		int power = PowersOfTwo.next(size/2);
		int prev = power/2;
		int left = (power+prev > size) ? (size - prev) : power-1;
		return checkSubtree(r.left, r, left) && checkSubtree(r.right, r, size - left - 1);
	}
	
	private boolean wellFormed() 
	{
		return checkSubtree(root, null, manyNodes);
	}

	/**
	 * Create an empty complete tree.
	 */
	public TreeCompleteTree() 
	{
		root = null;
		manyNodes = 0;
		assert wellFormed(): "invariant broken by constructor";
	}
	
	/**
	 * Locate a node in the tree using the algorithm
	 * explained in Activity 14.  It will start with the root and then
	 * go left or right at each step depending on what the algorithm says.
	 * It will return the "lag" (parent) pointer too, which will make it 
	 * easier to handle additions and removals.
	 * <p>
	 * If the node is not in the tree yet, the node may be null.
	 * If the node is at the root, the parent may be null.
	 * <p>
	 * This is useful for accessing the last element, including before we add 
	 * or remove it.
	 * @param n number of node to locate, one-based.
	 * It must be positive and must be no more than one more than 
	 * manyNodes.
	 * @return two nodes, either of which could be null,
	 * the first is the node itself, and the second is its parent
	 * You will need to use PowersOfTwo to find the power
	 * of two that represents the first bit in the number.
	 * This is the largest power of two which is *less*
	 * than the number.  You can compute this as the *next* power of two after half the number.
	 * @piazza questions 838 & 817
	 */
	private Pair<Node<E>, Node<E>> find(int n) 
	{
	    if(n <= 0 || n > manyNodes + 1) throw new IllegalArgumentException("bad index " + n);

	    Node<E> rootNode = root;
	    
	    Node<E> parent = null;
	    
	    //	calculates the next power of 2 that is greater than n, and then calculates the logarithm base 2 of that number.
	    int length =  (int) ( Math.log( PowersOfTwo.next(n) ) / Math.log(2) );

	    //	subtract 2 ... 1 to remove first bit, and 1 to account for an index of 0 since we start at 1
	    int i = length - 2;	
	    
	    //  since we are checking bits in reverse we stop once we reach index 0 not start at index 0 
	    while (i >= 0) 
	    {
	        parent = rootNode;
	        
	        //  bit shifting in java based on index we are at
	        int bitSet =  (n & (1 << i) );
	        
	        if(bitSet == 0)	rootNode = rootNode.left;      
	        
	        else	rootNode = rootNode.right;
	        
	        //  decrement index length as we loop so we eventually reach 0 / the end of the bit length 
	        --i;
	    }
	    
	    return new Pair<>(rootNode, parent);
	}

	@Override	//	required
	public int size() 
	{
		assert wellFormed(): "invariant broken at the start of size";
		
		return manyNodes;
	}

	@Override	//	required
	public Location<E> root() 
	{
		assert wellFormed(): "invariant broken at the start of root";
		
		if(size() == 0)	return null;
		
		assert wellFormed(): "invariant broken at the end of root";
		
		return root;
	}

	@Override	//	required
	public Location<E> last() 
	{
		assert wellFormed(): "invariant broken at the start of last";
		
		if(size() == 0)	return null;
		
		assert wellFormed(): "invariant broken at the end of last";
		
		return find(manyNodes).fst();
	}
	
	public static class Spy 
	{
		public static <E> Pair<Location<E>,Location<E>> find(CompleteTree<E> tree, int n) 
		{
			Pair<Node<E>, Node<E>> pair = ((TreeCompleteTree<E>)tree).find(n);
			return new Pair<>(pair.fst(), pair.snd());
		}
	}

	//	if value is null add the node with a null value no special case
	@Override	//	required
	public Location<E> add(E value) 
	{
		assert wellFormed(): "invariant broken at the start of add";

		Node<E> nodeValue =  new Node<E>(value);

		if(size() == 0)	root = nodeValue;

		else
		{
			Pair<Node<E>, Node<E>> lastPair =  find(manyNodes + 1);

			Node<E> lastParent = lastPair.snd();
			
			nodeValue.parent = lastParent;

			if(lastParent.left == null)	lastParent.left = nodeValue;

			else	lastParent.right = nodeValue;
		}

		++manyNodes;

		assert wellFormed(): "invariant broken at the end of add";

		return nodeValue;
	}

	@Override	//	required
	public E remove() 
	{
		assert wellFormed(): "invariant broken at the start of remove";

		if (size() == 0)	throw new NoSuchElementException();

		Node<E> last = (Node<E>) last();

		Node<E> lastParent = last.parent;

		if (lastParent == null)	root = null;

		else 
		{
			// Set the last node's parent equal to null
			last.parent = null;
			
			// Set the parent's left or right child equal to null, based on which child is the last node
			if(lastParent.left == last)	lastParent.left = null;

			else	lastParent.right = null;
		}

		--manyNodes;	

		assert wellFormed(): "invariant broken at the end of remove";

		return last.data;
	}
}
