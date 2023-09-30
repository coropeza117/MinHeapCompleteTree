package edu.uwm.cs351;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author * CHRISTIAN OROPEZA CS-351 ...RECIEVED HELP FROM BOOK, CS LIBRARY TUTORS, ONLINE CS TUTOR, AND ADVICE FROM FRIENDS ON HOW TO APPROACH FIXING PERSISTENT BUGS.
 * COLLABORATORS: JOSHUA KNIGHT, JULLIAN MURENO, BIJAY PANTA, JIAHUI YANG , MIGUEL GARCIA, MARAWAN SALAMA, ESTELLE BRADY (WHILE IN TUTORING LIBRARY SECTION) BUT NO CODE WAS SHARED.
 * Online Sources: https://www.geeksforgeeks.org/binary-heap/
 * 				   https://linuxhint.com/use-swap-method-in-java/
 */

/**
 * An implementation of a min-heap that is agnostic
 * on the tree implementation technique, 
 * and use any comparator of the values.
 * @param E type of elements on the heap.
 */
public class MinHeap<E> 
{
	private CompleteTree<E> tree;
	private Comparator<E> comparator;

	/// Invariant checks:
	
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}
	
	/**
	 * Check the subtree that all elements are within the given bound,
	 * and that the elements form a min-heap
	 * @param l subtree to check, may be null
	 * @param bound inclusive lower bound
	 * @return if a problem is found, in which case it has been reported
	 */
	private boolean checkSubtree(CompleteTree.Location<E> l, E bound) 
	{
		if (l == null) return true;
	    
	    E element = l.get();
	    
	    if (bound != null && comparator.compare(element, bound) < 0) return report("bound not null & element(subtree) less than bound");
	    
	    return checkSubtree(l.left(), element) && checkSubtree(l.right(), element);
	}
	
	/**
	 * check that fields aren't null
	 * and that the tree obeys the min-heap property.
	 * (No need to check that the tree is "complete";
	 * that's done by the CompleteTree implementation.)
	 * @return same as ArrayTree
	 */
	private boolean wellFormed() 
	{
		if (comparator == null) return report("comparator is Null !");
		
		if(tree == null)	return report("tree is null");
		
	    if (tree.size() < 0 )	return report("tree size is negative");

		return checkSubtree(tree.root(), null);
	}
	
	/**
	 * Create a min-heap with the given tree (which must not be null)
	 * and comparator (if null, using natural ordering)
	 * @param tree complete tree implementation, must be empty
	 * @param comparator ordering to use, many be null (natural ordering)
	 */
	@SuppressWarnings("unchecked")
	public MinHeap(CompleteTree<E> tree, Comparator<E> comparator) 
	{
		if (!tree.isEmpty()) throw new IllegalArgumentException("tree must be empty");
		
		if (comparator == null)	comparator = (o1, o2) -> ((Comparable<E>)o1).compareTo(o2);;
		
		this.tree = tree;
		
		this.comparator = comparator;
		
		assert wellFormed() : "invariant broken in constructor";
	}
	
	private MinHeap(boolean ignored) {} // do not change this constructor
	
	/**
	 * Return the number of elements in the heap.
	 */
	public int size() 
	{
		assert wellFormed() : "invariant broken in size";
		
		return tree.size();
	}
	
	/**
	 * Add a new value to the min heap.
	 * @param value to add, must be acceptable to the comparator (usually not null)
	 * 1. Add the new value to the end of the heap
	 * 2. Keep track of the location of the new value
	 * 3. Swap the new value with its parent until it reaches the correct position
	 */
	public void add(E value) 
	{
		assert wellFormed() : "invariant broken in add";
		
		tree.add(value);
		
		CompleteTree.Location<E> locTreelast = tree.last();
		
		while (locTreelast.parent() != null) 
		{
			E parent = locTreelast.parent().get();
			
			if (comparator.compare(value, parent) < 0) 
			{
				swap(locTreelast, locTreelast.parent());
				locTreelast = locTreelast.parent();
			} 
			
			else	break;	
		}
		
		assert wellFormed() : "invariant broken by add";
	}

	/**
	 * Swap the values at the given locations.
	 * @param locFirstvalue location for the first value
	 * @param locSecondvalue location for the second value
	 */
	private void swap(CompleteTree.Location<E> locFirstvalue, CompleteTree.Location<E> locSecondvalue) 
	{
		E temp = locFirstvalue.get();
		
		locFirstvalue.set(locSecondvalue.get());
		
		locSecondvalue.set(temp);
	}

	/**
	 * Return the minimum value in the min-heap.
	 * This is a constant-time operation.
	 * @return minimum element
	 * @exception NoSuchElementException if the heap is empty.
	 */
	public E min() 
	{
		assert wellFormed() : "invariant broken in min";
		
		if(tree.isEmpty() == true)	throw new NoSuchElementException("heap is empty !");
		
		return tree.root().get();
	}

	/**
	 * Remove and return the minimum value from this heap.
	 * @return the former minimum value.
	 * @exception NoSuchElementException if the heap is empty.
	 * 1. Get the minimum value (the root of the heap)
	 * 2. Remove the last element in the heap and put it at the root
	 * 3. Remove the last element
	 * 4. Swap the root with one of its children until it reaches the correct position
	 * 5. if (left == null)	break; due to : no more children
	 * 6. if (comparator.compare(locTreeroot.get(), left.get()) > 0) - only a left child
	 * 7. if (comparator.compare(locTreeroot.get(), left.get()) > 0 || ...
	 *    ...comparator.compare(locTreeroot.get(), right.get()) > 0) - both left and right children
	 * 8. if (comparator.compare(left.get(), right.get()) < 0) - choose the child with the smaller value
	 */
	public E removeMin() 
	{
		assert wellFormed() : "invariant broken in removeMin";

		if (tree.isEmpty() == true)	throw new NoSuchElementException("heap is empty");

		E result = null;

		if (tree.isEmpty() == false) 
		{
			result = tree.root().get();

			CompleteTree.Location<E> last = tree.last();
			
			tree.root().set(last.get());

			tree.remove();

			CompleteTree.Location<E> locTreeroot = tree.root();

			while (tree.isEmpty() == false) 
			{
				if (locTreeroot == null)	break;		

				CompleteTree.Location<E> left = locTreeroot.left();
				
				CompleteTree.Location<E> right = locTreeroot.right();

				if (left == null)	break; 

				else if (right == null) 
				{
					if (comparator.compare(locTreeroot.get(), left.get()) > 0) 
					{
						swap(locTreeroot, left);
						locTreeroot = left;
					} 

					else	break;
				} 

				else 
				{
					if (comparator.compare(locTreeroot.get(), left.get()) > 0 || comparator.compare(locTreeroot.get(), right.get()) > 0) 
					{
						if (comparator.compare(left.get(), right.get()) <= 0) 
						{
							swap(locTreeroot, left);
							locTreeroot = left;
						} 

						else 
						{
							swap(locTreeroot, right);
							locTreeroot = right;
						}
					} 

					else	break;
				}
			}
		}

		assert wellFormed() : "invariant broken by removeMin";

		return result;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static class Spy 
	{
		/**
		 * Return the sink for invariant error messages
		 * @return
		 */
		public Consumer<String> getReporter() 
		{
			return reporter;
		}
		
		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) 
		{
			reporter = r;
		}
		
		/**
		 * Create a min heap with the given data structure.
		 * This method does not check the invariant.
		 * nor does it use the regular constructor that checks the parameters.
		 * @param tree complete tree implementation
		 * @param comp comparator to use
		 * @return new main heap object.
		 */
		public <T> MinHeap<T> createMinHeap(CompleteTree<T> tree, Comparator<T> comp) 
		{
			MinHeap<T> result = new MinHeap<>(false);
			result.comparator = comp;
			result.tree = tree;
			return result;
		}
		
		/**
		 * Return the results of running the invariant checker
		 * on this object.  Errors will likely be reported
		 * to the error reporter.
		 * @param h min heap to check
		 * @return whether the invariant evaluates to true
		 */
		public boolean wellFormed(MinHeap<?> h) 
		{
			return h.wellFormed();
		}
	}
}
