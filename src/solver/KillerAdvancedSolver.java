/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import grid.KillerSudokuGrid;
import grid.StdSudokuGrid;
import grid.SudokuGrid;
import grid.KillerSudokuGrid.CagePair;
import grid.KillerSudokuGrid.Cell;

/**
 * Your advanced solver for Killer Sudoku.
 */
public class KillerAdvancedSolver extends KillerSudokuSolver {
	private int gridSize;
	private ColumnNode header;
	private List<DancingNode> answer;
	public List<DancingNode> result;
	public ArrayList<CagePair> cagePairList;
	public int[][] grid;
	public int[] symbols;

	public KillerAdvancedSolver() {
		answer = new ArrayList<DancingNode>();
		cagePairList = new ArrayList<CagePair>();
	} // end of KillerAdvancedSolver()

	@Override
	public boolean solve(SudokuGrid sudokuGrid) {
		KillerSudokuGrid killerGrid = (KillerSudokuGrid) sudokuGrid;
		gridSize = killerGrid.size;
		cagePairList = killerGrid.cagePairList;
		grid = killerGrid.grid;
		symbols = killerGrid.symbols;
		
		CoverMatrix coverMatrix = new CoverMatrix(sudokuGrid);
		int[][] cover = coverMatrix.createCoverMatrix();
		header = createDLXList(cover);
		process(0);
		if (!result.isEmpty()) {
			killerGrid.grid = convertDLXListToGrid(result);
			return true;
		}
		return false;
	} // end of solve()

	private boolean process(int k) {
		if (header.right == header) {
			// End of Algorithm X
			// Result is copied in a result list
			result = new LinkedList<>(answer);
			return true;
		} else {
			// we choose column c
			ColumnNode c = (ColumnNode) header.right;
			c.cover();

			for (DancingNode r = c.bottom; r != c; r = r.bottom) {

//				int[] rcn = getRowColNum(r);
				int rowIndexNo = r.rowIndex;              
				int row = rowIndexNo/(gridSize*gridSize);        
				int col = (rowIndexNo/(gridSize))%gridSize;
				int value = (rowIndexNo%gridSize);
				grid[row][col] = symbols[value];
				int gridLen = grid.length;
//				StringBuilder sb = new StringBuilder();
//
//				for (int i = 0; i < gridLen; i++) {
//					for (int j = 0; j < gridLen; j++) {
//						// last column not need to attach comma.
//						if (j == gridLen - 1)
//							sb.append(grid[i][j]);
//						else
//							sb.append(grid[i][j] + ",");
//					}
//					sb.append('\n');
//				}
//				System.out.println(sb.toString());
				if (validateCagePair()) {
					// We add r line to partial solution
					answer.add(r);

					// We cover columns
					for (DancingNode j = r.right; j != r; j = j.right) {
						j.column.cover();
					}
					// recursive call to leverl k + 1
					if(process(k + 1)) {
						return true;
					}

					// We go back
					r = answer.remove(answer.size() - 1);
					c = r.column;

					// We uncover columns
					for (DancingNode j = r.left; j != r; j = j.left) {
						j.column.uncover();
					}
				}
				grid[row][col] = 0;
			}

			c.uncover();
		}
		return false;
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

		for (int i = 0; i < grid.length; i++) {
//		for (int[] aGrid : grid) {

			DancingNode prev = null;

			for (int j = 0; j < nbColumns; j++) {
				if (grid[i][j] == 1) {
					ColumnNode col = columnNodes.get(j);
					DancingNode newNode = new DancingNode(col, i);

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

	private int[] getRowColNum(DancingNode n) {
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

		int[] values = { r, c, num };
		return values;

	}

	// check if it satisfies cage constraint
	private boolean validateCagePair() {
//		boolean isCellFound = false;

		for (CagePair cagePair : cagePairList) {
			ArrayList<Cell> cells = cagePair.cells;

			// the expected total of the cage pairs.
			int cellsExpectedTotal = cagePair.total;

			// stores the total values of cage pairs.
			int cellsCurrentTotal = 0;
			boolean isCagesPartiallyFilled = false;

			for (Cell cell : cells) {
//				if (cell.row == row && cell.col == col) {
//					cellsCurrentTotal += number;
//					isCellFound = true;
//				} else {
				// check if any cell's value is 0.
				int cellValue = grid[cell.row][cell.col];
				if (cellValue == 0)
					isCagesPartiallyFilled = true;

				cellsCurrentTotal += cellValue;
//				}
			}

			// check if the existing cell satisfies the constraint.
			// less is when all the cages are partially filled
//			if (isCellFound) {
			if (isCagesPartiallyFilled) {
//					return cellsCurrentTotal < cellsExpectedTotal;
				if (cellsCurrentTotal >= cellsExpectedTotal) {
					return false;
				}
			} else {
				if (cellsCurrentTotal != cellsExpectedTotal) {
					return false;
				}
			}

//			}
		}

		return true;
	}

} // end of class KillerAdvancedSolver
