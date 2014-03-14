package KDTree;

/**
 * TUPLE CLASS
 * @param <X>
 * @param <Y>
 */
public class Tuple<X,Y extends Comparable<Y>> implements Comparable<Tuple> { 
	  X x; 
	  Y y; 
	  public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  }
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Tuple o) {
		// TODO Auto-generated method stub
		return this.y.compareTo((Y) o.y);
	}
} 