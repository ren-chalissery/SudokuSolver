/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import java.util.Arrays;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver {
	// Grid size
	private int gridSize;
	private int maxValue;
	// Box size
	private static final int BOX_SIZE = 3;
	private static final int EMPTY_CELL = 0;
	// 4 constraints : cell, line, column, boxes
	private static final int CONSTRAINTS = 4;
	// Values for each cells
	private static final int MIN_VALUE = 1;
	// Starting index for cover matrix
	private static final int COVER_START_INDEX = 1;

	private int[][] grid;
	private int[][] gridSolved;

	public AlgorXSolver() {
		// TODO: any initialisation you want to implement.
	} // end of AlgorXSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdSudokuGrid = (StdSudokuGrid) grid;
		gridSize = stdSudokuGrid.size;
		maxValue = stdSudokuGrid.size;
		this.grid = new int[gridSize][gridSize];

		for (int i = 0; i < gridSize; i++)
			for (int j = 0; j < gridSize; j++)
				this.grid[i][j] = stdSudokuGrid.grid[i][j];

		int[][] coverMatrix = createCoverMatrix();
		return false;
	} // end of solve()
		// Index in the cover matrix

	private int indexInCoverMatrix(int row, int column, int num) {
		return (row - 1) * gridSize * gridSize + (column - 1) * gridSize + (num - 1);
	}

	// Building of an empty cover matrix
	private int[][] createCoverMatrix() {
		int[][] coverMatrix = new int[gridSize * gridSize * maxValue][gridSize * gridSize * CONSTRAINTS];

		int header = 0;
		header = createCellConstraints(coverMatrix, header);
		header = createRowConstraints(coverMatrix, header);
		header = createColumnConstraints(coverMatrix, header);
		createBoxConstraints(coverMatrix, header);

		return coverMatrix;
	}

	private int createBoxConstraints(int[][] matrix, int header) {
		for (int row = COVER_START_INDEX; row <= gridSize; row += BOX_SIZE) {
			for (int column = COVER_START_INDEX; column <= gridSize; column += BOX_SIZE) {
				for (int n = COVER_START_INDEX; n <= gridSize; n++, header++) {
					for (int rowDelta = 0; rowDelta < BOX_SIZE; rowDelta++) {
						for (int columnDelta = 0; columnDelta < BOX_SIZE; columnDelta++) {
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
		for (int column = COVER_START_INDEX; column <= gridSize; column++) {
			for (int n = COVER_START_INDEX; n <= gridSize; n++, header++) {
				for (int row = COVER_START_INDEX; row <= gridSize; row++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createRowConstraints(int[][] matrix, int header) {
		for (int row = COVER_START_INDEX; row <= gridSize; row++) {
			for (int n = COVER_START_INDEX; n <= gridSize; n++, header++) {
				for (int column = COVER_START_INDEX; column <= gridSize; column++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createCellConstraints(int[][] matrix, int header) {
		for (int row = COVER_START_INDEX; row <= gridSize; row++) {
			for (int column = COVER_START_INDEX; column <= gridSize; column++, header++) {
				for (int n = COVER_START_INDEX; n <= gridSize; n++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	// Converting Sudoku grid as a cover matrix
	private int[][] convertInCoverMatrix(int[][] grid) {
		int[][] coverMatrix = createCoverMatrix();

		// Taking into account the values already entered in Sudoku's grid instance
		for (int row = COVER_START_INDEX; row <= gridSize; row++) {
			for (int column = COVER_START_INDEX; column <= gridSize; column++) {
				int n = grid[row - 1][column - 1];

				if (n != EMPTY_CELL) {
					for (int num = MIN_VALUE; num <= maxValue; num++) {
						if (num != n) {
							Arrays.fill(coverMatrix[indexInCoverMatrix(row, column, num)], 0);
						}
					}
				}
			}
		}

		return coverMatrix;
	}

} // end of class AlgorXSolver
