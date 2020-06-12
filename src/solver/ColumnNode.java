package solver;

public class ColumnNode extends DancingNode {

	  public int size;
	  public String name;

	  public ColumnNode(String n) {
	    super();
	    size = 0;
	    name = n;
	    column = this;
	  }

	   public void cover() {
	     removeLeftRight();

	     for (DancingNode i = bottom; i != this; i = i.bottom) {
	       for (DancingNode j = i.right; j != i; j = j.right) {
	         j.removeTopBottom();
	         j.column.size--;
	       }
	     }
	  }

	  public void uncover() {
	    for (DancingNode i = top; i != this; i = i.top) {
	      for (DancingNode j = i.left; j != i; j = j.left) {
	        j.column.size++;
	        j.reinsertTopBottom();
	      }
	    }

	    reinsertLeftRight();
	  }
	}