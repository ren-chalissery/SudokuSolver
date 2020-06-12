package solver;

import java.util.Arrays;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

public class CoverMatrix {

	private int[][] grid;
	private int gridSize;
	private int boxSize;
	private int maxValue;
	private int minValue;

	public CoverMatrix(SudokuGrid sudokuGrid) {
		gridSize = sudokuGrid.size;
		boxSize = (int) Math.sqrt(gridSize);
		minValue = 1;
		maxValue = sudokuGrid.size;
		this.grid = new int[gridSize][gridSize];

		for (int i = 0; i < gridSize; i++)
			for (int j = 0; j < gridSize; j++)
				this.grid[i][j] = grid[i][j];
	}

// ...

	// Index in the cover matrix
	private int indexInCoverMatrix(int row, int column, int num) {
		return (row - 1) * gridSize * gridSize + (column - 1) * gridSize + (num - 1);
	}

	// Building of an empty cover matrix
	public int[][] createCoverMatrix() {
		int[][] coverMatrix = new int[gridSize * gridSize * maxValue][gridSize * gridSize * 4];

		int header = 0;
		header = createCell4(coverMatrix, header);
		header = createRow4(coverMatrix, header);
		header = createColumn4(coverMatrix, header);
		createBox4(coverMatrix, header);

		return coverMatrix;
	}

	private int createBox4(int[][] matrix, int header) {
		for (int row = 1; row <= gridSize; row += boxSize) {
			for (int column = 1; column <= gridSize; column += boxSize) {
				for (int n = 1; n <= gridSize; n++, header++) {
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

	private int createColumn4(int[][] matrix, int header) {
		for (int column = 1; column <= gridSize; column++) {
			for (int n = 1; n <= gridSize; n++, header++) {
				for (int row = 1; row <= gridSize; row++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createRow4(int[][] matrix, int header) {
		for (int row = 1; row <= gridSize; row++) {
			for (int n = 1; n <= gridSize; n++, header++) {
				for (int column = 1; column <= gridSize; column++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createCell4(int[][] matrix, int header) {
		for (int row = 1; row <= gridSize; row++) {
			for (int column = 1; column <= gridSize; column++, header++) {
				for (int n = 1; n <= gridSize; n++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	// Converting Sudoku grid as a cover matrix
	public int[][] convertInCoverMatrix(int[][] grid) {
		int[][] coverMatrix = createCoverMatrix();

		// Taking into account the values already entered in Sudoku's grid instance
		for (int row = 1; row <= gridSize; row++) {
			for (int column = 1; column <= gridSize; column++) {
				int n = grid[row - 1][column - 1];

				if (n != 0) {
					for (int num = minValue; num <= maxValue; num++) {
						if (num != n) {
							Arrays.fill(coverMatrix[indexInCoverMatrix(row, column, num)], 0);
						}
					}
				}
			}
		}

		return coverMatrix;
	}

// ...
}
