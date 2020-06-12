/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Dancing links solver for standard Sudoku.
 */
public class DancingLinksSolver extends StdSudokuSolver {
	private int gridSize;
	private ColumnNode header;
	private List<DancingNode> answer;
	public List<DancingNode> result;

	public DancingLinksSolver() {
		answer = new ArrayList<DancingNode>();
	} // end of DancingLinksSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdSudokuGrid = (StdSudokuGrid) grid;
		gridSize = stdSudokuGrid.size;

		CoverMatrix coverMatrix = new CoverMatrix(grid);
		int[][] cover = coverMatrix.convertInCoverMatrix(stdSudokuGrid.grid);
		header = createDLXList(cover);
		process(0);
		if (!result.isEmpty()) {
			stdSudokuGrid.grid = convertDLXListToGrid(result);
			return true;
		}
		return false;
	} // end of solve()

	private ColumnNode selectColumnNodeHeuristic() {
		int min = Integer.MAX_VALUE;
		ColumnNode ret = null;
		for (ColumnNode c = (ColumnNode) header.right; c != header; c = (ColumnNode) c.right) {
			if (c.size < min) {
				min = c.size;
				ret = c;
			}
		}
		return ret;
	}

	private void process(int k) {
		if (header.right == header) {
			// End of Algorithm X
			// Result is copied in a result list
			result = new LinkedList<>(answer);
		} else {
			// we choose column c
			ColumnNode c = selectColumnNodeHeuristic();
			c.cover();

			for (DancingNode r = c.bottom; r != c; r = r.bottom) {
				// We add r line to partial solution
				answer.add(r);

				// We cover columns
				for (DancingNode j = r.right; j != r; j = j.right) {
					j.column.cover();
				}

				// recursive call to leverl k + 1
				process(k + 1);

				// We go back
				r = answer.remove(answer.size() - 1);
				c = r.column;

				// We uncover columns
				for (DancingNode j = r.left; j != r; j = j.left) {
					j.column.uncover();
				}
			}

			c.uncover();
		}
	}

	private ColumnNode createDLXList(int[][] grid) {
		final int nbColumns = grid[0].length;
		ColumnNode headerNode = new ColumnNode("header");
		List<ColumnNode> columnNodes = new ArrayList<>();

		for (int i = 0; i < nbColumns; i++) {
			ColumnNode n = new ColumnNode(i + "");
			columnNodes.add(n);
			headerNode = (ColumnNode) headerNode.linkRight(n);
		}

		headerNode = headerNode.right.column;

		for (int[] aGrid : grid) {
			DancingNode prev = null;

			for (int j = 0; j < nbColumns; j++) {
				if (aGrid[j] == 1) {
					ColumnNode col = columnNodes.get(j);
					DancingNode newNode = new DancingNode(col);

					if (prev == null)
						prev = newNode;

					col.top.linkDown(newNode);
					prev = prev.linkRight(newNode);
					col.size++;
				}
			}
		}

		headerNode.size = nbColumns;

		return headerNode;
	}

	private int[][] convertDLXListToGrid(List<DancingNode> answer) {
		int[][] result = new int[gridSize][gridSize];

		for (DancingNode n : answer) {
			DancingNode rcNode = n;
			int min = Integer.parseInt(rcNode.column.name);

			for (DancingNode tmp = n.right; tmp != n; tmp = tmp.right) {
				int val = Integer.parseInt(tmp.column.name);

				if (val < min) {
					min = val;
					rcNode = tmp;
				}
			}

			// we get line and column
			int ans1 = Integer.parseInt(rcNode.column.name);
			int ans2 = Integer.parseInt(rcNode.right.column.name);
			int r = ans1 / gridSize;
			int c = ans1 % gridSize;
			// and the affected value
			int num = (ans2 % gridSize) + 1;
			// we affect that on the result grid
			result[r][c] = num;
		}

		return result;
	}

} // end of class DancingLinksSolver
