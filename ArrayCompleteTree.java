package edu.uwm.cs351;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author * CHRISTIAN OROPEZA CS-351 ...RECIEVED HELP FROM BOOK, CS LIBRARY TUTORS, ONLINE CS TUTOR, 
	AND ADVICE FROM FRIENDS ON HOW TO APPROACH FIXING PERSISTENT BUGS.
 * COLLABORATORS: JOSHUA KNIGHT, JULLIAN MURENO, BIJAY PANTA, JIAHUI YANG , MIGUEL GARCIA, MARAWAN SALAMA, ESTELLE BRADY (WHILE IN TUTORING LIBRARY SECTION) BUT NO CODE WAS SHARED.
 * Online Sources: 
 */

/**
 * A dynamic-array implementation of the CompleteTree interface.
 */
public class ArrayCompleteTree<E> implements CompleteTree<E> 
{
	private static final int INITIAL_CAPACITY = 10;
	
	private E[] data;
	private int manyItems;
	
	
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}
	
	private boolean wellFormed() 
	{
		if (data == null) return report("array is null");
		
		if (manyItems < 0) return report("manyItems is negative");
		
		if (manyItems > data.length) return report("manyItems is too many: " + manyItems + " > capacity = " + data.length);
		
		return true;
	}
	
	/**
	 * Create a new array of the element type.
	 * @param cap number of elements to create array
	 * @return array of the required size (that pretends to be
	 * of the required type -- do not let this array escape the scope
	 * of this class).
	 */
	@SuppressWarnings("unchecked")
	private E[] makeArray(int cap) 
	{
		return (E[]) new Object[cap];
	}
	
	/**
	 * Ensure that the underlying array has at least the given 
	 * capacity.  If it's necessary to allocate an array,
	 * we allocate one that is at least twice as long.
	 * @param minimumCapacity minimum number of elements needed
	 */
	private void ensureCapacity(int minimumCapacity) 
	{
		if (data.length >= minimumCapacity) return;
		int newCap = data.length*2;
		if (newCap < minimumCapacity) newCap = minimumCapacity;
		E[] newData = makeArray(newCap);
		
		for (int i=0; i < manyItems; ++i) 
		{
			newData[i] = data[i];
		}
		
		data = newData;
	}

	/**
	 * Create an empty complete tree.
	 */
	public ArrayCompleteTree() 
	{
		data = makeArray(INITIAL_CAPACITY);
		
		assert wellFormed(): "invariant broken by constructor";
	}
	
	// TODO
	// We don't use identity on locations; you can create a new Location
	// whenever you need to return a location.

	private /* non-static */ class Location implements CompleteTree.Location<E> 
	{
		private final int index; // 1-based index into tree

		Location(int index) 
		{
			if (index < 1) throw new IllegalArgumentException("cannot use a negative index");
			
			this.index = index;
		}
		
		// TODO: implement required Location methods
		// You will need to figure out the (simple) pattern
		// connecting parents and children location numbers.

		@Override // implementation
		public String toString() 
		{
			return "Location(" + index + ")";
		}
		
		@Override // implementation
		public int hashCode() 
		{
			return index;
		}

		@Override // implementation
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof ArrayCompleteTree<?>.Location)) return false;
			
			ArrayCompleteTree<?>.Location loc = (ArrayCompleteTree<?>.Location)obj;
			
			return loc.index == index;
		}

		@Override	//	implementation
		public E get() 
		{	
			assert wellFormed(): "invariant broken at the start get";

			E getLoc = data[index - 1];
			
			assert wellFormed(): "invariant broken at the end of get";
			
			return getLoc;
		}
		
		

		@Override	//	implementation
		public void set(E val) 
		{
			assert wellFormed(): "invariant broken at the start of set";	
			
			data[index - 1] = val;
			
			assert wellFormed(): "invariant broken at the end of set";
		}

		@Override	//	implementation
		public ArrayCompleteTree<E>.Location parent() 
		{
			assert wellFormed(): "invariant broken at the start of parent";
			
			if(index <= 1)	return null;
			
			ArrayCompleteTree<E>.Location parentLoc = new Location((index / 2));
			
			assert wellFormed(): "invariant broken at the end of parent";
			
			return parentLoc;		
		}

		@Override	//	implementation
		public ArrayCompleteTree<E>.Location child(boolean right) 
		{
			assert wellFormed(): "invariant broken at the start of child";
			
			int childLoc = right ? index * 2 + 1 : index * 2;
			
			if(childLoc > manyItems)	return null;

			assert wellFormed(): "invariant broken at the end of child";

			return new Location(childLoc);	
		}
	
	}

	@Override	//	required
	public int size() 
	{
		assert wellFormed(): "invariant broken at the start of size";
		
		return manyItems;
	}

	@Override	//	required
	public ArrayCompleteTree<E>.Location root()
	{
		assert wellFormed(): "invariant broken at the start of root";
		
		if(size() == 0)	return null;
		
		assert wellFormed(): "invariant broken at the end of root";

		return new Location(1);
	}

	@Override	//	required
	public ArrayCompleteTree<E>.Location last() 
	{
		assert wellFormed(): "invariant broken at the start of last";

		if(size() == 0)	return null;
			
		ArrayCompleteTree<E>.Location lastLoc = new Location(manyItems);
		
		assert wellFormed(): "invariant broken at the end of last";
		
		return lastLoc;
	}

	@Override	//	required
	public ArrayCompleteTree<E>.Location add(E value) 
	{
		assert wellFormed(): "invariant broken at the start of add";
			
		ensureCapacity(manyItems + 1); 
		
		data[manyItems++] = value;
		
		assert wellFormed(): "invariant broken at the end of add";
		
		return new Location(manyItems);
	}

	@Override	//	required
	public E remove() 
	{
		assert wellFormed(): "invariant broken at the start of remove";

		if(size() == 0)	throw new NoSuchElementException("size() = 0 nothing to remove");
		
		--manyItems;
		
		assert wellFormed(): "invariant broken at the end of remove";
		
		return data[manyItems];
	}
	
}
