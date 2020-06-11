/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver {
	// Grid size
	private int gridSize;
	private int maxValue;
	private int boxSize;
	private int minValue;

	List<Integer> columnsList;
	List<Integer> rowsList;
	Stack<List<Integer>> coveredRowStack;
	Stack<List<Integer>> coveredColumnStack;
	Stack<Integer> solutionStack;

	public AlgorXSolver() {
		rowsList = new ArrayList<Integer>();
		columnsList = new ArrayList<Integer>();
		coveredRowStack = new Stack<List<Integer>>();
		coveredColumnStack = new Stack<List<Integer>>();
		solutionStack = new Stack<Integer>();
	} // end of AlgorXSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdSudokuGrid = (StdSudokuGrid) grid;
		gridSize = stdSudokuGrid.size;
		boxSize = (int) Math.sqrt(gridSize);
		// TODO: make it dynamic
		minValue = 1;
		maxValue = stdSudokuGrid.size;
		int[][] coverMatrix = createCoverMatrix();
		coverMatrix = convertInCoverMatrix(stdSudokuGrid.grid, coverMatrix);
		if (solveCoverMatrix(coverMatrix)) {
			stdSudokuGrid = convertCoverMatrixToGrid(stdSudokuGrid, solutionStack);
			return true;
		}
		return false;
	} // end of solve()

	private boolean solveCoverMatrix(int[][] coverMatrix) {
		if (rowsList.isEmpty() && columnsList.isEmpty())
			return true;
		if (columnsList.isEmpty())
			return false;
		if (rowsList.isEmpty())
			return false;
		else {
			int c = columnsList.get(0);
			List<Integer> newRow = new ArrayList<>();
			newRow.addAll(rowsList);
			Iterator<Integer> rowItr = newRow.iterator();
			while (rowItr.hasNext()) {
				int r = rowItr.next();
				if (coverMatrix[r][c] == 1) {
					// Cover the overlapping rows and columns
					coverMatrix = coverRowsAndColumns(r, c, coverMatrix);
//					rowsList.remove(Integer.valueOf(r));
					
					// Add to solution row index Array
					solutionStack.push(r);

					// Call the solve matrix recursively
					if (solveCoverMatrix(coverMatrix)) {
						return true;
					}
					// Put back the solution row
					solutionStack.pop();

					// Put back the overlapping rows
					rowsList.addAll(coveredRowStack.pop());

					// Put back the overlapping columns
					columnsList.addAll(coveredColumnStack.pop());
				}
			}
		}
		return false;
	}

	private int[][] coverRowsAndColumns(int sr, int sc, int[][] coverMatrix) {
		List<Integer> rowCoverIndexList = new ArrayList<Integer>();
		List<Integer> colCoverIndexList = new ArrayList<Integer>();
		Iterator<Integer> itrCol = columnsList.iterator();
		while (itrCol.hasNext()) {
			int col = itrCol.next();
			if (coverMatrix[sr][col] == 1) {
				Iterator<Integer> itr = rowsList.iterator();
				while (itr.hasNext()) {
					int row = itr.next();
					if (coverMatrix[row][col] == 1) {
						rowCoverIndexList.add(row);
						itr.remove();
					}
				}
				colCoverIndexList.add(col);
				itrCol.remove();
			}
		}
		coveredRowStack.push(rowCoverIndexList);
		coveredColumnStack.push(colCoverIndexList);
		return coverMatrix;
	}

	// Get solved grid
	private StdSudokuGrid convertCoverMatrixToGrid(StdSudokuGrid grid, Stack<Integer> solution) {
		Iterator<Integer> sIterator = solution.iterator();
		while (sIterator.hasNext()) {
			int data = sIterator.next();
			int i = data / (gridSize * gridSize);
			int j = (data / (gridSize)) % gridSize;
			int value = (data % gridSize);
			grid.grid[i][j] = grid.symbols[value];
		}
		return grid;
	}

	// Index in the cover matrix
	private int indexInCoverMatrix(int row, int column, int num) {
		return (row - 1) * gridSize * gridSize + (column - 1) * gridSize + (num - 1);
	}

	// Building of an empty cover matrix
	private int[][] createCoverMatrix() {
		int[][] coverMatrix = new int[gridSize * gridSize * maxValue][gridSize * gridSize * 4];

		int header = 0;
		header = createRowConstraints(coverMatrix, header);
		header = createColumnConstraints(coverMatrix, header);
		header = createCellConstraints(coverMatrix, header);
		createBoxConstraints(coverMatrix, header);

		return coverMatrix;
	}

	private int createBoxConstraints(int[][] matrix, int header) {
		for (int row = 1; row <= gridSize; row += boxSize) {
			for (int column = 1; column <= gridSize; column += boxSize) {
				for (int n = 1; n <= gridSize; n++, columnsList.add(header), header++) {
					for (int rowDelta = 0; rowDelta < boxSize; rowDelta++) {
						for (int columnDelta = 0; columnDelta < boxSize; columnDelta++) {
							int index = indexInCoverMatrix(row + rowDelta, column + columnDelta, n);
							matrix[index][header] = 1;
						}
					}
				}
			}
		}

		return header;
	}

	private int createColumnConstraints(int[][] matrix, int header) {
		for (int column = 1; column <= gridSize; column++) {
			for (int n = 1; n <= gridSize; n++, columnsList.add(header), header++) {
				for (int row = 1; row <= gridSize; row++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
					rowsList.add(index);
				}
			}
		}

		return header;
	}

	private int createRowConstraints(int[][] matrix, int header) {
		for (int row = 1; row <= gridSize; row++) {
			for (int n = 1; n <= gridSize; n++, columnsList.add(header), header++) {
				for (int column = 1; column <= gridSize; column++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createCellConstraints(int[][] matrix, int header) {
		for (int row = 1; row <= gridSize; row++) {
			for (int column = 1; column <= gridSize; column++, columnsList.add(header), header++) {
				for (int n = 1; n <= gridSize; n++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	// Converting Sudoku grid as a cover matrix
	private int[][] convertInCoverMatrix(int[][] grid, int[][] coverMatrix) {

		// Taking into account the values already entered in Sudoku's grid instance
		for (int row = 1; row <= gridSize; row++) {
			for (int column = 1; column <= gridSize; column++) {
				int n = grid[row - 1][column - 1];

				if (n != 0) {
					for (int num = minValue; num <= maxValue; num++) {
						if (num == n) {
							coverRowsAndColumns(indexInCoverMatrix(row, column, num), column, coverMatrix);
						}
					}
				}
			}
		}

		return coverMatrix;
	}

} // end of class AlgorXSolver
