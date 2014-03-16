package KDTree;

/**
 * TUPLE CLASS
 * @param <X>
 * @param <Y>
 */
public class Tuple<X,Y extends Comparable<Y>> implements Comparable<Tuple<X,Y>> { 
	  X x; 
	  Y y; 
	  public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  }
	  
	@Override
	public int compareTo(Tuple<X,Y> o) {
		return this.y.compareTo((Y) o.y);
	}
} 