package solver;

public class DancingNode {
	  public DancingNode left, right, top, bottom;
	  public ColumnNode column;
	  public int rowIndex;

	  public DancingNode() {
	    left = right = top = bottom = this;
	  }

	  public DancingNode(ColumnNode c) {
	    this();
	    column = c;
	  }

	  public DancingNode(ColumnNode c, int rowIndex) {
	    this();
	    this.rowIndex = rowIndex;
	    column = c;
	  }
	  
	  public DancingNode linkDown(DancingNode node) {
	    node.bottom = bottom;
	    node.bottom.top = node;
	    node.top = this;
	    bottom = node;
	    return node;
	  }

	  public DancingNode linkRight(DancingNode node) {
	    node.right = right;
	    node.right.left = node;
	    node.left = this;
	    right = node;
	    return node;
	  }

	  public void removeLeftRight() {
	    left.right = right;
	    right.left = left;
	  }

	  public void reinsertLeftRight() {
	    left.right = this;
	    right.left = this;
	  }

	  public void removeTopBottom() {
	    top.bottom = bottom;
	    bottom.top = top;
	  }

	  public void reinsertTopBottom() {
	    top.bottom = this;
	    bottom.top = this;
	  }
	}