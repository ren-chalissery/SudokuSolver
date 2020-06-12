/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.KillerSudokuGrid;
import grid.SudokuGrid;

/**
 * Backtracking solver for Killer Sudoku.
 */
public class KillerBackTrackingSolver extends KillerSudokuSolver {
	// TODO: Add attributes as needed.

	public KillerBackTrackingSolver() {
	} // end of KillerBackTrackingSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		KillerSudokuGrid killerGrid = (KillerSudokuGrid) grid;

		int size = killerGrid.size;
		int[] validNumbers = killerGrid.symbols;
		int[][] board = killerGrid.grid;

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] == 0) {

					for (int number : validNumbers) {
						// check if the value satifies all the constraints.
						if (validateToInsert(row, col, number, killerGrid)) {
							board[row][col] = number;

							if (solve(grid)) {
								return true;
							} else {
								board[row][col] = 0;
							}
						}
					}
					return false;
				}
			}
		}
		return true;
	} // end of solve()

	// combined methods to check the constarints before inserting a value
	public boolean validateToInsert(int row, int col, int number, KillerSudokuGrid grid) {
		if (grid.checkNumberIsValid(number) && grid.isInRow(row, number) == 0 && grid.isInCol(col, number) == 0
				&& grid.isInBox(row, col, number) == 0 && grid.isCagesTotalOk(row, col, number)) {
			return true;
		}
		return false;
	}

} // end of class KillerBackTrackingSolver()
